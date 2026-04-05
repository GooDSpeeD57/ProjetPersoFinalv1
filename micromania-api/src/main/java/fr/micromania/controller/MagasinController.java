package fr.micromania.controller;

import fr.micromania.dto.magasin.MagasinProximiteResponse;
import fr.micromania.dto.magasin.MagasinPublicResponse;
import fr.micromania.service.MagasinService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/magasins")
@RequiredArgsConstructor
public class MagasinController {

    private final MagasinService magasinService;

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
}
