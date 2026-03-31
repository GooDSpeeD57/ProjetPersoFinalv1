package com.monprojet.boutiquejeux.controller;

import com.monprojet.boutiquejeux.dto.CartItem;
import com.monprojet.boutiquejeux.dto.api.catalog.ApiProduitDetail;
import com.monprojet.boutiquejeux.dto.api.panier.ApiPanier;
import com.monprojet.boutiquejeux.service.ApiService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

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

    @PostMapping("/ajouter")
    String ajouter(@RequestParam Long produitId,
                   @RequestParam String produitNom,
                   @RequestParam(required = false) BigDecimal prix,
                   HttpSession session,
                   RedirectAttributes redirect) {

        String jwt = (String) session.getAttribute("jwt");
        if (jwt != null) {
            try {
                ApiProduitDetail produit = api.getProduitDetail(produitId);
                if (produit == null || produit.variants() == null || produit.variants().isEmpty()) {
                    redirect.addFlashAttribute("errorMessage", "Aucune variante disponible pour ce produit");
                    return "redirect:/catalogue/" + produitId;
                }
                Long idVariant = produit.variants().getFirst().id();
                ApiPanier panier = api.addLignePanier(jwt, idVariant, 1);
                session.setAttribute("cartCount", extractCartCountApi(panier));
                redirect.addFlashAttribute("successMessage", produitNom + " ajouté au panier");
                return "redirect:/panier";
            } catch (RuntimeException e) {
                redirect.addFlashAttribute("errorMessage", e.getMessage());
                return "redirect:/catalogue/" + produitId;
            }
        }

        List<CartItem> panier = getCart(session);
        CartItem existing = panier.stream()
                .filter(i -> i.getProduitId().equals(produitId))
                .findFirst().orElse(null);
        BigDecimal prixSafe = prix != null ? prix : BigDecimal.ZERO;
        if (existing != null) {
            existing.setQuantite(existing.getQuantite() + 1);
        } else {
            panier.add(new CartItem(produitId, produitNom, prixSafe, 1));
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
        panier.stream().filter(i -> i.getProduitId().equals(produitId)).findFirst().ifPresent(i -> {
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
        panier.removeIf(i -> i.getProduitId().equals(produitId));
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
    String valider(HttpSession session, RedirectAttributes redirect) {
        String jwt = (String) session.getAttribute("jwt");
        if (jwt == null) {
            redirect.addFlashAttribute("errorMessage", "Connectez-vous pour finaliser votre commande");
            return "redirect:/auth/login";
        }
        redirect.addFlashAttribute("infoMessage", "Le checkout n'est pas encore branché sur votre API actuelle.");
        return "redirect:/compte";
    }

    private int extractCartCountApi(ApiPanier panier) {
        if (panier == null || panier.lignes() == null) return 0;
        return panier.lignes().stream().mapToInt(l -> l.quantite()).sum();
    }

    @SuppressWarnings("unchecked")
    private List<CartItem> getCart(HttpSession session) {
        Object cart = session.getAttribute("cart");
        if (cart instanceof List<?>) return (List<CartItem>) cart;
        return new ArrayList<>();
    }

    private void saveCart(HttpSession session, List<CartItem> panier) {
        session.setAttribute("cart", panier);
        session.setAttribute("cartCount", panier.stream().mapToInt(CartItem::getQuantite).sum());
    }
}
