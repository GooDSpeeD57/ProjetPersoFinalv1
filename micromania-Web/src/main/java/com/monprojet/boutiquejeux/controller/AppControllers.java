package com.monprojet.boutiquejeux.controller;

import com.monprojet.boutiquejeux.dto.api.client.ApiAdresse;
import com.monprojet.boutiquejeux.dto.api.client.ApiBonAchat;
import com.monprojet.boutiquejeux.dto.api.client.ApiClient;
import com.monprojet.boutiquejeux.dto.api.client.ApiFideliteDetail;
import com.monprojet.boutiquejeux.dto.api.client.ApiHistoriquePoints;
import com.monprojet.boutiquejeux.dto.api.client.ApiPoints;
import com.monprojet.boutiquejeux.dto.api.common.ApiPage;
import com.monprojet.boutiquejeux.dto.api.facture.ApiFactureDetail;
import com.monprojet.boutiquejeux.dto.api.facture.ApiFactureSummary;
import com.monprojet.boutiquejeux.service.ApiService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/compte")
@RequiredArgsConstructor
class CompteController {

    private final ApiService api;

    @GetMapping
    String compte(HttpSession session, Model model) {
        String jwt = (String) session.getAttribute("jwt");
        if (jwt == null) return "redirect:/auth/login";

        ApiClient client = api.getClientMe(jwt);
        if (client != null) {
            session.setAttribute("userTypeFidelite", client.typeFidelite());
        }

        ApiPoints points = api.getClientPoints(jwt);
        ApiFideliteDetail fidelite = api.getClientFidelite(jwt);
        ApiPage<ApiFactureSummary> factures = api.getClientFactures(jwt);
        List<ApiAdresse> adresses = api.getClientAdresses(jwt);
        List<ApiBonAchat> bons = api.getClientBonsAchat(jwt);
        List<ApiHistoriquePoints> historiquePoints = api.getClientHistoriquePoints(jwt);

        model.addAttribute("client", client);
        model.addAttribute("points", points);
        model.addAttribute("fidelite", fidelite);
        model.addAttribute("factures", factures != null ? factures.content() : List.of());
        model.addAttribute("adresses", adresses != null ? adresses : List.of());
        model.addAttribute("bonsDisponibles", bons != null ? bons.stream().filter(bon -> !bon.utilise()).toList() : List.of());
        model.addAttribute("bonsUtilises", bons != null ? bons.stream().filter(ApiBonAchat::utilise).toList() : List.of());
        model.addAttribute("historiquePoints", historiquePoints != null ? historiquePoints : List.of());
        return "compte/index";
    }

    @PostMapping("/ultimate/subscribe")
    String subscribeUltimate(HttpSession session, RedirectAttributes redirect) {
        String jwt = (String) session.getAttribute("jwt");
        if (jwt == null) return "redirect:/auth/login";
        try {
            ApiClient client = api.subscribeUltimate(jwt);
            if (client != null) {
                session.setAttribute("userTypeFidelite", client.typeFidelite());
            }
            redirect.addFlashAttribute("successMessage", "Abonnement ULTIMATE activé pour 12 mois.");
        } catch (RuntimeException e) {
            redirect.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/compte";
    }

    @PostMapping("/supprimer")
    String supprimer(HttpSession session, RedirectAttributes redirect) {
        String jwt = (String) session.getAttribute("jwt");
        if (jwt == null) return "redirect:/auth/login";
        try {
            api.deleteClientMe(jwt);
            session.invalidate();
            redirect.addFlashAttribute("successMessage",
                    "Votre demande de suppression a été enregistrée.");
            return "redirect:/auth/login";
        } catch (RuntimeException e) {
            redirect.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/compte";
        }
    }

    @GetMapping("/factures/{id}")
    String factureDetail(@PathVariable Long id,
                         HttpSession session,
                         Model model,
                         RedirectAttributes redirect) {
        String jwt = (String) session.getAttribute("jwt");
        if (jwt == null) return "redirect:/auth/login";

        try {
            ApiFactureDetail facture = api.getClientFacture(jwt, id);
            model.addAttribute("facture", facture);
            return "compte/facture-detail";
        } catch (RuntimeException e) {
            redirect.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/compte";
        }
    }
}
