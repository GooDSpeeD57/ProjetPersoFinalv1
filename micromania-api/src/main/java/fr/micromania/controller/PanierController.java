package fr.micromania.controller;

import fr.micromania.dto.panier.*;
import fr.micromania.service.PanierService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/panier")
@RequiredArgsConstructor
@PreAuthorize("hasRole('CLIENT')")
public class PanierController {

    private final PanierService panierService;

    @GetMapping
    public ResponseEntity<PanierResponse> getPanier(
            @AuthenticationPrincipal Long idClient,
            @RequestParam(defaultValue = "WEB") String canal) {

        return ResponseEntity.ok(panierService.getPanierActif(idClient, canal));
    }

    @PostMapping("/lignes")
    public ResponseEntity<PanierResponse> addLigne(
            @AuthenticationPrincipal Long idClient,
            @Valid @RequestBody AddLignePanierRequest request) {

        return ResponseEntity.ok(panierService.addLigne(idClient, request));
    }

    @PutMapping("/lignes/{idLigne}")
    public ResponseEntity<PanierResponse> updateLigne(
            @AuthenticationPrincipal Long idClient,
            @PathVariable Long idLigne,
            @Valid @RequestBody UpdateLignePanierRequest request) {

        return ResponseEntity.ok(panierService.updateLigne(idClient, idLigne, request));
    }

    @DeleteMapping("/lignes/{idLigne}")
    public ResponseEntity<PanierResponse> removeLigne(
            @AuthenticationPrincipal Long idClient,
            @PathVariable Long idLigne) {

        return ResponseEntity.ok(panierService.removeLigne(idClient, idLigne));
    }

    @DeleteMapping
    public ResponseEntity<Void> vider(
            @AuthenticationPrincipal Long idClient,
            @RequestParam(defaultValue = "WEB") String canal) {

        panierService.vider(idClient, canal);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/promo")
    public ResponseEntity<PanierResponse> appliquerPromo(
            @AuthenticationPrincipal Long idClient,
            @RequestParam String code,
            @RequestParam(defaultValue = "WEB") String canal) {

        return ResponseEntity.ok(panierService.appliquerCodePromo(idClient, code, canal));
    }

    @DeleteMapping("/promo")
    public ResponseEntity<PanierResponse> retirerPromo(
            @AuthenticationPrincipal Long idClient,
            @RequestParam(defaultValue = "WEB") String canal) {

        return ResponseEntity.ok(panierService.retirerCodePromo(idClient, canal));
    }
}
