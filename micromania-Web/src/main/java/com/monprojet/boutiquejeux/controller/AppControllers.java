package com.monprojet.boutiquejeux.controller;

import com.monprojet.boutiquejeux.dto.api.client.ApiAdresse;
import com.monprojet.boutiquejeux.dto.api.client.ApiAdresseRequest;
import com.monprojet.boutiquejeux.dto.api.client.ApiAvatar;
import com.monprojet.boutiquejeux.dto.api.client.ApiBonAchat;
import com.monprojet.boutiquejeux.dto.api.client.ApiClient;
import com.monprojet.boutiquejeux.dto.api.client.ApiFideliteDetail;
import com.monprojet.boutiquejeux.dto.api.client.ApiHistoriquePoints;
import com.monprojet.boutiquejeux.dto.api.client.ApiPoints;
import com.monprojet.boutiquejeux.dto.api.client.ApiUpdateClientRequest;
import com.monprojet.boutiquejeux.dto.api.common.ApiPage;
import com.monprojet.boutiquejeux.dto.api.facture.ApiFactureDetail;
import com.monprojet.boutiquejeux.dto.api.facture.ApiFactureSummary;
import com.monprojet.boutiquejeux.dto.api.garantie.ApiGarantie;
import com.monprojet.boutiquejeux.service.ApiService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
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
        List<ApiAvatar> avatars = api.getAvatars(jwt);
        List<ApiGarantie> garanties = api.getClientGaranties(jwt);

        model.addAttribute("client", client);
        model.addAttribute("points", points);
        model.addAttribute("fidelite", fidelite);
        model.addAttribute("factures", factures != null ? factures.content() : List.of());
        model.addAttribute("adresses", adresses != null ? adresses : List.of());
        model.addAttribute("bonsDisponibles", bons != null ? bons.stream().filter(bon -> !bon.utilise()).toList() : List.of());
        model.addAttribute("bonsUtilises", bons != null ? bons.stream().filter(ApiBonAchat::utilise).toList() : List.of());
        model.addAttribute("historiquePoints", historiquePoints != null ? historiquePoints : List.of());
        model.addAttribute("typeAdresseOptions", List.of("DOMICILE", "LIVRAISON", "FACTURATION"));
        model.addAttribute("avatars", avatars != null ? avatars : List.of());
        model.addAttribute("garanties", garanties);
        return "compte/index";
    }

    @PostMapping("/avatar")
    String updateAvatar(HttpSession session,
                        @RequestParam Long idAvatar,
                        RedirectAttributes redirect) {
        String jwt = (String) session.getAttribute("jwt");
        if (jwt == null) return "redirect:/auth/login";

        try {
            ApiClient client = api.updateClientMe(jwt, new ApiUpdateClientRequest(
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    idAvatar
            ));
            if (client != null) {
                session.setAttribute("userTypeFidelite", client.typeFidelite());
            }
            redirect.addFlashAttribute("successMessage", "Avatar mis à jour.");
        } catch (RuntimeException e) {
            redirect.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/compte#avatar-picker";
    }

    @PostMapping("/infos")
    String updateInfos(HttpSession session,
                       @RequestParam String pseudo,
                       @RequestParam String nom,
                       @RequestParam String prenom,
                       @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateNaissance,
                       @RequestParam String email,
                       @RequestParam String telephone,
                       RedirectAttributes redirect) {
        String jwt = (String) session.getAttribute("jwt");
        if (jwt == null) return "redirect:/auth/login";

        try {
            ApiClient client = api.updateClientMe(jwt, new ApiUpdateClientRequest(
                    pseudo,
                    nom,
                    prenom,
                    dateNaissance,
                    email,
                    telephone,
                    null
            ));
            if (client != null) {
                session.setAttribute("userPseudo", client.pseudo());
                session.setAttribute("userTypeFidelite", client.typeFidelite());
            }
            redirect.addFlashAttribute("successMessage", "Informations du compte mises à jour.");
        } catch (RuntimeException e) {
            redirect.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/compte#infos";
    }

    @PostMapping("/adresses")
    String addAdresse(HttpSession session,
                      @RequestParam String codeTypeAdresse,
                      @RequestParam String rue,
                      @RequestParam(required = false) String complement,
                      @RequestParam String ville,
                      @RequestParam String codePostal,
                      @RequestParam(defaultValue = "France") String pays,
                      @RequestParam(defaultValue = "false") boolean estDefaut,
                      RedirectAttributes redirect) {
        String jwt = (String) session.getAttribute("jwt");
        if (jwt == null) return "redirect:/auth/login";

        try {
            api.addClientAdresse(jwt, new ApiAdresseRequest(
                    null,
                    codeTypeAdresse,
                    rue,
                    complement,
                    ville,
                    codePostal,
                    pays,
                    estDefaut
            ));
            redirect.addFlashAttribute("successMessage", "Adresse ajoutée à votre compte.");
        } catch (RuntimeException e) {
            redirect.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/compte#adresses";
    }

    @PostMapping("/adresses/{id}/defaut")
    String setAdresseDefaut(@PathVariable Long id,
                            HttpSession session,
                            RedirectAttributes redirect) {
        String jwt = (String) session.getAttribute("jwt");
        if (jwt == null) return "redirect:/auth/login";
        try {
            api.setAdresseDefaut(jwt, id);
            redirect.addFlashAttribute("successMessage", "Adresse par défaut mise à jour.");
        } catch (RuntimeException e) {
            redirect.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/compte#adresses";
    }

    @PostMapping("/adresses/{id}/supprimer")
    String deleteAdresse(@PathVariable Long id,
                         HttpSession session,
                         RedirectAttributes redirect) {
        String jwt = (String) session.getAttribute("jwt");
        if (jwt == null) return "redirect:/auth/login";
        try {
            api.deleteClientAdresse(jwt, id);
            redirect.addFlashAttribute("successMessage", "Adresse supprimée.");
        } catch (RuntimeException e) {
            redirect.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/compte#adresses";
    }

    @PostMapping("/magasin-favori/{idMagasin}")
    String setMagasinFavori(@PathVariable Long idMagasin,
                            @RequestParam(required = false, defaultValue = "false") boolean popup,
                            HttpSession session,
                            RedirectAttributes redirect) {
        String jwt = (String) session.getAttribute("jwt");
        if (jwt == null) return "redirect:/auth/login";
        try {
            api.setMagasinFavori(jwt, idMagasin);
            if (popup) return "redirect:/compte/magasin-favori/popup-ok";
            redirect.addFlashAttribute("successMessage", "Votre boutique favorite a été enregistrée.");
        } catch (RuntimeException e) {
            redirect.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/compte";
    }

    @GetMapping("/magasin-favori/popup-ok")
    org.springframework.http.ResponseEntity<String> magasinFavoriPopupOk() {
        String html = """
            <!DOCTYPE html><html><head><meta charset="UTF-8"></head>
            <body><script>
              window.parent.postMessage('magasin-favori-ok', window.location.origin);
            </script></body></html>
            """;
        return org.springframework.http.ResponseEntity.ok()
            .contentType(org.springframework.http.MediaType.TEXT_HTML)
            .body(html);
    }

    @PostMapping("/magasin-favori/supprimer")
    String removeMagasinFavori(HttpSession session, RedirectAttributes redirect) {
        String jwt = (String) session.getAttribute("jwt");
        if (jwt == null) return "redirect:/auth/login";
        try {
            api.removeMagasinFavori(jwt);
            redirect.addFlashAttribute("successMessage", "Boutique favorite retirée.");
        } catch (RuntimeException e) {
            redirect.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/compte";
    }

    @PostMapping("/verifier-email")
    String verifierEmail(HttpSession session, RedirectAttributes redirect) {
        String jwt = (String) session.getAttribute("jwt");
        if (jwt == null) return "redirect:/auth/login";
        try {
            api.simulerVerificationEmail(jwt);
            redirect.addFlashAttribute("successMessage",
                    "✅ Un e-mail de vérification a été envoyé (simulation). Votre adresse e-mail est maintenant vérifiée.");
        } catch (RuntimeException e) {
            redirect.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/compte#infos";
    }

    @PostMapping("/verifier-telephone")
    String verifierTelephone(HttpSession session, RedirectAttributes redirect) {
        String jwt = (String) session.getAttribute("jwt");
        if (jwt == null) return "redirect:/auth/login";
        try {
            api.simulerVerificationTelephone(jwt);
            redirect.addFlashAttribute("successMessage",
                    "✅ Un SMS de vérification a été envoyé (simulation). Votre numéro de téléphone est maintenant vérifié.");
        } catch (RuntimeException e) {
            redirect.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/compte#infos";
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
