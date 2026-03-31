package fr.micromania.controller;

import fr.micromania.dto.reprise.*;
import fr.micromania.service.RepriseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/reprises")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('VENDEUR','MANAGER','ADMIN')")
public class RepriseController {

    private final RepriseService repriseService;

    @PostMapping
    public ResponseEntity<RepriseResponse> creer(
            @Valid @RequestBody CreateRepriseRequest request) {

        return ResponseEntity.status(HttpStatus.CREATED)
            .body(repriseService.creer(request));
    }

    @GetMapping
    public ResponseEntity<Page<RepriseResponse>> filter(
            @RequestParam(required = false) Long idMagasin,
            @RequestParam(required = false) String statut,
            @RequestParam(required = false) Long idClient,
            @PageableDefault(size = 20) Pageable pageable) {

        return ResponseEntity.ok(repriseService.filter(idMagasin, statut, idClient, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RepriseResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(repriseService.getById(id));
    }

    @GetMapping("/ref/{reference}")
    public ResponseEntity<RepriseResponse> getByReference(@PathVariable String reference) {
        return ResponseEntity.ok(repriseService.getByReference(reference));
    }

    @PatchMapping("/{id}/lignes/valider")
    public ResponseEntity<RepriseResponse> validerLigne(
            @PathVariable Long id,
            @Valid @RequestBody ValiderRepriseLigneRequest request) {

        return ResponseEntity.ok(repriseService.validerLigne(id, request));
    }

    @PatchMapping("/{id}/valider")
    @PreAuthorize("hasAnyRole('MANAGER','ADMIN')")
    public ResponseEntity<RepriseResponse> valider(@PathVariable Long id) {
        return ResponseEntity.ok(repriseService.valider(id));
    }

    @PatchMapping("/{id}/refuser")
    @PreAuthorize("hasAnyRole('MANAGER','ADMIN')")
    public ResponseEntity<RepriseResponse> refuser(
            @PathVariable Long id,
            @RequestParam String motif) {

        return ResponseEntity.ok(repriseService.refuser(id, motif));
    }
}
