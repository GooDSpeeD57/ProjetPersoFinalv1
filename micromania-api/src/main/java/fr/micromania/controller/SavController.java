package fr.micromania.controller;

import fr.micromania.dto.sav.*;
import fr.micromania.service.SavService;
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
@RequestMapping("/api/v1/sav")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('VENDEUR','MANAGER','ADMIN')")
public class SavController {

    private final SavService savService;

    @PostMapping
    public ResponseEntity<DossierSavResponse> ouvrir(
            @Valid @RequestBody CreateDossierSavRequest request) {

        return ResponseEntity.status(HttpStatus.CREATED)
            .body(savService.ouvrir(request));
    }

    @GetMapping
    public ResponseEntity<Page<DossierSavResponse>> filter(
            @RequestParam(required = false) String statut,
            @RequestParam(required = false) Long idEmploye,
            @PageableDefault(size = 20) Pageable pageable) {

        return ResponseEntity.ok(savService.filter(statut, idEmploye, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DossierSavResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(savService.getById(id));
    }

    @GetMapping("/ref/{reference}")
    public ResponseEntity<DossierSavResponse> getByReference(@PathVariable String reference) {
        return ResponseEntity.ok(savService.getByReference(reference));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DossierSavResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateDossierSavRequest request) {

        return ResponseEntity.ok(savService.update(id, request));
    }

    @PatchMapping("/{id}/cloturer")
    public ResponseEntity<DossierSavResponse> cloturer(
            @PathVariable Long id,
            @RequestParam String solution) {

        return ResponseEntity.ok(savService.cloturer(id, solution));
    }
}
