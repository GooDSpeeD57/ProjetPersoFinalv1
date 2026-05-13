package com.monprojet.boutiquejeux.controller;

import com.monprojet.boutiquejeux.dto.InscriptionForm;
import com.monprojet.boutiquejeux.dto.api.auth.ApiAuthResponse;
import com.monprojet.boutiquejeux.dto.api.catalog.ApiProduitDetail;
import com.monprojet.boutiquejeux.dto.api.catalog.ApiProduitImage;
import com.monprojet.boutiquejeux.dto.api.catalog.ApiProduitSummary;
import com.monprojet.boutiquejeux.dto.api.common.ApiPage;
import com.monprojet.boutiquejeux.service.ApiService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

@Controller
@RequiredArgsConstructor
class HomeController {

    private static final int PROMO_SLIDES_COUNT = 4;
    private static final int RAIL_ITEMS_COUNT = 8;
    private static final DateTimeFormatter HOME_DATE_FORMATTER = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.FRANCE);

    private final ApiService api;

    @GetMapping("/")
    String home(Model model) {
        LocalDate today = LocalDate.now();

        List<ApiProduitSummary> misEnAvant = api.getProduitsMisEnAvant();
        List<ApiProduitSummary> poolRecents = safeContent(api.getProduitsTries(0, 24, null, null, null, "dateSortie,desc", "id,desc"));
        List<ApiProduitSummary> poolAVenir = safeContent(api.getProduitsTries(0, 24, null, null, null, "dateSortie,asc", "id,desc"));
        List<ApiProduitSummary> poolPopularite = safeContent(api.getProduitsTries(0, 36, null, null, null, "id,desc"));

        Map<Long, ApiProduitDetail> details = chargerDetails(idsUniques(misEnAvant, poolRecents, poolAVenir, poolPopularite));

        List<HomeHeroSlide> promoSlides = construirePromoSlides(misEnAvant, poolPopularite, details, today);
        List<HomeProductCard> prochainesSorties = construireProchainesSorties(poolAVenir, details, today);
        List<HomeProductCard> topVentes = construireTopVentes(poolPopularite, details);
        List<HomeProductCard> nouveautes = construireNouveautes(poolRecents, details, today);

        model.addAttribute("promoSlides", promoSlides);
        model.addAttribute("prochainesSorties", prochainesSorties);
        model.addAttribute("topVentes", topVentes);
        model.addAttribute("nouveautes", nouveautes);
        return "index";
    }

    private List<HomeHeroSlide> construirePromoSlides(List<ApiProduitSummary> misEnAvant,
                                                      List<ApiProduitSummary> poolPopularite,
                                                      Map<Long, ApiProduitDetail> details,
                                                      LocalDate today) {
        List<ApiProduitSummary> source = !misEnAvant.isEmpty() ? misEnAvant : poolPopularite;

        return dedupliquer(source).stream()
                .limit(PROMO_SLIDES_COUNT)
                .map(produit -> {
                    ApiProduitDetail detail = details.get(produit.id());
                    boolean prochaineSortie = detail != null
                            && detail.dateSortie() != null
                            && !detail.dateSortie().isBefore(today);

                    String eyebrow = prochaineSortie ? "Précommande ouverte" : "Promo en cours";
                    String infoLine = prochaineSortie && detail.dateSortie() != null
                            ? "Sortie le " + formaterDate(detail.dateSortie())
                            : produit.prixNeuf() != null
                                ? "À partir de " + formaterPrix(produit.prixNeuf())
                                : "Sélection gaming du moment";

                    HomeProductCard card = versCarte(
                            produit,
                            detail,
                            "Offre web",
                            "badge-ultimate",
                            infoLine
                    );

                    String description = premiereValeur(detail != null ? detail.resumeCourt() : null,
                            detail != null ? detail.description() : null,
                            "Sélection mise en avant pour dynamiser la home, avec un rendu inspiré des grandes vitrines Micromania.");

                    return new HomeHeroSlide(card, eyebrow, raccourcir(description, 170));
                })
                .toList();
    }

    private List<HomeProductCard> construireProchainesSorties(List<ApiProduitSummary> pool,
                                                              Map<Long, ApiProduitDetail> details,
                                                              LocalDate today) {
        List<HomeProductCard> sorties = dedupliquer(pool).stream()
                .map(produit -> versCarte(
                        produit,
                        details.get(produit.id()),
                        "À venir",
                        "badge-premium",
                        construireLibelleSortie(details.get(produit.id()), today, true)
                ))
                .filter(card -> card.dateSortie() != null && !card.dateSortie().isBefore(today))
                .sorted(Comparator.comparing(HomeProductCard::dateSortie))
                .limit(RAIL_ITEMS_COUNT)
                .toList();

        if (!sorties.isEmpty()) {
            return sorties;
        }

        return dedupliquer(pool).stream()
                .limit(RAIL_ITEMS_COUNT)
                .map(produit -> versCarte(produit, details.get(produit.id()), "Bientôt", "badge-premium", "Précommandes et sorties à suivre"))
                .toList();
    }

    private List<HomeProductCard> construireTopVentes(List<ApiProduitSummary> pool,
                                                      Map<Long, ApiProduitDetail> details) {
        Comparator<ApiProduitSummary> popularite = Comparator
                .comparingLong(ApiProduitSummary::nbAvis).reversed()
                .thenComparing(ApiProduitSummary::noteMoyenne, Comparator.nullsLast(Comparator.reverseOrder()))
                .thenComparing((ApiProduitSummary produit) -> produit.misEnAvant() ? 1 : 0, Comparator.reverseOrder())
                .thenComparing(ApiProduitSummary::id, Comparator.nullsLast(Comparator.reverseOrder()));

        return dedupliquer(pool).stream()
                .sorted(popularite)
                .limit(RAIL_ITEMS_COUNT)
                .map(produit -> versCarte(
                        produit,
                        details.get(produit.id()),
                        "Top vente",
                        "badge-ultimate",
                        construireLibellePopularite(produit)
                ))
                .toList();
    }

    private List<HomeProductCard> construireNouveautes(List<ApiProduitSummary> pool,
                                                       Map<Long, ApiProduitDetail> details,
                                                       LocalDate today) {
        List<HomeProductCard> nouveautes = dedupliquer(pool).stream()
                .map(produit -> versCarte(
                        produit,
                        details.get(produit.id()),
                        "Nouveau",
                        "badge-neuf",
                        construireLibelleSortie(details.get(produit.id()), today, false)
                ))
                .filter(card -> card.dateSortie() != null && !card.dateSortie().isAfter(today))
                .sorted(Comparator.comparing(HomeProductCard::dateSortie, Comparator.nullsLast(Comparator.reverseOrder())))
                .limit(RAIL_ITEMS_COUNT)
                .toList();

        if (!nouveautes.isEmpty()) {
            return nouveautes;
        }

        return dedupliquer(pool).stream()
                .limit(RAIL_ITEMS_COUNT)
                .map(produit -> versCarte(produit, details.get(produit.id()), "Nouveau", "badge-neuf", "Derniers ajouts au catalogue"))
                .toList();
    }

    private HomeProductCard versCarte(ApiProduitSummary produit,
                                      ApiProduitDetail detail,
                                      String badge,
                                      String badgeClass,
                                      String infoLine) {
        String categorie = premiereValeur(produit.categorie(),
                detail != null && detail.categorie() != null ? detail.categorie().description() : null,
                "Gaming");

        String imageUrl = premiereValeur(produit.imageUrl(), imageDepuisDetail(detail));
        String imageAlt = premiereValeur(produit.imageAlt(), imageAltDepuisDetail(detail), produit.nom());

        return new HomeProductCard(
                produit.id(),
                produit.nom(),
                categorie,
                imageUrl,
                imageAlt,
                produit.prixNeuf(),
                produit.prixOccasion(),
                detail != null ? detail.dateSortie() : null,
                produit.noteMoyenne(),
                produit.nbAvis(),
                badge,
                badgeClass,
                infoLine
        );
    }

    private String construireLibellePopularite(ApiProduitSummary produit) {
        if (produit.noteMoyenne() != null && produit.nbAvis() > 0) {
            return String.format(Locale.FRANCE, "%.1f/5 · %d avis", produit.noteMoyenne(), produit.nbAvis());
        }
        if (produit.nbAvis() > 0) {
            return produit.nbAvis() + " avis clients";
        }
        return "Sélection la plus consultée";
    }

    private String construireLibelleSortie(ApiProduitDetail detail, LocalDate today, boolean future) {
        if (detail == null || detail.dateSortie() == null) {
            return future ? "Précommandes et sorties à suivre" : "Nouveau dans le catalogue";
        }

        if (future && !detail.dateSortie().isBefore(today)) {
            return "Sortie le " + formaterDate(detail.dateSortie());
        }
        if (!future && !detail.dateSortie().isAfter(today)) {
            return "Sorti le " + formaterDate(detail.dateSortie());
        }
        return future ? "Disponible bientôt" : "Ajout récent";
    }

    private Map<Long, ApiProduitDetail> chargerDetails(Set<Long> ids) {
        Map<Long, ApiProduitDetail> details = new LinkedHashMap<>();
        for (Long id : ids) {
            ApiProduitDetail detail = api.getProduitDetail(id);
            if (detail != null) {
                details.put(id, detail);
            }
        }
        return details;
    }

    private Set<Long> idsUniques(Collection<ApiProduitSummary>... collections) {
        Set<Long> ids = new LinkedHashSet<>();
        for (Collection<ApiProduitSummary> collection : collections) {
            if (collection == null) {
                continue;
            }
            for (ApiProduitSummary produit : collection) {
                if (produit != null && produit.id() != null) {
                    ids.add(produit.id());
                }
            }
        }
        return ids;
    }

    private List<ApiProduitSummary> dedupliquer(List<ApiProduitSummary> produits) {
        Map<Long, ApiProduitSummary> uniques = new LinkedHashMap<>();
        for (ApiProduitSummary produit : produits) {
            if (produit != null && produit.id() != null) {
                uniques.putIfAbsent(produit.id(), produit);
            }
        }
        return new ArrayList<>(uniques.values());
    }

    private List<ApiProduitSummary> safeContent(ApiPage<ApiProduitSummary> page) {
        return page != null && page.content() != null ? page.content() : List.of();
    }

    private String imageDepuisDetail(ApiProduitDetail detail) {
        if (detail == null || detail.images() == null || detail.images().isEmpty()) {
            return null;
        }
        return detail.images().stream()
                .filter(ApiProduitImage::principale)
                .findFirst()
                .or(() -> detail.images().stream().findFirst())
                .map(ApiProduitImage::url)
                .orElse(null);
    }

    private String imageAltDepuisDetail(ApiProduitDetail detail) {
        if (detail == null || detail.images() == null || detail.images().isEmpty()) {
            return null;
        }
        return detail.images().stream()
                .filter(ApiProduitImage::principale)
                .findFirst()
                .or(() -> detail.images().stream().findFirst())
                .map(ApiProduitImage::alt)
                .orElse(null);
    }

    private String premiereValeur(String... valeurs) {
        for (String valeur : valeurs) {
            if (valeur != null && !valeur.isBlank()) {
                return valeur;
            }
        }
        return null;
    }

    private String raccourcir(String texte, int longueurMax) {
        if (texte == null || texte.length() <= longueurMax) {
            return texte;
        }
        return texte.substring(0, Math.max(0, longueurMax - 1)).trim() + "…";
    }

    private String formaterDate(LocalDate date) {
        return date.format(HOME_DATE_FORMATTER);
    }

    private String formaterPrix(BigDecimal prix) {
        return prix.stripTrailingZeros().toPlainString().replace('.', ',') + " €";
    }
}

