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

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Controller
@RequestMapping("/catalogue")
@RequiredArgsConstructor
public class CatalogueController {

    private static final int CATALOGUE_FETCH_SIZE = 200;
    private static final int CATALOGUE_FETCH_MAX_PAGES = 15;

    private final ApiService api;

    @GetMapping
    String catalogue(Model model,
                     @RequestParam(defaultValue = "0") int page,
                     @RequestParam(defaultValue = "12") int size,
                     @RequestParam(required = false) String q,
                     @RequestParam(required = false) Long categorie,
                     @RequestParam(required = false) String niveau,
                     @RequestParam(required = false) String univers,
                     @RequestParam(required = false) String plateforme,
                     @RequestParam(required = false) String famille,
                     @RequestParam(required = false) String etat,
                     @RequestParam(required = false) String licence,
                     @RequestParam(required = false) String format) {

        String rechercheLibre = nettoyer(q);
        String universFiltre = nettoyer(univers);
        String plateformeFiltre = nettoyer(plateforme);
        String familleFiltre = nettoyer(famille);
        String etatFiltre = nettoyer(etat);
        String licenceFiltre = nettoyer(licence);
        String formatFiltre = nettoyer(format);

        boolean vueOccasion = "occasion".equalsIgnoreCase(universFiltre);
        boolean vueTcg = "tcg".equalsIgnoreCase(universFiltre);
        boolean vueJeuxVideo = "jeux-video".equalsIgnoreCase(universFiltre);

        if (vueOccasion) {
            etatFiltre = "occasion";
        }

        if (vueTcg) {
            plateformeFiltre = null;
            familleFiltre = null;
            etatFiltre = null;
        } else {
            licenceFiltre = null;
            formatFiltre = null;
        }

        boolean filtresStructurants = aTexte(universFiltre)
                || aTexte(plateformeFiltre)
                || aTexte(familleFiltre)
                || aTexte(etatFiltre)
                || aTexte(licenceFiltre)
                || aTexte(formatFiltre);

        ApiPage<ApiProduitSummary> produits;
        if (filtresStructurants) {
            List<ApiProduitSummary> pool = chargerCataloguePourFiltres(rechercheLibre, categorie, niveau);
            List<ApiProduitSummary> filtres = new ArrayList<>();
            for (ApiProduitSummary produit : pool) {
                if (correspondAuxFiltres(
                        produit,
                        universFiltre,
                        plateformeFiltre,
                        familleFiltre,
                        etatFiltre,
                        licenceFiltre,
                        formatFiltre)) {
                    filtres.add(produit);
                }
            }
            produits = paginer(filtres, page, size);
        } else {
            produits = api.getProduits(page, size, rechercheLibre, categorie, niveau);
        }

        List<ApiCategorie> categories = api.getCategories();

        model.addAttribute("produits", produits);
        model.addAttribute("categories", categories);
        model.addAttribute("q", rechercheLibre);
        model.addAttribute("categorie", categorie);
        model.addAttribute("niveau", niveau);
        model.addAttribute("univers", universFiltre);
        model.addAttribute("plateforme", plateformeFiltre);
        model.addAttribute("famille", familleFiltre);
        model.addAttribute("etat", etatFiltre);
        model.addAttribute("licence", licenceFiltre);
        model.addAttribute("format", formatFiltre);
        String catalogueTitre = "Catalogue";
        String catalogueIntro = "Recherche libre sur l'ensemble du catalogue.";

        if (vueJeuxVideo) {
            catalogueTitre = "Jeux vidéo";
            catalogueIntro = "Affinage par plateforme, type et état, sans dupliquer le menu principal.";
        } else if (vueOccasion) {
            catalogueTitre = "Occasion";
            catalogueIntro = "Cette vue affiche uniquement les offres d'occasion : la carte ne mélange plus neuf et occasion.";
        } else if (vueTcg) {
            catalogueTitre = "TCG";
            catalogueIntro = "Affinage dédié aux cartes à collectionner par licence et format.";
        }

        model.addAttribute("vueJeuxVideo", vueJeuxVideo);
        model.addAttribute("vueOccasion", vueOccasion);
        model.addAttribute("vueTcg", vueTcg);
        model.addAttribute("catalogueTitre", catalogueTitre);
        model.addAttribute("catalogueIntro", catalogueIntro);
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

    private List<ApiProduitSummary> chargerCataloguePourFiltres(String q, Long categorie, String niveau) {
        Map<Long, ApiProduitSummary> produits = new LinkedHashMap<>();

        for (int page = 0; page < CATALOGUE_FETCH_MAX_PAGES; page++) {
            ApiPage<ApiProduitSummary> bloc = api.getProduits(page, CATALOGUE_FETCH_SIZE, q, categorie, niveau);
            if (bloc == null || bloc.content() == null || bloc.content().isEmpty()) {
                break;
            }

            for (ApiProduitSummary produit : bloc.content()) {
                if (produit == null || produit.id() == null) {
                    continue;
                }
                produits.putIfAbsent(produit.id(), produit);
            }

            if (bloc.last() || bloc.content().size() < CATALOGUE_FETCH_SIZE) {
                break;
            }
        }

        return new ArrayList<>(produits.values());
    }

    private ApiPage<ApiProduitSummary> paginer(List<ApiProduitSummary> produits, int page, int size) {
        int pageCourante = Math.max(page, 0);
        int taillePage = size > 0 ? size : 12;
        int totalElements = produits != null ? produits.size() : 0;
        int totalPages = totalElements == 0 ? 0 : (int) Math.ceil((double) totalElements / taillePage);

        if (totalPages > 0 && pageCourante >= totalPages) {
            pageCourante = totalPages - 1;
        }

        int debut = Math.min(pageCourante * taillePage, totalElements);
        int fin = Math.min(debut + taillePage, totalElements);
        List<ApiProduitSummary> contenu = totalElements == 0 ? List.of() : produits.subList(debut, fin);

        return new ApiPage<>(
                contenu,
                pageCourante,
                taillePage,
                totalPages,
                totalElements,
                pageCourante == 0,
                totalPages == 0 || pageCourante >= totalPages - 1,
                totalElements == 0
        );
    }

    private boolean correspondAuxFiltres(ApiProduitSummary produit,
                                         String univers,
                                         String plateforme,
                                         String famille,
                                         String etat,
                                         String licence,
                                         String format) {
        return correspondUnivers(produit, univers)
                && correspondPlateforme(produit, plateforme)
                && correspondFamille(produit, famille)
                && correspondEtat(produit, etat)
                && correspondLicence(produit, licence)
                && correspondFormat(produit, format);
    }

    private boolean correspondUnivers(ApiProduitSummary produit, String univers) {
        if (!aTexte(univers)) {
            return true;
        }
        return switch (univers) {
            case "jeux-video" -> !estTcg(produit);
            case "occasion" -> aProduitOccasion(produit) && !estTcg(produit);
            case "tcg" -> estTcg(produit);
            default -> true;
        };
    }

    private boolean correspondPlateforme(ApiProduitSummary produit, String plateforme) {
        if (!aTexte(plateforme)) {
            return true;
        }
        String texte = texteProduit(produit);
        return switch (plateforme) {
            case "ps5" -> contientUnDesMots(texte, "playstation 5", "play station 5", "ps5");
            case "xbox" -> contientUnDesMots(texte, "xbox", "series x", "series s", "xbox one");
            case "nintendo" -> contientUnDesMots(texte, "nintendo", "switch");
            case "pc" -> contientUnDesMots(texte, "pc", "pc gaming", "steam", "ordinateur");
            default -> true;
        };
    }

    private boolean correspondFamille(ApiProduitSummary produit, String famille) {
        if (!aTexte(famille)) {
            return true;
        }
        return switch (famille) {
            case "jeux" -> estJeu(produit);
            case "consoles" -> estConsole(produit);
            default -> true;
        };
    }

    private boolean correspondEtat(ApiProduitSummary produit, String etat) {
        if (!aTexte(etat)) {
            return true;
        }
        return switch (etat) {
            case "neuf" -> produit.prixNeuf() != null;
            case "occasion" -> aProduitOccasion(produit);
            default -> true;
        };
    }

    private boolean correspondLicence(ApiProduitSummary produit, String licence) {
        if (!aTexte(licence)) {
            return true;
        }
        String texte = texteProduit(produit);
        return switch (licence) {
            case "pokemon" -> contientUnDesMots(texte, "pokemon");
            case "yugioh" -> contientUnDesMots(texte, "yugioh", "yu gi oh", "yu-gi-oh");
            case "magic" -> contientUnDesMots(texte, "magic");
            case "lorcana" -> contientUnDesMots(texte, "lorcana");
            default -> true;
        };
    }

    private boolean correspondFormat(ApiProduitSummary produit, String format) {
        if (!aTexte(format)) {
            return true;
        }
        String texte = texteProduit(produit);
        return switch (format) {
            case "boosters" -> contientUnDesMots(texte, "booster", "boosters", "display");
            case "coffrets" -> contientUnDesMots(texte, "coffret", "coffrets", "collection", "elite trainer box", "bundle", "box");
            case "accessoires" -> contientUnDesMots(texte, "accessoire", "accessoires", "sleeve", "sleeves", "binder", "classeur", "deck box", "playmat", "protege carte");
            default -> true;
        };
    }

    private boolean estTcg(ApiProduitSummary produit) {
        String texte = texteProduit(produit);
        return contientUnDesMots(texte,
                "tcg", "pokemon", "yugioh", "yu gi oh", "yu-gi-oh", "magic", "lorcana",
                "carte", "cartes", "booster", "boosters", "display", "deck", "sleeve", "playmat");
    }

    private boolean estConsole(ApiProduitSummary produit) {
        String typeCategorie = normaliserTexte(produit.typeCategorie());
        if (contientUnDesMots(typeCategorie, "console", "hardware")) {
            return true;
        }
        String texte = texteProduit(produit);
        return contientUnDesMots(texte,
                "console", "bundle console", "pack console", "edition console", "edition limitee console");
    }

    private boolean estJeu(ApiProduitSummary produit) {
        String typeCategorie = normaliserTexte(produit.typeCategorie());
        if (contientUnDesMots(typeCategorie, "jeu", "game")) {
            return true;
        }
        return !estConsole(produit) && !estTcg(produit);
    }

    private boolean aProduitOccasion(ApiProduitSummary produit) {
        if (produit.prixOccasion() != null) {
            return true;
        }
        return contientUnDesMots(texteProduit(produit), "occasion", "pre owned", "preowned", "seconde main");
    }

    private String texteProduit(ApiProduitSummary produit) {
        return " " + normaliserTexte(
                String.join(" ",
                        valeurOuVide(produit.nom()),
                        valeurOuVide(produit.categorie()),
                        valeurOuVide(produit.typeCategorie()),
                        valeurOuVide(produit.plateforme()),
                        valeurOuVide(produit.formatProduit()),
                        valeurOuVide(produit.imageAlt())
                )) + " ";
    }

    private String valeurOuVide(String valeur) {
        return valeur == null ? "" : valeur;
    }

    private boolean contientUnDesMots(String texte, String... termes) {
        if (!aTexte(texte) || termes == null) {
            return false;
        }
        for (String terme : termes) {
            String mot = normaliserTexte(terme);
            if (!aTexte(mot)) {
                continue;
            }
            String borne = " " + mot + " ";
            if (texte.contains(borne)) {
                return true;
            }
            if (mot.contains(" ") && texte.contains(mot)) {
                return true;
            }
        }
        return false;
    }

    private String normaliserTexte(String valeur) {
        if (valeur == null) {
            return "";
        }
        return Normalizer.normalize(valeur, Normalizer.Form.NFD)
                .replaceAll("\\p{M}+", "")
                .toLowerCase(Locale.ROOT)
                .replace('&', ' ')
                .replace('/', ' ')
                .replace('-', ' ')
                .replaceAll("[^a-z0-9 ]", " ")
                .replaceAll("\\s+", " ")
                .trim();
    }

    private boolean aTexte(String valeur) {
        return valeur != null && !valeur.isBlank();
    }

    private String nettoyer(String valeur) {
        if (valeur == null) {
            return null;
        }
        String nettoyee = valeur.trim();
        return nettoyee.isEmpty() ? null : nettoyee;
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
