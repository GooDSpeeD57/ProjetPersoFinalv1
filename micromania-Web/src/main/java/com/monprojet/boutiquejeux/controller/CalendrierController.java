package com.monprojet.boutiquejeux.controller;

import com.monprojet.boutiquejeux.dto.api.catalog.ApiProduitSortie;
import com.monprojet.boutiquejeux.service.ApiService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/calendrier-sorties")
@RequiredArgsConstructor
public class CalendrierController {

    private final ApiService api;

    @GetMapping
    public String calendrier(
            @RequestParam(required = false) Integer annee,
            @RequestParam(required = false) Integer mois,
            HttpSession session,
            Model model) {

        LocalDate aujourd_hui = LocalDate.now();
        int anneeActive = (annee != null) ? annee : aujourd_hui.getYear();
        int moisActif   = (mois  != null) ? mois  : aujourd_hui.getMonthValue();

        // Charger toutes les sorties depuis l'API
        List<ApiProduitSortie> toutesLesSorties = api.getCalendrier();

        // Filtrer : NEUF uniquement (PRECOMMANDE inclus, OCCASION exclu)
        // + filtrer sur l'année et le mois sélectionnés
        YearMonth moisFiltre = YearMonth.of(anneeActive, moisActif);

        List<ApiProduitSortie> sortiesDuMois = toutesLesSorties.stream()
                .filter(s -> {
                    // Exclure l'occasion
                    String statut = s.statutProduit();
                    if (statut != null && statut.equalsIgnoreCase("OCCASION")) return false;
                    // Filtrer par mois/année
                    if (s.dateSortie() == null) return false;
                    return YearMonth.from(s.dateSortie()).equals(moisFiltre);
                })
                .collect(Collectors.toList());

        // Précommandes sans date (NEUF/PRECOMMANDE, pas OCCASION, pas de date)
        List<ApiProduitSortie> precommandesSansDate = toutesLesSorties.stream()
                .filter(s -> {
                    String statut = s.statutProduit();
                    if (statut != null && statut.equalsIgnoreCase("OCCASION")) return false;
                    return s.dateSortie() == null && s.estPreCommande();
                })
                .collect(Collectors.toList());

        // Construire la liste des mois disponibles pour l'année active (pour la barre de navigation)
        // On affiche tous les mois jan-déc ; on indique lesquels ont des sorties
        java.util.Set<Integer> moisAvecSorties = toutesLesSorties.stream()
                .filter(s -> s.dateSortie() != null
                        && s.dateSortie().getYear() == anneeActive
                        && (s.statutProduit() == null || !s.statutProduit().equalsIgnoreCase("OCCASION")))
                .map(s -> s.dateSortie().getMonthValue())
                .collect(java.util.stream.Collectors.toSet());

        // Années disponibles dans la liste
        java.util.List<Integer> anneesDisponibles = toutesLesSorties.stream()
                .filter(s -> s.dateSortie() != null
                        && (s.statutProduit() == null || !s.statutProduit().equalsIgnoreCase("OCCASION")))
                .map(s -> s.dateSortie().getYear())
                .distinct()
                .sorted()
                .collect(Collectors.toList());

        if (!anneesDisponibles.contains(anneeActive)) {
            anneesDisponibles.add(anneeActive);
            java.util.Collections.sort(anneesDisponibles);
        }

        model.addAttribute("sortiesDuMois",         sortiesDuMois);
        model.addAttribute("precommandesSansDate",   precommandesSansDate);
        model.addAttribute("anneeActive",            anneeActive);
        model.addAttribute("moisActif",              moisActif);
        model.addAttribute("moisAvecSorties",        moisAvecSorties);
        model.addAttribute("anneesDisponibles",      anneesDisponibles);
        model.addAttribute("aujourd_hui",            aujourd_hui);
        return "calendrier/index";
    }
}
