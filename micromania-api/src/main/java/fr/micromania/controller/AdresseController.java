package fr.micromania.controller;

import fr.micromania.dto.client.AdresseRequest;
import fr.micromania.dto.client.AdresseResponse;
import fr.micromania.service.AdresseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/clients/{idClient}/adresses")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('VENDEUR','MANAGER','ADMIN')")
public class AdresseController {

    private final AdresseService adresseService;

    /** Liste toutes les adresses d'un client. */
    @GetMapping
    public ResponseEntity<List<AdresseResponse>> lister(@PathVariable Long idClient) {
        return ResponseEntity.ok(adresseService.getByClient(idClient));
    }

    /** Ajoute une adresse à un client. */
    @PostMapping
    public ResponseEntity<AdresseResponse> ajouter(
            @PathVariable Long idClient,
            @Valid @RequestBody AdresseRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(adresseService.ajouter(idClient, request));
    }

    /** Modifie une adresse existante (PATCH partiel). */
    @PutMapping("/{idAdresse}")
    public ResponseEntity<AdresseResponse> modifier(
            @PathVariable Long idClient,
            @PathVariable Long idAdresse,
            @Valid @RequestBody AdresseRequest request) {
        return ResponseEntity.ok(adresseService.modifier(idAdresse, idClient, request));
    }

    /** Supprime une adresse (interdit si c'est l'adresse par défaut). */
    @DeleteMapping("/{idAdresse}")
    public ResponseEntity<Void> supprimer(
            @PathVariable Long idClient,
            @PathVariable Long idAdresse) {
        adresseService.supprimer(idAdresse, idClient);
        return ResponseEntity.noContent().build();
    }

    /** Définit une adresse comme adresse par défaut. */
    @PatchMapping("/{idAdresse}/defaut")
    public ResponseEntity<Void> setDefaut(
            @PathVariable Long idClient,
            @PathVariable Long idAdresse) {
        adresseService.setDefaut(idAdresse, idClient);
        return ResponseEntity.noContent().build();
    }
}
