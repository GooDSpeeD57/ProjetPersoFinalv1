package fr.micromania.controller;

import fr.micromania.dto.commande.CommandeResponse;
import fr.micromania.dto.precommande.*;
import fr.micromania.service.PrecommandeService;
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
@RequestMapping("/api/v1/precommandes")
@RequiredArgsConstructor
public class PrecommandeController {

    private final PrecommandeService precommandeService;

    @PostMapping
    @PreAuthorize("hasAnyRole('VENDEUR','MANAGER','ADMIN')")
    public ResponseEntity<PrecommandeResponse> creer(
            @Valid @RequestBody CreatePrecommandeRequest request) {

        return ResponseEntity.status(HttpStatus.CREATED)
            .body(precommandeService.creer(request));
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<Page<PrecommandeResponse>> getMesPrecommandes(
            @AuthenticationPrincipal Long idClient,
            @PageableDefault(size = 10) Pageable pageable) {

        return ResponseEntity.ok(precommandeService.getByClient(idClient, pageable));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('MANAGER','ADMIN')")
    public ResponseEntity<Page<PrecommandeResponse>> filter(
            @RequestParam(required = false) String statut,
            @PageableDefault(size = 20) Pageable pageable) {

        return ResponseEntity.ok(precommandeService.getByStatut(statut, pageable));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('VENDEUR','MANAGER','ADMIN')")
    public ResponseEntity<PrecommandeResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(precommandeService.getById(id));
    }

    @PostMapping("/{id}/convertir")
    @PreAuthorize("hasAnyRole('VENDEUR','MANAGER','ADMIN')")
    public ResponseEntity<CommandeResponse> convertir(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(precommandeService.convertirEnCommande(id));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('MANAGER','ADMIN')")
    public ResponseEntity<Void> annuler(
            @PathVariable Long id,
            @RequestParam(defaultValue = "Annulée") String motif) {

        precommandeService.annuler(id, motif);
        return ResponseEntity.noContent().build();
    }
}
