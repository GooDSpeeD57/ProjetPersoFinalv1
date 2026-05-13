package fr.micromania.controller;

import fr.micromania.dto.magasin.CreateMagasinRequest;
import fr.micromania.dto.magasin.HoraireMagasinDto;
import fr.micromania.dto.magasin.HoraireMagasinRequest;
import fr.micromania.dto.magasin.MagasinAdminResponse;
import fr.micromania.dto.magasin.MagasinProximiteResponse;
import fr.micromania.dto.magasin.MagasinPublicResponse;
import fr.micromania.dto.magasin.UpdateMagasinRequest;
import fr.micromania.service.MagasinService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/magasins")
@RequiredArgsConstructor
public class MagasinController {

    private final MagasinService magasinService;

    // ── Endpoints publics (lecture) ──────────────────────────────

    @GetMapping
    public ResponseEntity<List<MagasinPublicResponse>> getMagasins(
            @RequestParam(required = false) String q) {
        return ResponseEntity.ok(magasinService.getMagasinsActifs(q));
    }

    @GetMapping("/proches")
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<List<MagasinProximiteResponse>> getMagasinsProches(
            @AuthenticationPrincipal Long idClient,
            @RequestParam Long idAdresse,
            @RequestParam(defaultValue = "5") Integer limit) {
        return ResponseEntity.ok(magasinService.getMagasinsProches(idClient, idAdresse, limit));
    }

    @GetMapping("/{idMagasin:\\d+}")
    public ResponseEntity<MagasinPublicResponse> getMagasinById(@PathVariable Long idMagasin) {
        return ResponseEntity.ok(magasinService.getMagasinActifById(idMagasin));
    }

    // ── Endpoints admin ───────────────────────────────────────────

    @GetMapping("/admin")
    @PreAuthorize("hasAnyRole('MANAGER','ADMIN')")
    public ResponseEntity<List<MagasinAdminResponse>> listerTous() {
        return ResponseEntity.ok(magasinService.listerTous());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MagasinAdminResponse> creer(@Valid @RequestBody CreateMagasinRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(magasinService.creerMagasin(req));
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MagasinAdminResponse> modifier(
            @PathVariable Long id,
            @Valid @RequestBody UpdateMagasinRequest req) {

        return ResponseEntity.ok(magasinService.modifierMagasin(id, req));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> supprimer(@PathVariable Long id) {
        magasinService.supprimerMagasin(id);
        return ResponseEntity.noContent().build();
    }

    // ── Horaires d'ouverture ─────────────────────────────────────

    @GetMapping("/{idMagasin:\\d+}/horaires")
    public ResponseEntity<List<HoraireMagasinDto>> getHoraires(@PathVariable Long idMagasin) {
        return ResponseEntity.ok(magasinService.getHoraires(idMagasin));
    }

    @PutMapping("/{idMagasin:\\d+}/horaires")
    @PreAuthorize("hasAnyRole('MANAGER','ADMIN')")
    public ResponseEntity<List<HoraireMagasinDto>> setHoraires(
            @PathVariable Long idMagasin,
            @Valid @RequestBody List<@Valid HoraireMagasinRequest> horaires) {

        return ResponseEntity.ok(magasinService.setHoraires(idMagasin, horaires));
    }

    @PatchMapping("/{idMagasin:\\d+}/horaires/{jour:[1-7]}")
    @PreAuthorize("hasAnyRole('MANAGER','ADMIN')")
    public ResponseEntity<HoraireMagasinDto> updateHoraire(
            @PathVariable Long idMagasin,
            @PathVariable int jour,
            @Valid @RequestBody HoraireMagasinRequest req) {

        return ResponseEntity.ok(magasinService.updateHoraire(idMagasin, jour, req));
    }
}
