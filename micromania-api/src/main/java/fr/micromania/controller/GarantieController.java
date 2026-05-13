package fr.micromania.controller;

import fr.micromania.dto.garantie.ExtensionGarantieRequest;
import fr.micromania.dto.garantie.ExtensionGarantieResponse;
import fr.micromania.dto.garantie.GarantieResponse;
import fr.micromania.service.GarantieService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/garanties")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('VENDEUR','MANAGER','ADMIN')")
public class GarantieController {

    private final GarantieService garantieService;

    /** Toutes les garanties d'un client — UN item par Garantie. */
    @GetMapping
    public ResponseEntity<List<GarantieResponse>> getByClientId(@RequestParam Long clientId) {
        return ResponseEntity.ok(garantieService.getByClientId(clientId));
    }

    @GetMapping("/vente-unite/{idVenteUnite}")
    public ResponseEntity<GarantieResponse> getByVenteUnite(@PathVariable Long idVenteUnite) {
        return ResponseEntity.ok(garantieService.getByVenteUniteId(idVenteUnite));
    }

    @GetMapping("/{id}")
    public ResponseEntity<GarantieResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(garantieService.getById(id));
    }

    @PostMapping("/{id}/extensions")
    public ResponseEntity<ExtensionGarantieResponse> ajouterExtension(
            @PathVariable Long id,
            @Valid @RequestBody ExtensionGarantieRequest request) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(garantieService.ajouterExtension(id, request));
    }
}
