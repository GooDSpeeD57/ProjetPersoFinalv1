package com.monprojet.boutiquejeux.controller;

import com.monprojet.boutiquejeux.dto.api.catalog.ApiProduitDetail;
import com.monprojet.boutiquejeux.dto.api.client.ApiAdresse;
import com.monprojet.boutiquejeux.dto.api.client.ApiBonAchat;
import com.monprojet.boutiquejeux.dto.api.client.ApiClient;
import com.monprojet.boutiquejeux.dto.api.magasin.ApiMagasin;
import com.monprojet.boutiquejeux.dto.api.facture.ApiFactureDetail;
import com.monprojet.boutiquejeux.dto.api.magasin.ApiMagasinProche;
import com.monprojet.boutiquejeux.dto.api.panier.ApiLignePanier;
import com.monprojet.boutiquejeux.dto.api.panier.ApiPanier;
import com.monprojet.boutiquejeux.dto.api.stock.ApiStockCheckout;
import com.monprojet.boutiquejeux.dto.api.stock.ApiStockEntrepotCheckout;
import com.monprojet.boutiquejeux.dto.CartItem;
import com.monprojet.boutiquejeux.service.ApiService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/panier")
@RequiredArgsConstructor
public class PanierController {

    private final ApiService api;

    @GetMapping
    String panier(HttpSession session, Model model) {
        String jwt = (String) session.getAttribute("jwt");
        if (jwt != null) {
            ApiPanier panier = api.getPanier(jwt);
            model.addAttribute("apiPanier", panier);
            session.setAttribute("cartCount", extractCartCountApi(panier));
            BigDecimal totalGaranties = panier == null || panier.lignes() == null ? BigDecimal.ZERO :
                panier.lignes().stream()
                    .filter(l -> l.garantiePrix() != null)
                    .map(com.monprojet.boutiquejeux.dto.api.panier.ApiLignePanier::garantiePrix)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            model.addAttribute("totalGaranties", totalGaranties);
        } else {
            List<CartItem> panier = getCart(session);
            BigDecimal total = panier.stream()
                    .map(i -> {
                        BigDecimal ligne = i.getPrix().multiply(BigDecimal.valueOf(i.getQuantite()));
                        if (i.getGarantiePrix() != null)
                            ligne = ligne.add(i.getGarantiePrix());
                        return ligne;
                    })
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            int totalQte = panier.stream().mapToInt(CartItem::getQuantite).sum();
            model.addAttribute("panier", panier);
            model.addAttribute("totalPrix", total);
            model.addAttribute("totalQuantite", totalQte);
        }
        return "panier/index";
    }

