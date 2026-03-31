package com.monprojet.boutiquejeux.controller;

import com.monprojet.boutiquejeux.dto.api.client.ApiAdresse;
import com.monprojet.boutiquejeux.dto.api.client.ApiClient;
import com.monprojet.boutiquejeux.dto.api.client.ApiPoints;
import com.monprojet.boutiquejeux.dto.api.common.ApiPage;
import com.monprojet.boutiquejeux.dto.api.facture.ApiFactureSummary;
import com.monprojet.boutiquejeux.service.ApiService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
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
        ApiPoints points = api.getClientPoints(jwt);
        ApiPage<ApiFactureSummary> factures = api.getClientFactures(jwt);
        List<ApiAdresse> adresses = api.getClientAdresses(jwt);

        model.addAttribute("client", client);
        model.addAttribute("points", points);
        model.addAttribute("factures", factures != null ? factures.content() : List.of());
        model.addAttribute("adresses", adresses);
        return "compte/index";
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
}
