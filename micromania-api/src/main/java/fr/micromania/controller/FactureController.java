package fr.micromania.controller;

import fr.micromania.dto.facture.*;
import fr.micromania.service.FactureService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/factures")
@RequiredArgsConstructor
public class FactureController {

    private final FactureService factureService;

    @GetMapping("/me")
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<Page<FactureSummary>> getMesFactures(
            @AuthenticationPrincipal Long idClient,
            @PageableDefault(size = 10) Pageable pageable) {

        return ResponseEntity.ok(factureService.getByClient(idClient, pageable));
    }

    @GetMapping("/me/{id}")
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<FactureResponse> getMaFacture(
            @AuthenticationPrincipal Long idClient,
            @PathVariable Long id) {

        return ResponseEntity.ok(factureService.getByIdForClient(idClient, id));
    }


    @PostMapping("/me/checkout")
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<FactureResponse> checkoutPanier(
            @AuthenticationPrincipal Long idClient,
            @Valid @RequestBody CheckoutPanierRequest request) {

        return ResponseEntity.status(HttpStatus.CREATED)
            .body(factureService.checkoutPanierClient(idClient, request));
    }

    // ── Back-office ────────────────────────────────────────────

    @PostMapping("/commande/{idCommande}")
    @PreAuthorize("hasAnyRole('VENDEUR','MANAGER','ADMIN')")
    public ResponseEntity<FactureResponse> genererDepuisCommande(
            @PathVariable Long idCommande) {

        return ResponseEntity.status(HttpStatus.CREATED)
            .body(factureService.genererDepuisCommande(idCommande));
    }

    @PostMapping("/vente-magasin")
    @PreAuthorize("hasAnyRole('VENDEUR','MANAGER','ADMIN')")
    public ResponseEntity<FactureResponse> creerVenteMagasin(
            @Valid @RequestBody CreateFactureVenteRequest request) {

        return ResponseEntity.status(HttpStatus.CREATED)
            .body(factureService.creerVenteMagasin(request));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('MANAGER','ADMIN')")
    public ResponseEntity<Page<FactureSummary>> getByMagasin(
            @RequestParam Long idMagasin,
            @PageableDefault(size = 20) Pageable pageable) {

        return ResponseEntity.ok(factureService.getByMagasin(idMagasin, pageable));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('VENDEUR','MANAGER','ADMIN')")
    public ResponseEntity<FactureResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(factureService.getById(id));
    }

    @GetMapping("/ref/{reference}")
    @PreAuthorize("hasAnyRole('VENDEUR','MANAGER','ADMIN')")
    public ResponseEntity<FactureResponse> getByReference(@PathVariable String reference) {
        return ResponseEntity.ok(factureService.getByReference(reference));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('MANAGER','ADMIN')")
    public ResponseEntity<Void> annuler(@PathVariable Long id) {
        factureService.annuler(id);
        return ResponseEntity.noContent().build();
    }
}