    @GetMapping("/checkout")
    String checkout(HttpSession session, Model model, RedirectAttributes redirect) {
        String jwt = (String) session.getAttribute("jwt");
        if (jwt == null) {
            redirect.addFlashAttribute("errorMessage", "Connectez-vous pour finaliser votre commande");
            return "redirect:/auth/login";
        }

        ApiPanier panier = api.getPanier(jwt);
        if (panier == null || panier.lignes() == null || panier.lignes().isEmpty()) {
            redirect.addFlashAttribute("errorMessage", "Votre panier est vide");
            return "redirect:/panier";
        }

        List<ApiAdresse> adresses = new ArrayList<>(api.getClientAdresses(jwt));
        adresses.sort(Comparator.comparing(ApiAdresse::estDefaut).reversed());

        List<ApiBonAchat> bonsDisponibles = api.getClientBonsAchat(jwt).stream()
            .filter(bon -> !bon.utilise())
            .toList();

        Long defaultAdresseId = adresses.stream().filter(ApiAdresse::estDefaut).map(ApiAdresse::id).findFirst()
                .orElse(adresses.stream().map(ApiAdresse::id).findFirst().orElse(null));

        // Récupère jusqu'à 12 magasins proches par adresse (plus de choix pour le filtre stock)
        Map<Long, List<ApiMagasinProche>> magasinsProchesParAdresse = new LinkedHashMap<>();
        for (ApiAdresse adresse : adresses) {
            magasinsProchesParAdresse.put(adresse.id(), api.getMagasinsProches(jwt, adresse.id(), 12));
        }

        // Calcule le stock disponible par magasin ET entrepôt pour chaque variant du panier
        Map<Long, Map<Long, Integer>> stockParVariant = new HashMap<>();
        // stockEntrepot : idVariant -> quantite totale dispo en entrepôt
        Map<Long, Integer> stockEntrepotParVariant = new HashMap<>();
        // ligneStockOk : idVariant -> true si entrepôt peut couvrir la quantité commandée
        Map<Long, Boolean> ligneEntrepotOk = new HashMap<>();

        for (ApiLignePanier ligne : panier.lignes()) {
            // Stock magasin (pour retrait)
            List<ApiStockCheckout> stocks = api.getStockCheckout(jwt, ligne.idVariant());
            Map<Long, Integer> stockParMagasin = stocks.stream()
                    .collect(Collectors.toMap(ApiStockCheckout::idMagasin, ApiStockCheckout::quantiteDisponible));
            stockParVariant.put(ligne.idVariant(), stockParMagasin);

            // Stock entrepôt (pour livraison domicile)
            ApiStockEntrepotCheckout stockEntrepot = api.getStockEntrepotCheckout(jwt, ligne.idVariant());
            int dispoEntrepot = stockEntrepot != null ? stockEntrepot.quantiteTotaleDisponible() : 0;
            stockEntrepotParVariant.put(ligne.idVariant(), dispoEntrepot);
            ligneEntrepotOk.put(ligne.idVariant(), dispoEntrepot >= ligne.quantite());
        }

        // Stock entrepôt global : true si TOUS les articles sont dispo en entrepôt
        boolean tousArticlesEnEntrepot = panier.lignes().stream()
                .allMatch(l -> Boolean.TRUE.equals(ligneEntrepotOk.get(l.idVariant())));

        // Pour chaque magasin proche : a-t-il le stock suffisant pour TOUS les articles ?
        Set<Long> allMagasinIds = magasinsProchesParAdresse.values().stream()
                .flatMap(List::stream).map(ApiMagasinProche::id).collect(Collectors.toSet());
        Map<Long, Boolean> magasinStockOk = new HashMap<>();
        for (Long magasinId : allMagasinIds) {
            boolean ok = panier.lignes().stream().allMatch(ligne -> {
                Map<Long, Integer> stockParMagasin = stockParVariant.get(ligne.idVariant());
                int dispo = stockParMagasin != null ? stockParMagasin.getOrDefault(magasinId, 0) : 0;
                return dispo >= ligne.quantite();
            });
            magasinStockOk.put(magasinId, ok);
        }

        // Tri : magasins en stock d'abord, puis hors stock (pour chaque adresse)
        Map<Long, List<ApiMagasinProche>> magasinsTries = new LinkedHashMap<>();
        for (Map.Entry<Long, List<ApiMagasinProche>> entry : magasinsProchesParAdresse.entrySet()) {
            List<ApiMagasinProche> tries = entry.getValue().stream()
                    .sorted(Comparator.<ApiMagasinProche, Boolean>comparing(
                            m -> !Boolean.TRUE.equals(magasinStockOk.get(m.id())))
                    )
                    .toList();
            magasinsTries.put(entry.getKey(), tries);
        }

        // Magasin par défaut : le 1er en stock pour l'adresse par défaut
        Long defaultMagasinRetraitId = null;
        if (defaultAdresseId != null) {
            List<ApiMagasinProche> magasinsDefaut = magasinsTries.get(defaultAdresseId);
            if (magasinsDefaut != null) {
                defaultMagasinRetraitId = magasinsDefaut.stream()
                        .filter(m -> Boolean.TRUE.equals(magasinStockOk.get(m.id())))
                        .map(ApiMagasinProche::id)
                        .findFirst()
                        .orElse(magasinsDefaut.isEmpty() ? null : magasinsDefaut.getFirst().id());
            }
        }

        BigDecimal totalGaranties = panier.lignes() == null ? BigDecimal.ZERO :
            panier.lignes().stream()
                .filter(l -> l.garantiePrix() != null)
                .map(com.monprojet.boutiquejeux.dto.api.panier.ApiLignePanier::garantiePrix)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Magasin favori du client + liste complète pour le sélecteur
        ApiClient clientMe = api.getClientMe(jwt);
        ApiClient.ApiMagasinFavori magasinFavori = clientMe != null ? clientMe.magasinFavori() : null;
        List<ApiMagasin> tousMagasins = api.getMagasins(null);

        model.addAttribute("apiPanier", panier);
        model.addAttribute("adresses", adresses);
        model.addAttribute("magasinsProchesParAdresse", magasinsTries);
        model.addAttribute("magasinStockOk", magasinStockOk);
        model.addAttribute("ligneEntrepotOk", ligneEntrepotOk);
        model.addAttribute("stockEntrepotParVariant", stockEntrepotParVariant);
        model.addAttribute("tousArticlesEnEntrepot", tousArticlesEnEntrepot);
        model.addAttribute("bonsDisponibles", bonsDisponibles);
        model.addAttribute("defaultAdresseId", defaultAdresseId);
        model.addAttribute("defaultMagasinRetraitId", defaultMagasinRetraitId);
        model.addAttribute("magasinFavori", magasinFavori);
        model.addAttribute("tousMagasins", tousMagasins);
        model.addAttribute("modePaiementCode", "CB");
        model.addAttribute("modeLivraisonCode", "DOMICILE");
        model.addAttribute("totalGaranties", totalGaranties);
        return "panier/checkout";
    }

