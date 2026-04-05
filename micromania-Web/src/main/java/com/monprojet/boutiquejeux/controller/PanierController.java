package com.monprojet.boutiquejeux.controller;

import com.monprojet.boutiquejeux.dto.api.catalog.ApiProduitDetail;
import com.monprojet.boutiquejeux.dto.api.client.ApiAdresse;
import com.monprojet.boutiquejeux.dto.api.client.ApiBonAchat;
import com.monprojet.boutiquejeux.dto.api.facture.ApiFactureDetail;
import com.monprojet.boutiquejeux.dto.api.magasin.ApiMagasinProche;
import com.monprojet.boutiquejeux.dto.api.panier.ApiPanier;
import com.monprojet.boutiquejeux.dto.CartItem;
import com.monprojet.boutiquejeux.service.ApiService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
        } else {
            List<CartItem> panier = getCart(session);
            BigDecimal total = panier.stream()
                    .map(i -> i.getPrix().multiply(BigDecimal.valueOf(i.getQuantite())))
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

        Map<Long, List<ApiMagasinProche>> magasinsProchesParAdresse = new LinkedHashMap<>();
        for (ApiAdresse adresse : adresses) {
            magasinsProchesParAdresse.put(adresse.id(), api.getMagasinsProches(jwt, adresse.id()));
        }

        Long defaultMagasinRetraitId = null;
        if (defaultAdresseId != null) {
            List<ApiMagasinProche> magasinsDefaut = magasinsProchesParAdresse.get(defaultAdresseId);
            if (magasinsDefaut != null && !magasinsDefaut.isEmpty()) {
                defaultMagasinRetraitId = magasinsDefaut.getFirst().id();
            }
        }

        model.addAttribute("apiPanier", panier);
        model.addAttribute("adresses", adresses);
        model.addAttribute("magasinsProchesParAdresse", magasinsProchesParAdresse);
        model.addAttribute("bonsDisponibles", bonsDisponibles);
        model.addAttribute("defaultAdresseId", defaultAdresseId);
        model.addAttribute("defaultMagasinRetraitId", defaultMagasinRetraitId);
        model.addAttribute("modePaiementCode", "CB");
        model.addAttribute("modeLivraisonCode", "DOMICILE");
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

    @PostMapping("/ajouter")
    String ajouter(@RequestParam Long produitId,
                   @RequestParam String produitNom,
                   @RequestParam(required = false) Long idVariant,
                   @RequestParam(required = false) BigDecimal prix,
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
                ApiPanier panier = api.addLignePanier(jwt, resolvedVariantId, 1);
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
        } else {
            panier.add(new CartItem(produitId, resolvedVariantId, produitNom, prixSafe, 1));
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
                   @RequestParam(required = false) Long idBonAchat,
                   @RequestParam(defaultValue = "CB") String modePaiementCode,
                   @RequestParam(defaultValue = "DOMICILE") String modeLivraisonCode,
                   @RequestParam(required = false) Long idMagasinRetrait,
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

            ApiFactureDetail facture = api.checkoutPanier(jwt, idAdresse, idBonAchat, modePaiementCode, modeLivraisonCode, idMagasinRetrait);
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
        session.removeAttribute("userPseudo");
        session.removeAttribute("userTypeFidelite");
        session.setAttribute("cartCount", 0);
    }
}
