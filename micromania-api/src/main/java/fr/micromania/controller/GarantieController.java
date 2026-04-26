package fr.micromania.controller;

import fr.micromania.dto.garantie.ExtensionGarantieRequest;
import fr.micromania.dto.garantie.ExtensionGarantieResponse;
import fr.micromania.dto.garantie.GarantieResponse;
import fr.micromania.entity.ExtensionGarantie;
import fr.micromania.entity.Garantie;
import fr.micromania.entity.referentiel.TypeGarantie;
import fr.micromania.repository.ExtensionGarantieRepository;
import fr.micromania.repository.GarantieRepository;
import fr.micromania.repository.TypeGarantieRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/garanties")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('VENDEUR','MANAGER','ADMIN')")
public class GarantieController {

    private final GarantieRepository garantieRepository;
    private final ExtensionGarantieRepository extensionGarantieRepository;
    private final TypeGarantieRepository typeGarantieRepository;

    /** Toutes les garanties d'un client (pour la vue JavaFX Garanties). */
    @GetMapping
    public ResponseEntity<List<GarantieResponse>> getByClientId(@RequestParam Long clientId) {
        return ResponseEntity.ok(
            garantieRepository.findByClientId(clientId).stream()
                .map(this::toGarantieResponse)
                .toList()
        );
    }

    @GetMapping("/vente-unite/{idVenteUnite}")
    public ResponseEntity<GarantieResponse> getByVenteUnite(@PathVariable Long idVenteUnite) {
        Garantie garantie = garantieRepository.findByVenteUniteId(idVenteUnite)
                .orElseThrow(() -> new EntityNotFoundException("Garantie introuvable pour la vente unité : " + idVenteUnite));
        return ResponseEntity.ok(toGarantieResponse(garantie));
    }

    @GetMapping("/{id}")
    public ResponseEntity<GarantieResponse> getById(@PathVariable Long id) {
        Garantie garantie = garantieRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Garantie introuvable : " + id));
        return ResponseEntity.ok(toGarantieResponse(garantie));
    }

    @PostMapping("/{id}/extensions")
    @Transactional
    public ResponseEntity<ExtensionGarantieResponse> ajouterExtension(
            @PathVariable Long id,
            @Valid @RequestBody ExtensionGarantieRequest request) {

        Garantie garantie = garantieRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Garantie introuvable : " + id));
        TypeGarantie typeGarantie = typeGarantieRepository.findById(request.idTypeGarantie())
                .orElseThrow(() -> new EntityNotFoundException("Type de garantie introuvable : " + request.idTypeGarantie()));

        ExtensionGarantie extension = ExtensionGarantie.builder()
                .garantie(garantie)
                .typeGarantie(typeGarantie)
                .dateFinEtendue(request.dateFinEtendue())
                .build();
        extension = extensionGarantieRepository.save(extension);

        garantie.setEstEtendue(true);
        garantie.setDateExtension(LocalDate.now());
        garantieRepository.save(garantie);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(toExtensionResponse(extension));
    }

    // ── Helpers ───────────────────────────────────────────────

    private GarantieResponse toGarantieResponse(Garantie g) {
        return new GarantieResponse(
                g.getId(),
                g.getVenteUnite() != null ? g.getVenteUnite().getId() : null,
                g.getTypeGarantie() != null ? g.getTypeGarantie().getCode() : null,
                g.getTypeGarantie() != null ? g.getTypeGarantie().getDescription() : null,
                g.getTypeGarantie() != null ? g.getTypeGarantie().getDureeMois() : null,
                g.getDateDebut(),
                g.getDateFin(),
                g.isEstEtendue(),
                g.getDateExtension()
        );
    }

    private ExtensionGarantieResponse toExtensionResponse(ExtensionGarantie e) {
        return new ExtensionGarantieResponse(
                e.getId(),
                e.getGarantie() != null ? e.getGarantie().getId() : null,
                e.getTypeGarantie() != null ? e.getTypeGarantie().getCode() : null,
                e.getDateAchat(),
                e.getDateFinEtendue()
        );
    }
}
