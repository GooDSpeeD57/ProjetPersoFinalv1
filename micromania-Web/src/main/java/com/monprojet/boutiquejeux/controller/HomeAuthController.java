package com.monprojet.boutiquejeux.controller;

import com.monprojet.boutiquejeux.dto.InscriptionForm;
import com.monprojet.boutiquejeux.dto.api.auth.ApiAuthResponse;
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

@Controller
class HomeController {
    @GetMapping("/")
    String home() {
        return "index";
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