package com.monprojet.boutiquejeux.controller;

import com.monprojet.boutiquejeux.dto.AvisProduitForm;
import com.monprojet.boutiquejeux.dto.api.catalog.ApiAvisClient;
import com.monprojet.boutiquejeux.dto.api.catalog.ApiCategorie;
import com.monprojet.boutiquejeux.dto.api.catalog.ApiProduitDetail;
import com.monprojet.boutiquejeux.dto.api.catalog.ApiProduitSummary;
import com.monprojet.boutiquejeux.dto.api.common.ApiPage;
import com.monprojet.boutiquejeux.service.ApiService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/catalogue")
@RequiredArgsConstructor
public class CatalogueController {

    private final ApiService api;

    @GetMapping
    String catalogue(Model model,
                     @RequestParam(defaultValue = "0") int page,
                     @RequestParam(defaultValue = "12") int size,
                     @RequestParam(required = false) String q,
                     @RequestParam(required = false) Long categorie,
                     @RequestParam(required = false) String niveau) {

        ApiPage<ApiProduitSummary> produits = api.getProduits(page, size, q, categorie, niveau);
        List<ApiCategorie> categories = api.getCategories();
        model.addAttribute("produits", produits);
        model.addAttribute("categories", categories);
        model.addAttribute("q", q);
        model.addAttribute("categorie", categorie);
        model.addAttribute("niveau", niveau);
        return "catalogue/index";
    }

    @GetMapping("/{id}")
    String detail(@PathVariable Long id, Model model, HttpServletRequest request) {
        chargerPageDetail(id, model, request, true);
        return "catalogue/detail";
    }

    @PostMapping("/{id}/avis")
    String deposerAvis(@PathVariable Long id,
                       @Valid @ModelAttribute("avisForm") AvisProduitForm avisForm,
                       BindingResult result,
                       HttpServletRequest request,
                       Model model,
                       RedirectAttributes redirect) {

        String jwt = lireJwt(request);
        if (jwt == null) {
            redirect.addFlashAttribute("errorMessage", "Connectez-vous pour laisser une note et un avis.");
            return "redirect:/auth/login";
        }

        if (result.hasErrors()) {
            chargerPageDetail(id, model, request, false);
            return "catalogue/detail";
        }

        try {
            api.soumettreAvisProduit(jwt, id, avisForm.getNote(), avisForm.getCommentaire());
            redirect.addFlashAttribute("infoMessage", "Votre avis a été publié. Il pourra ensuite être modéré si nécessaire.");
        } catch (RuntimeException e) {
            redirect.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/catalogue/" + id;
    }

    private void chargerPageDetail(Long id, Model model, HttpServletRequest request, boolean initialiserForm) {
        ApiProduitDetail produit = api.getProduitDetail(id);
        model.addAttribute("produit", produit);

        if (produit == null) {
            if (initialiserForm && !model.containsAttribute("avisForm")) {
                model.addAttribute("avisForm", new AvisProduitForm());
            }
            model.addAttribute("monAvis", null);
            return;
        }

        ApiAvisClient monAvis = null;
        String jwt = lireJwt(request);
        if (jwt != null) {
            monAvis = api.getMonAvisProduit(jwt, id);
        }
        model.addAttribute("monAvis", monAvis);

        if (initialiserForm && !model.containsAttribute("avisForm")) {
            AvisProduitForm form = new AvisProduitForm();
            if (monAvis != null) {
                form.setNote((int) monAvis.note());
                form.setCommentaire(monAvis.commentaire());
            }
            model.addAttribute("avisForm", form);
        }
    }

    private String lireJwt(HttpServletRequest request) {
        HttpSession session = request != null ? request.getSession(false) : null;
        if (session == null) {
            return null;
        }
        Object jwt = session.getAttribute("jwt");
        return jwt instanceof String token && !token.isBlank() ? token : null;
    }
}
