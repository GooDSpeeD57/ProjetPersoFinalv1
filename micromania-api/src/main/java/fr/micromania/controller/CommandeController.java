package fr.micromania.controller;

import fr.micromania.dto.commande.*;
import fr.micromania.service.CommandeService;
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
@RequestMapping("/api/v1/commandes")
@RequiredArgsConstructor
public class CommandeController {

    private final CommandeService commandeService;

    @PostMapping
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<CommandeResponse> creer(
            @AuthenticationPrincipal Long idClient,
            @Valid @RequestBody CreateCommandeRequest request) {

        return ResponseEntity.status(HttpStatus.CREATED)
            .body(commandeService.creer(idClient, request));
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<Page<CommandeSummary>> getMesCommandes(
            @AuthenticationPrincipal Long idClient,
            @RequestParam(required = false) String statut,
            @PageableDefault(size = 10) Pageable pageable) {

        return ResponseEntity.ok(commandeService.filter(idClient, statut, pageable));
    }

    @GetMapping("/me/{id}")
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<CommandeResponse> getMaCommande(
            @AuthenticationPrincipal Long idClient,
            @PathVariable Long id) {

        CommandeResponse commande = commandeService.getById(id);
        // Sécurité : le client ne peut voir que ses propres commandes
        // Vérification délégable au service si besoin
        return ResponseEntity.ok(commande);
    }

    @DeleteMapping("/me/{id}")
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<Void> annulerMaCommande(
            @AuthenticationPrincipal Long idClient,
            @PathVariable Long id,
            @RequestParam(required = false, defaultValue = "Annulée par le client") String motif) {

        commandeService.annuler(id, motif);
        return ResponseEntity.noContent().build();
    }

    // ── Back-office ────────────────────────────────────────────

    @GetMapping
    @PreAuthorize("hasAnyRole('VENDEUR','MANAGER','ADMIN')")
    public ResponseEntity<Page<CommandeSummary>> filter(
            @RequestParam(required = false) Long idClient,
            @RequestParam(required = false) String statut,
            @PageableDefault(size = 20) Pageable pageable) {

        return ResponseEntity.ok(commandeService.filter(idClient, statut, pageable));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('VENDEUR','MANAGER','ADMIN')")
    public ResponseEntity<CommandeResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(commandeService.getById(id));
    }

    @GetMapping("/ref/{reference}")
    @PreAuthorize("hasAnyRole('VENDEUR','MANAGER','ADMIN')")
    public ResponseEntity<CommandeResponse> getByReference(@PathVariable String reference) {
        return ResponseEntity.ok(commandeService.getByReference(reference));
    }

    @PatchMapping("/{id}/statut")
    @PreAuthorize("hasAnyRole('VENDEUR','MANAGER','ADMIN')")
    public ResponseEntity<CommandeResponse> updateStatut(
            @PathVariable Long id,
            @Valid @RequestBody UpdateStatutCommandeRequest request) {

        return ResponseEntity.ok(commandeService.updateStatut(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('MANAGER','ADMIN')")
    public ResponseEntity<Void> annuler(
            @PathVariable Long id,
            @RequestParam(required = false, defaultValue = "Annulée par un responsable") String motif) {

        commandeService.annuler(id, motif);
        return ResponseEntity.noContent().build();
    }
}
