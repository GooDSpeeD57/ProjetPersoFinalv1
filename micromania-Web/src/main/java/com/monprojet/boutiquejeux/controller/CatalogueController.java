package com.monprojet.boutiquejeux.controller;

import com.monprojet.boutiquejeux.dto.AvisProduitForm;
import com.monprojet.boutiquejeux.dto.api.catalog.ApiAvisClient;
import com.monprojet.boutiquejeux.dto.api.catalog.ApiCategorie;
import com.monprojet.boutiquejeux.dto.api.catalog.ApiProduitDetail;
import com.monprojet.boutiquejeux.dto.api.catalog.ApiProduitSummary;
import com.monprojet.boutiquejeux.dto.api.catalog.ApiVariantSummary;
import com.monprojet.boutiquejeux.dto.api.referentiel.ApiEdition;
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

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/catalogue")
@RequiredArgsConstructor
public class CatalogueController {

    private final ApiService api;

    @GetMapping
    String catalogue(Model model,
                     @RequestParam(defaultValue = "0") int page,
                     @RequestParam(defaultValue = "16") int size,
                     @RequestParam(required = false) String q,
                     @RequestParam(required = false) Long categorie,
                     @RequestParam(required = false) String niveau,
                     @RequestParam(required = false) String univers,
                     @RequestParam(required = false) String plateforme,
                     @RequestParam(required = false) String famille,
                     @RequestParam(required = false) String etat,
                     @RequestParam(required = false) String licence,
                     @RequestParam(required = false) String format,
                     @RequestParam(required = false) String edition,
                     @RequestParam(required = false) String tri) {

        String rechercheLibre   = nettoyer(q);
        String universFiltre    = nettoyer(univers);
        String plateformeFiltre = nettoyer(plateforme);
        String familleFiltre    = nettoyer(famille);
        String etatFiltre       = nettoyer(etat);
        String licenceFiltre    = nettoyer(licence);
        String formatFiltre     = nettoyer(format);
        String editionFiltre    = nettoyer(edition);

        boolean vueOccasion  = "occasion".equalsIgnoreCase(universFiltre);
        boolean vueTcg       = "tcg".equalsIgnoreCase(universFiltre);
        boolean vueJeuxVideo = "jeux-video".equalsIgnoreCase(universFiltre);

        if (vueOccasion) {
            etatFiltre = "occasion";
        } else if (vueJeuxVideo) {
            etatFiltre = "neuf";
        }

        if (vueTcg) {
            plateformeFiltre = null;
            familleFiltre    = "tcg";
            etatFiltre       = null;
        } else {
            licenceFiltre = null;
            formatFiltre  = null;
        }
        
        String qEffectif = rechercheLibre;
        if (vueTcg) {
            if (licenceFiltre != null) qEffectif = combine(qEffectif, licenceFiltre);
            if (formatFiltre  != null) qEffectif = combine(qEffectif, formatFiltre);
        }

        String triFiltre = nettoyer(tri);

        ApiPage<ApiVariantSummary> produits = api.getCatalogue(
                page, size,
                qEffectif,
                categorie,
                plateformeFiltre,
                familleFiltre,
                etatFiltre,
                editionFiltre,
                triFiltre
        );

        List<ApiCategorie>  categories = api.getCategories();
        List<ApiEdition>    editions   = api.getEditions();

        model.addAttribute("produits",    produits);
        model.addAttribute("categories",  categories);
        model.addAttribute("editions",    editions);
        model.addAttribute("q",           rechercheLibre);
        model.addAttribute("categorie",   categorie);
        model.addAttribute("niveau",      niveau);
        model.addAttribute("univers",     universFiltre);
        model.addAttribute("plateforme",  plateformeFiltre);
        model.addAttribute("famille",     familleFiltre);
        model.addAttribute("etat",        etatFiltre);
        model.addAttribute("edition",     editionFiltre);
        model.addAttribute("licence",     licenceFiltre);
        model.addAttribute("format",      formatFiltre);
        model.addAttribute("tri",         triFiltre);

        String catalogueTitre = "Catalogue";
        String catalogueIntro = "Recherche libre sur l'ensemble du catalogue.";
        if (vueJeuxVideo) {
            catalogueTitre = "Jeux vidéo";
            catalogueIntro = "Affinage par plateforme, type et état.";
        } else if (vueOccasion) {
            catalogueTitre = "Occasion";
            catalogueIntro = "Uniquement les offres d'occasion.";
        } else if (vueTcg) {
            catalogueTitre = "TCG";
            catalogueIntro = "Cartes à collectionner par licence et format.";
        }

        model.addAttribute("vueJeuxVideo",    vueJeuxVideo);
        model.addAttribute("vueOccasion",     vueOccasion);
        model.addAttribute("vueTcg",          vueTcg);
        model.addAttribute("catalogueTitre",  catalogueTitre);
        model.addAttribute("catalogueIntro",  catalogueIntro);
        model.addAttribute("resetVersUnivers", universFiltre);
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
            model.addAttribute("avisFormOpen", true);
            return "catalogue/detail";
        }

        try {
            api.soumettreAvisProduit(jwt, id, avisForm.getNote(), avisForm.getCommentaire());
            redirect.addFlashAttribute("successMessage", "Votre avis a bien été publié !");
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

        // Plateformes uniques (dédupliquées) pour affichage en tags
        List<String> plateformesUniques = (produit != null && produit.variants() != null)
                ? produit.variants().stream()
                    .filter(v -> v.actif() && v.plateforme() != null && v.plateforme().libelle() != null)
                    .map(v -> v.plateforme().libelle())
                    .distinct()
                    .sorted()
                    .collect(Collectors.toList())
                : List.of();
        model.addAttribute("plateformesUniques", plateformesUniques);

        ApiAvisClient monAvis = null;
        boolean aAchete = false;
        String jwt = lireJwt(request);
        String typeFideliteCode = null;

        HttpSession session = request != null ? request.getSession(false) : null;
        if (session != null) {
            Object tf = session.getAttribute("userTypeFidelite");
            if (tf instanceof String s && !s.isBlank()) typeFideliteCode = s;
        }

        if (jwt != null) {
            monAvis = api.getMonAvisProduit(jwt, id);
            // Si l'utilisateur a déjà un avis, il a forcément acheté le produit
            aAchete = monAvis != null || api.peutSoumettreAvis(jwt, id);
        }
        // Prix de reprise : premier variant actif avec prixReprise non null
        BigDecimal prixReprise = null;
        if (produit.variants() != null) {
            prixReprise = produit.variants().stream()
                    .filter(v -> v.actif() && v.prixReprise() != null)
                    .map(v -> v.prixReprise())
                    .findFirst()
                    .orElse(null);
        }

        model.addAttribute("monAvis", monAvis);
        model.addAttribute("aAchete", aAchete);
        model.addAttribute("typeFideliteCode", typeFideliteCode);
        model.addAttribute("prixReprise", prixReprise);
        Long categorieId = (produit.categorie() != null) ? produit.categorie().id() : null;
        model.addAttribute("typesGarantie", api.getTypesGarantie(categorieId));

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
        if (session == null) return null;
        Object jwt = session.getAttribute("jwt");
        return jwt instanceof String token && !token.isBlank() ? token : null;
    }

    private String nettoyer(String valeur) {
        if (valeur == null) return null;
        String nettoyee = valeur.trim();
        return nettoyee.isEmpty() ? null : nettoyee;
    }

    private String combine(String a, String b) {
        if (a == null || a.isBlank()) return b;
        if (b == null || b.isBlank()) return a;
        return a + " " + b;
    }
}
