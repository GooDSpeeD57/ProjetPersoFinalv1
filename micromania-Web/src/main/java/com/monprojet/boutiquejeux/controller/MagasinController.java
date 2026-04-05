package com.monprojet.boutiquejeux.controller;

import com.monprojet.boutiquejeux.dto.api.magasin.ApiMagasin;
import com.monprojet.boutiquejeux.service.ApiService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/magasins")
@RequiredArgsConstructor
public class MagasinController {

    private final ApiService apiService;

    @GetMapping
    String index(@RequestParam(required = false) String q,
                 HttpSession session,
                 Model model) {

        String recherche = q != null ? q.trim() : "";
        boolean hasSearch = !recherche.isBlank();
        List<ApiMagasin> magasins = hasSearch ? apiService.getMagasins(recherche) : List.of();

        model.addAttribute("magasins", magasins);
        model.addAttribute("q", recherche);
        model.addAttribute("hasSearch", hasSearch);
        model.addAttribute("selectedMagasinId", session.getAttribute("selectedMagasinId"));
        return "magasins/index";
    }

    @PostMapping("/{idMagasin}/selection")
    String choisirMagasin(@PathVariable Long idMagasin,
                          HttpSession session,
                          RedirectAttributes redirect) {

        ApiMagasin magasin = apiService.getMagasinById(idMagasin);
        if (magasin == null) {
            redirect.addFlashAttribute("errorMessage", "Magasin introuvable.");
            return "redirect:/magasins";
        }

        session.setAttribute("selectedMagasinId", magasin.id());
        session.setAttribute("selectedMagasinNom", magasin.nom());
        session.setAttribute("selectedMagasinVille", magasin.ville());
        session.setAttribute("selectedMagasinCodePostal", magasin.codePostal());
        session.setAttribute("selectedMagasinAdresse", magasin.adresseComplete());

        redirect.addFlashAttribute("successMessage", "Votre magasin a bien été sélectionné : " + magasin.nom());
        String retourRecherche = magasin.codePostal() != null && !magasin.codePostal().isBlank()
                ? magasin.codePostal()
                : (magasin.ville() != null ? magasin.ville() : "");
        return "redirect:/magasins?q=" + retourRecherche;
    }

    @PostMapping("/selection/reset")
    String reinitialiserMagasin(HttpSession session,
                                RedirectAttributes redirect) {

        session.removeAttribute("selectedMagasinId");
        session.removeAttribute("selectedMagasinNom");
        session.removeAttribute("selectedMagasinVille");
        session.removeAttribute("selectedMagasinCodePostal");
        session.removeAttribute("selectedMagasinAdresse");

        redirect.addFlashAttribute("infoMessage", "Votre magasin sélectionné a été retiré.");
        return "redirect:/magasins";
    }
}
