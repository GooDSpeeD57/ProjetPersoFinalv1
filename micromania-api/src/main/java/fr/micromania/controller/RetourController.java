package fr.micromania.controller;

import fr.micromania.dto.retour.*;
import fr.micromania.service.RetourService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/retours")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('VENDEUR','MANAGER','ADMIN')")
public class RetourController {

    private final RetourService retourService;

    @PostMapping
    public ResponseEntity<RetourResponse> creer(
            @Valid @RequestBody CreateRetourRequest request) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(retourService.creer(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RetourResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(retourService.getById(id));
    }

    @GetMapping
    public ResponseEntity<List<RetourResponse>> getByFacture(
            @RequestParam Long idFacture) {

        return ResponseEntity.ok(retourService.getByFacture(idFacture));
    }

    @PatchMapping("/{id}/statut")
    public ResponseEntity<RetourResponse> updateStatut(
            @PathVariable Long id,
            @Valid @RequestBody UpdateStatutRetourRequest request) {

        return ResponseEntity.ok(retourService.updateStatut(id, request));
    }
}