    @GetMapping("/confirmation/{idFacture}")
    String confirmation(@PathVariable Long idFacture, HttpSession session, Model model, RedirectAttributes redirect) {
        String jwt = (String) session.getAttribute("jwt");
        if (jwt == null) {
            return "redirect:/auth/login";
        }
        try {
            ApiFactureDetail facture = api.getClientFacture(jwt, idFacture);
            model.addAttribute("facture", facture);
            return "panier/confirmation";
        } catch (RuntimeException e) {
            redirect.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/compte";
        }
    }

    @PostMapping("/ajouter-ajax")
    @ResponseBody
    ResponseEntity<Map<String, Object>> ajouterAjax(
            @RequestParam Long produitId,
            @RequestParam String produitNom,
            @RequestParam(required = false) Long idVariant,
            @RequestParam(required = false) BigDecimal prix,
            @RequestParam(required = false) String garantieLabel,
            @RequestParam(required = false) BigDecimal garantiePrix,
            @RequestParam(required = false) Long typeGarantieId,
            HttpSession session) {

        String jwt = (String) session.getAttribute("jwt");
        Long variantId = idVariant;

        if (variantId == null) {
            try {
                ApiProduitDetail produit = api.getProduitDetail(produitId);
                if (produit == null || produit.variants() == null || produit.variants().isEmpty()) {
                    return ResponseEntity.ok(Map.of("success", false, "message", "Aucune variante disponible"));
                }
                variantId = produit.variants().getFirst().id();
            } catch (RuntimeException e) {
                return ResponseEntity.ok(Map.of("success", false, "message", "Impossible de résoudre la variante"));
            }
        }

        final Long resolvedVariantId = variantId;

        if (jwt != null) {
            try {
                ApiPanier panier = api.addLignePanier(jwt, resolvedVariantId, 1, typeGarantieId);
                int count = extractCartCountApi(panier);
                session.setAttribute("cartCount", count);
                return ResponseEntity.ok(Map.of("success", true, "message", produitNom + " ajouté au panier ✓", "cartCount", count));
            } catch (RuntimeException e) {
                if (isUnauthorized(e)) {
                    clearWebAuthSession(session);
                    return ResponseEntity.ok(Map.of("success", false, "message", "Session expirée, reconnectez-vous"));
                }
                return ResponseEntity.ok(Map.of("success", false, "message", e.getMessage() != null ? e.getMessage() : "Erreur"));
            }
        }

        List<CartItem> panier = getCart(session);
        CartItem existing = panier.stream().filter(i -> resolvedVariantId.equals(i.getVariantId())).findFirst().orElse(null);
        BigDecimal prixSafe = prix != null ? prix : BigDecimal.ZERO;
        boolean avecGarantie = garantieLabel != null && !garantieLabel.isBlank();
        if (existing != null) {
            existing.setQuantite(existing.getQuantite() + 1);
            if (avecGarantie) {
                existing.setGarantieLabel(garantieLabel);
                existing.setGarantiePrix(garantiePrix);
                existing.setTypeGarantieId(typeGarantieId);
            }
        } else {
            panier.add(new CartItem(produitId, resolvedVariantId, produitNom, prixSafe, 1,
                    avecGarantie ? garantieLabel  : null,
                    avecGarantie ? garantiePrix   : null,
                    avecGarantie ? typeGarantieId : null));
        }
        saveCart(session, panier);
        int count = panier.stream().mapToInt(CartItem::getQuantite).sum();
        return ResponseEntity.ok(Map.of("success", true, "message", produitNom + " ajouté au panier ✓", "cartCount", count));
    }

