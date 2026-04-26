package fr.micromania.controller;

import fr.micromania.dto.favori.FavoriMagasinResponse;
import fr.micromania.dto.favori.FavoriProduitResponse;
import fr.micromania.entity.FavoriMagasin;
import fr.micromania.entity.FavoriMagasinId;
import fr.micromania.entity.Magasin;
import fr.micromania.entity.Client;
import fr.micromania.entity.catalog.FavoriProduit;
import fr.micromania.entity.catalog.FavoriProduitId;
import fr.micromania.entity.catalog.Produit;
import fr.micromania.repository.ClientRepository;
import fr.micromania.repository.FavoriMagasinRepository;
import fr.micromania.repository.FavoriProduitRepository;
import fr.micromania.repository.MagasinRepository;
import fr.micromania.repository.ProduitRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/clients/{idClient}/favoris")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('VENDEUR','MANAGER','ADMIN')")
public class FavoriController {

    private final FavoriProduitRepository favoriProduitRepository;
    private final FavoriMagasinRepository favoriMagasinRepository;
    private final ClientRepository clientRepository;
    private final ProduitRepository produitRepository;
    private final MagasinRepository magasinRepository;

    // ── Favoris produits ──────────────────────────────────────

    @GetMapping("/produits")
    public ResponseEntity<List<FavoriProduitResponse>> getFavorisProduits(@PathVariable Long idClient) {
        List<FavoriProduitResponse> result = favoriProduitRepository.findByClientId(idClient).stream()
                .map(f -> new FavoriProduitResponse(
                        f.getProduit().getId(),
                        f.getProduit().getNom(),
                        f.getProduit().getSlug(),
                        f.getDateAjout()
                ))
                .toList();
        return ResponseEntity.ok(result);
    }

    @PostMapping("/produits/{idProduit}")
    @Transactional
    public ResponseEntity<FavoriProduitResponse> ajouterFavoriProduit(
            @PathVariable Long idClient,
            @PathVariable Long idProduit) {

        Client client = clientRepository.findByIdAndDeletedFalse(idClient)
                .orElseThrow(() -> new EntityNotFoundException("Client introuvable : " + idClient));
        Produit produit = produitRepository.findByIdAndDeletedFalse(idProduit)
                .orElseThrow(() -> new EntityNotFoundException("Produit introuvable : " + idProduit));

        FavoriProduitId id = new FavoriProduitId(idClient, idProduit);
        if (favoriProduitRepository.existsById(id)) {
            FavoriProduit existing = favoriProduitRepository.findById(id).orElseThrow();
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new FavoriProduitResponse(
                            existing.getProduit().getId(),
                            existing.getProduit().getNom(),
                            existing.getProduit().getSlug(),
                            existing.getDateAjout()
                    ));
        }

        FavoriProduit favori = FavoriProduit.builder()
                .id(id)
                .client(client)
                .produit(produit)
                .build();
        favori = favoriProduitRepository.save(favori);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new FavoriProduitResponse(
                        favori.getProduit().getId(),
                        favori.getProduit().getNom(),
                        favori.getProduit().getSlug(),
                        favori.getDateAjout()
                ));
    }

    @DeleteMapping("/produits/{idProduit}")
    @Transactional
    public ResponseEntity<Void> supprimerFavoriProduit(
            @PathVariable Long idClient,
            @PathVariable Long idProduit) {

        FavoriProduitId id = new FavoriProduitId(idClient, idProduit);
        if (!favoriProduitRepository.existsById(id)) {
            throw new EntityNotFoundException("Favori produit introuvable : client=" + idClient + " produit=" + idProduit);
        }
        favoriProduitRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // ── Favoris magasins ──────────────────────────────────────

    @GetMapping("/magasins")
    public ResponseEntity<List<FavoriMagasinResponse>> getFavorisMagasins(@PathVariable Long idClient) {
        List<FavoriMagasinResponse> result = favoriMagasinRepository.findByClientId(idClient).stream()
                .map(f -> new FavoriMagasinResponse(
                        f.getMagasin().getId(),
                        f.getMagasin().getNom(),
                        f.getDateAjout(),
                        f.isPrincipal()
                ))
                .toList();
        return ResponseEntity.ok(result);
    }

    @PostMapping("/magasins/{idMagasin}")
    @Transactional
    public ResponseEntity<FavoriMagasinResponse> ajouterFavoriMagasin(
            @PathVariable Long idClient,
            @PathVariable Long idMagasin) {

        Client client = clientRepository.findByIdAndDeletedFalse(idClient)
                .orElseThrow(() -> new EntityNotFoundException("Client introuvable : " + idClient));
        Magasin magasin = magasinRepository.findByIdAndActifTrue(idMagasin)
                .orElseThrow(() -> new EntityNotFoundException("Magasin introuvable : " + idMagasin));

        FavoriMagasinId id = new FavoriMagasinId(idClient, idMagasin);
        if (favoriMagasinRepository.existsById(id)) {
            FavoriMagasin existing = favoriMagasinRepository.findById(id).orElseThrow();
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new FavoriMagasinResponse(
                            existing.getMagasin().getId(),
                            existing.getMagasin().getNom(),
                            existing.getDateAjout(),
                            existing.isPrincipal()
                    ));
        }

        FavoriMagasin favori = FavoriMagasin.builder()
                .id(id)
                .client(client)
                .magasin(magasin)
                .principal(false)
                .build();
        favori = favoriMagasinRepository.save(favori);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new FavoriMagasinResponse(
                        favori.getMagasin().getId(),
                        favori.getMagasin().getNom(),
                        favori.getDateAjout(),
                        favori.isPrincipal()
                ));
    }

    @DeleteMapping("/magasins/{idMagasin}")
    @Transactional
    public ResponseEntity<Void> supprimerFavoriMagasin(
            @PathVariable Long idClient,
            @PathVariable Long idMagasin) {

        FavoriMagasinId id = new FavoriMagasinId(idClient, idMagasin);
        if (!favoriMagasinRepository.existsById(id)) {
            throw new EntityNotFoundException("Favori magasin introuvable : client=" + idClient + " magasin=" + idMagasin);
        }
        favoriMagasinRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
