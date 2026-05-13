package fr.micromania.controller;

import fr.micromania.dto.favori.FavoriMagasinResponse;
import fr.micromania.dto.favori.FavoriProduitResponse;
import fr.micromania.service.FavoriService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/clients/{idClient}/favoris")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('VENDEUR','MANAGER','ADMIN')")
public class FavoriController {

    private final FavoriService favoriService;

    // ── Favoris produits ──────────────────────────────────────

    @GetMapping("/produits")
    public ResponseEntity<List<FavoriProduitResponse>> getFavorisProduits(@PathVariable Long idClient) {
        return ResponseEntity.ok(favoriService.getFavorisProduits(idClient));
    }

    @PostMapping("/produits/{idProduit}")
    public ResponseEntity<FavoriProduitResponse> ajouterFavoriProduit(
            @PathVariable Long idClient,
            @PathVariable Long idProduit) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(favoriService.ajouterFavoriProduit(idClient, idProduit));
    }

    @DeleteMapping("/produits/{idProduit}")
    public ResponseEntity<Void> supprimerFavoriProduit(
            @PathVariable Long idClient,
            @PathVariable Long idProduit) {

        favoriService.supprimerFavoriProduit(idClient, idProduit);
        return ResponseEntity.noContent().build();
    }

    // ── Favoris magasins ──────────────────────────────────────

    @GetMapping("/magasins")
    public ResponseEntity<List<FavoriMagasinResponse>> getFavorisMagasins(@PathVariable Long idClient) {
        return ResponseEntity.ok(favoriService.getFavorisMagasins(idClient));
    }

    @PostMapping("/magasins/{idMagasin}")
    public ResponseEntity<FavoriMagasinResponse> ajouterFavoriMagasin(
            @PathVariable Long idClient,
            @PathVariable Long idMagasin) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(favoriService.ajouterFavoriMagasin(idClient, idMagasin));
    }

    @DeleteMapping("/magasins/{idMagasin}")
    public ResponseEntity<Void> supprimerFavoriMagasin(
            @PathVariable Long idClient,
            @PathVariable Long idMagasin) {

        favoriService.supprimerFavoriMagasin(idClient, idMagasin);
        return ResponseEntity.noContent().build();
    }
}