    @PostMapping("/ajouter")
    String ajouter(@RequestParam Long produitId,
                   @RequestParam String produitNom,
                   @RequestParam(required = false) Long idVariant,
                   @RequestParam(required = false) BigDecimal prix,
                   @RequestParam(required = false) String garantieLabel,
                   @RequestParam(required = false) BigDecimal garantiePrix,
                   @RequestParam(required = false) Long typeGarantieId,
                   HttpSession session,
                   RedirectAttributes redirect) {

        String jwt = (String) session.getAttribute("jwt");

        Long variantId = idVariant;
        if (variantId == null) {
            try {
                ApiProduitDetail produit = api.getProduitDetail(produitId);
                if (produit == null || produit.variants() == null || produit.variants().isEmpty()) {
                    redirect.addFlashAttribute("errorMessage", "Aucune variante disponible pour ce produit");
                    return "redirect:/catalogue/" + produitId;
                }
                variantId = produit.variants().getFirst().id();
            } catch (RuntimeException e) {
                redirect.addFlashAttribute("errorMessage", "Impossible de déterminer la variante du produit");
                return "redirect:/catalogue/" + produitId;
            }
        }

        final Long resolvedVariantId = variantId;

        if (jwt != null) {
            try {
                ApiPanier panier = api.addLignePanier(jwt, resolvedVariantId, 1, typeGarantieId);
                session.setAttribute("cartCount", extractCartCountApi(panier));
                redirect.addFlashAttribute("successMessage", produitNom + " ajouté au panier");
                return "redirect:/panier";
            } catch (RuntimeException e) {
                if (isUnauthorized(e)) {
                    clearWebAuthSession(session);
                } else {
                    redirect.addFlashAttribute("errorMessage", e.getMessage());
                    return "redirect:/catalogue/" + produitId;
                }
            }
        }

        List<CartItem> panier = getCart(session);

        CartItem existing = panier.stream()
                .filter(i -> resolvedVariantId.equals(i.getVariantId()))
                .findFirst()
                .orElse(null);

        BigDecimal prixSafe = prix != null ? prix : BigDecimal.ZERO;

        if (existing != null) {
            existing.setQuantite(existing.getQuantite() + 1);
            // Mettre à jour la garantie si re-ajout avec garantie
            if (garantieLabel != null && !garantieLabel.isBlank()) {
                existing.setGarantieLabel(garantieLabel);
                existing.setGarantiePrix(garantiePrix);
                existing.setTypeGarantieId(typeGarantieId);
            }
        } else {
            boolean avecGarantie = garantieLabel != null && !garantieLabel.isBlank();
            panier.add(new CartItem(produitId, resolvedVariantId, produitNom, prixSafe, 1,
                    avecGarantie ? garantieLabel   : null,
                    avecGarantie ? garantiePrix    : null,
                    avecGarantie ? typeGarantieId  : null));
        }

        saveCart(session, panier);
        redirect.addFlashAttribute("successMessage", produitNom + " ajouté au panier");
        return "redirect:/panier";
    }

    @PostMapping("/quantite")
    String quantite(@RequestParam Long produitId,
                    @RequestParam int quantite,
                    @RequestParam(required = false) Long idLigne,
                    HttpSession session,
                    RedirectAttributes redirect) {
        String jwt = (String) session.getAttribute("jwt");
        if (jwt != null && idLigne != null) {
            try {
                ApiPanier panier;
                if (quantite <= 0) {
                    panier = api.removeLignePanier(jwt, idLigne);
                } else {
                    panier = api.updateLignePanier(jwt, idLigne, quantite);
                }
                session.setAttribute("cartCount", extractCartCountApi(panier));
            } catch (RuntimeException e) {
                redirect.addFlashAttribute("errorMessage", e.getMessage());
            }
            return "redirect:/panier";
        }

        List<CartItem> panier = getCart(session);
        panier.stream().filter(i -> i.getVariantId() != null
                        ? i.getVariantId().equals(produitId)
                        : i.getProduitId().equals(produitId))
                .findFirst()
                .ifPresent(i -> {
                    if (quantite <= 0) panier.remove(i); else i.setQuantite(quantite);
                });
        saveCart(session, panier);
        return "redirect:/panier";
    }