@Controller
class RepriseController {
    /** Fonctionnalité de reprise — page provisoire en attendant l'implémentation complète. */
    @GetMapping("/reprise")
    String reprisePage() {
        return "redirect:/catalogue";
    }
}

@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
class AuthController {

    private final ApiService api;

    @GetMapping("/login")
    String loginPage() {
        return "auth/login";
    }

    @GetMapping("/inscription")
    String inscriptionPage(Model model) {
        if (!model.containsAttribute("inscriptionForm")) {
            model.addAttribute("inscriptionForm", new InscriptionForm());
        }
        return "auth/inscription";
    }

    @PostMapping("/inscription")
    String inscription(@Valid @ModelAttribute("inscriptionForm") InscriptionForm form,
                       BindingResult result,
                       HttpSession session,
                       RedirectAttributes redirect,
                       Model model) {

        if (result.hasErrors()) {
            return "auth/inscription";
        }

        try {
            ApiAuthResponse resp = api.inscription(
                    form.getPseudo(),
                    form.getNom(),
                    form.getPrenom(),
                    form.getDateNaissance(),
                    form.getEmail(),
                    form.getTelephone(),
                    form.getMotDePasse(),
                    Boolean.TRUE.equals(form.getRgpdConsent())
            );

            if (resp != null && resp.accessToken() != null && !resp.accessToken().isBlank()) {
                session.setAttribute("jwt", resp.accessToken());
                session.setAttribute("userEmail", resp.email());
                session.setAttribute("userPseudo", resp.pseudo());
                session.setAttribute("userTypeFidelite", resp.typeFidelite());

                redirect.addFlashAttribute("successMessage", "Compte créé avec succès ! Bienvenue 🎮");
                return "redirect:/catalogue";
            }

            redirect.addFlashAttribute("successMessage", "Compte créé avec succès. Vous pouvez maintenant vous connecter.");
            return "redirect:/auth/login";

        } catch (RuntimeException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "auth/inscription";
        }
    }
}

record HomeProductCard(
        Long id,
        String nom,
        String categorie,
        String imageUrl,
        String imageAlt,
        BigDecimal prixNeuf,
        BigDecimal prixOccasion,
        LocalDate dateSortie,
        Double noteMoyenne,
        long nbAvis,
        String badge,
        String badgeClass,
        String infoLine
) {}

record HomeHeroSlide(
        HomeProductCard product,
        String eyebrow,
        String description
) {}
