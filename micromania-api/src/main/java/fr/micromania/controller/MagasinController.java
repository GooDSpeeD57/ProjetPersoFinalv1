package fr.micromania.controller;

import fr.micromania.dto.magasin.CreateMagasinRequest;
import fr.micromania.dto.magasin.MagasinAdminResponse;
import fr.micromania.dto.magasin.MagasinProximiteResponse;
import fr.micromania.dto.magasin.MagasinPublicResponse;
import fr.micromania.dto.magasin.UpdateMagasinRequest;
import fr.micromania.entity.Magasin;
import fr.micromania.repository.MagasinRepository;
import fr.micromania.service.MagasinService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/v1/magasins")
@RequiredArgsConstructor
public class MagasinController {

    private final MagasinService    magasinService;
    private final MagasinRepository magasinRepository;

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

    // ── Endpoints admin (tous magasins y compris inactifs) ───────

    @GetMapping("/admin")
    @PreAuthorize("hasAnyRole('MANAGER','ADMIN')")
    public ResponseEntity<List<MagasinAdminResponse>> listerTous() {
        return ResponseEntity.ok(
                magasinRepository.findAll().stream().map(this::toAdmin).toList());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MagasinAdminResponse> creer(@Valid @RequestBody CreateMagasinRequest req) {
        Magasin m = Magasin.builder()
                .nom(req.nom())
                .telephone(req.telephone())
                .email(req.email())
                .actif(true)
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(toAdmin(magasinRepository.save(m)));
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MagasinAdminResponse> modifier(
            @PathVariable Long id,
            @Valid @RequestBody UpdateMagasinRequest req) {

        Magasin m = magasinRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Magasin introuvable"));

        if (req.nom()       != null) m.setNom(req.nom());
        if (req.telephone() != null) m.setTelephone(req.telephone());
        if (req.email()     != null) m.setEmail(req.email());
        if (req.actif()     != null) m.setActif(req.actif());

        return ResponseEntity.ok(toAdmin(magasinRepository.save(m)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> supprimer(@PathVariable Long id) {
        Magasin m = magasinRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Magasin introuvable"));
        m.setActif(false);
        magasinRepository.save(m);
        return ResponseEntity.noContent().build();
    }

    // ── Mapper privé ─────────────────────────────────────────────

    private MagasinAdminResponse toAdmin(Magasin m) {
        return new MagasinAdminResponse(
                m.getId(), m.getNom(), m.getTelephone(), m.getEmail(),
                m.isActif(), m.getDateCreation(), m.getDateModification());
    }
}