    @PostMapping("/retirer")
    String retirer(@RequestParam Long produitId,
                   @RequestParam(required = false) Long idLigne,
                   HttpSession session,
                   RedirectAttributes redirect) {
        String jwt = (String) session.getAttribute("jwt");
        if (jwt != null && idLigne != null) {
            try {
                ApiPanier panier = api.removeLignePanier(jwt, idLigne);
                session.setAttribute("cartCount", extractCartCountApi(panier));
            } catch (RuntimeException e) {
                redirect.addFlashAttribute("errorMessage", e.getMessage());
            }
            return "redirect:/panier";
        }
        List<CartItem> panier = getCart(session);
        panier.removeIf(i -> i.getVariantId() != null
                ? i.getVariantId().equals(produitId)
                : i.getProduitId().equals(produitId));
        saveCart(session, panier);
        return "redirect:/panier";
    }

    @PostMapping("/vider")
    String vider(HttpSession session, RedirectAttributes redirect) {
        String jwt = (String) session.getAttribute("jwt");
        if (jwt != null) {
            try {
                api.clearPanier(jwt);
                session.setAttribute("cartCount", 0);
            } catch (RuntimeException e) {
                redirect.addFlashAttribute("errorMessage", e.getMessage());
            }
            return "redirect:/panier";
        }
        saveCart(session, new ArrayList<>());
        return "redirect:/panier";
    }

    @PostMapping("/valider")
    String valider(@RequestParam(required = false) Long idAdresse,
                   @RequestParam(required = false) List<Long> idsBonAchat,
                   @RequestParam(defaultValue = "CB") String modePaiementCode,
                   @RequestParam(defaultValue = "DOMICILE") String modeLivraisonCode,
                   @RequestParam(required = false) Long idMagasinRetrait,
                   @RequestParam(required = false) Long idMagasinLivraison,
                   HttpSession session,
                   RedirectAttributes redirect) {
        String jwt = (String) session.getAttribute("jwt");
        if (jwt == null) {
            redirect.addFlashAttribute("errorMessage", "Connectez-vous pour finaliser votre commande");
            return "redirect:/auth/login";
        }
        try {
            if (idAdresse == null) {
                throw new IllegalStateException("Choisissez une adresse pour la livraison ou le retrait.");
            }
            if ("RETRAIT_MAGASIN".equals(modeLivraisonCode) && idMagasinRetrait == null) {
                throw new IllegalStateException("Choisissez un magasin de retrait proche de votre adresse.");
            }
            if ("LIVRAISON_MAGASIN".equals(modeLivraisonCode)) {
                if (idMagasinLivraison == null) {
                    throw new IllegalStateException("Choisissez un magasin pour la livraison en magasin.");
                }
                idMagasinRetrait = idMagasinLivraison;
            }

            ApiFactureDetail facture = api.checkoutPanier(jwt, idAdresse, idsBonAchat, modePaiementCode, modeLivraisonCode, idMagasinRetrait);
            session.setAttribute("cartCount", 0);
            redirect.addFlashAttribute("successMessage", "Commande validée — facture " + facture.referenceFacture());
            return "redirect:/panier/confirmation/" + facture.id();
        } catch (RuntimeException e) {
            redirect.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/panier/checkout";
        }
    }

    private int extractCartCountApi(ApiPanier panier) {
        if (panier == null || panier.lignes() == null) return 0;
        return panier.lignes().stream().mapToInt(l -> l.quantite()).sum();
    }

    @SuppressWarnings("unchecked")
    private List<CartItem> getCart(HttpSession session) {
        Object cart = session.getAttribute("cart");
        if (cart instanceof List<?> list) {
            return (List<CartItem>) list;
        }
        List<CartItem> panier = new ArrayList<>();
        session.setAttribute("cart", panier);
        return panier;
    }

    private void saveCart(HttpSession session, List<CartItem> cart) {
        session.setAttribute("cart", cart);
        session.setAttribute("cartCount", cart.stream().mapToInt(CartItem::getQuantite).sum());
    }

    private boolean isUnauthorized(RuntimeException e) {
        return e.getMessage() != null && e.getMessage().contains("401");
    }

    private void clearWebAuthSession(HttpSession session) {
        session.removeAttribute("jwt");
        session.removeAttribute("userEmail");
        session.removeAttribute("userPseudo");
        session.removeAttribute("userTypeFidelite");
        session.setAttribute("cartCount", 0);
    }
}
