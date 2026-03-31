package fr.micromania.controller;

import fr.micromania.dto.tcg.*;
import fr.micromania.service.TcgService;
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
@RequestMapping("/api/v1/tcg")
@RequiredArgsConstructor
public class TcgController {

    private final TcgService tcgService;

    @GetMapping("/cartes")
    public ResponseEntity<Page<TcgCarteSummary>> search(
            @RequestParam(required = false) Long idMagasin,
            @RequestParam(required = false) String nomCarte,
            @RequestParam(required = false) String etat,
            @RequestParam(required = false) String langue,
            @RequestParam(required = false) Boolean foil,
            @RequestParam(required = false) Long idJeu,
            @PageableDefault(size = 24) Pageable pageable) {

        return ResponseEntity.ok(tcgService.search(idMagasin, nomCarte, etat, langue, foil, idJeu, pageable));
    }

    @GetMapping("/cartes/{id}")
    public ResponseEntity<TcgCarteResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(tcgService.getById(id));
    }

    @PostMapping("/cartes")
    @PreAuthorize("hasAnyRole('VENDEUR','MANAGER','ADMIN')")
    public ResponseEntity<TcgCarteResponse> ajouter(
            @Valid @RequestBody AddTcgCarteInventaireRequest request) {

        return ResponseEntity.status(HttpStatus.CREATED)
            .body(tcgService.ajouter(request));
    }

    @PatchMapping("/cartes/{id}/vendu")
    @PreAuthorize("hasAnyRole('VENDEUR','MANAGER','ADMIN')")
    public ResponseEntity<Void> marquerVendu(@PathVariable Long id) {
        tcgService.marquerVendu(id);
        return ResponseEntity.noContent().build();
    }
}
