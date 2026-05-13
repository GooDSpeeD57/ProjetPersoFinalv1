package fr.micromania.service.impl;

import fr.micromania.dto.favori.FavoriMagasinResponse;
import fr.micromania.dto.favori.FavoriProduitResponse;
import fr.micromania.entity.Client;
import fr.micromania.entity.FavoriMagasin;
import fr.micromania.entity.FavoriMagasinId;
import fr.micromania.entity.Magasin;
import fr.micromania.entity.catalog.FavoriProduit;
import fr.micromania.entity.catalog.FavoriProduitId;
import fr.micromania.entity.catalog.Produit;
import fr.micromania.repository.ClientRepository;
import fr.micromania.repository.FavoriMagasinRepository;
import fr.micromania.repository.FavoriProduitRepository;
import fr.micromania.repository.MagasinRepository;
import fr.micromania.repository.ProduitRepository;
import fr.micromania.service.FavoriService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FavoriServiceImpl implements FavoriService {

    private final FavoriProduitRepository favoriProduitRepository;
    private final FavoriMagasinRepository favoriMagasinRepository;
    private final ClientRepository        clientRepository;
    private final ProduitRepository       produitRepository;
    private final MagasinRepository       magasinRepository;

    // ─── Favoris produits ────────────────────────────────────────────────────

    @Override
    public List<FavoriProduitResponse> getFavorisProduits(Long idClient) {
        return favoriProduitRepository.findByClientId(idClient).stream()
                .map(f -> new FavoriProduitResponse(
                        f.getProduit().getId(),
                        f.getProduit().getNom(),
                        f.getProduit().getSlug(),
                        f.getDateAjout()
                ))
                .toList();
    }

    @Override
    @Transactional
    public FavoriProduitResponse ajouterFavoriProduit(Long idClient, Long idProduit) {
        Client  client  = clientRepository.findByIdAndDeletedFalse(idClient)
                .orElseThrow(() -> new EntityNotFoundException("Client introuvable : " + idClient));
        Produit produit = produitRepository.findByIdAndDeletedFalse(idProduit)
                .orElseThrow(() -> new EntityNotFoundException("Produit introuvable : " + idProduit));

        FavoriProduitId id = new FavoriProduitId(idClient, idProduit);
        if (favoriProduitRepository.existsById(id)) {
            FavoriProduit existing = favoriProduitRepository.findById(id).orElseThrow();
            return toProduitResponse(existing);
        }

        FavoriProduit favori = FavoriProduit.builder()
                .id(id)
                .client(client)
                .produit(produit)
                .build();
        return toProduitResponse(favoriProduitRepository.save(favori));
    }

    @Override
    @Transactional
    public void supprimerFavoriProduit(Long idClient, Long idProduit) {
        FavoriProduitId id = new FavoriProduitId(idClient, idProduit);
        if (!favoriProduitRepository.existsById(id)) {
            throw new EntityNotFoundException(
                    "Favori produit introuvable : client=" + idClient + " produit=" + idProduit);
        }
        favoriProduitRepository.deleteById(id);
    }

    // ─── Favoris magasins ────────────────────────────────────────────────────

    @Override
    public List<FavoriMagasinResponse> getFavorisMagasins(Long idClient) {
        return favoriMagasinRepository.findByClientId(idClient).stream()
                .map(f -> new FavoriMagasinResponse(
                        f.getMagasin().getId(),
                        f.getMagasin().getNom(),
                        f.getDateAjout(),
                        f.isPrincipal()
                ))
                .toList();
    }

    @Override
    @Transactional
    public FavoriMagasinResponse ajouterFavoriMagasin(Long idClient, Long idMagasin) {
        Client  client  = clientRepository.findByIdAndDeletedFalse(idClient)
                .orElseThrow(() -> new EntityNotFoundException("Client introuvable : " + idClient));
        Magasin magasin = magasinRepository.findByIdAndActifTrue(idMagasin)
                .orElseThrow(() -> new EntityNotFoundException("Magasin introuvable : " + idMagasin));

        FavoriMagasinId id = new FavoriMagasinId(idClient, idMagasin);
        if (favoriMagasinRepository.existsById(id)) {
            FavoriMagasin existing = favoriMagasinRepository.findById(id).orElseThrow();
            return toMagasinResponse(existing);
        }

        FavoriMagasin favori = FavoriMagasin.builder()
                .id(id)
                .client(client)
                .magasin(magasin)
                .principal(false)
                .build();
        return toMagasinResponse(favoriMagasinRepository.save(favori));
    }

    @Override
    @Transactional
    public void supprimerFavoriMagasin(Long idClient, Long idMagasin) {
        FavoriMagasinId id = new FavoriMagasinId(idClient, idMagasin);
        if (!favoriMagasinRepository.existsById(id)) {
            throw new EntityNotFoundException(
                    "Favori magasin introuvable : client=" + idClient + " magasin=" + idMagasin);
        }
        favoriMagasinRepository.deleteById(id);
    }

    // ─── Helpers ─────────────────────────────────────────────────────────────

    private FavoriProduitResponse toProduitResponse(FavoriProduit f) {
        return new FavoriProduitResponse(
                f.getProduit().getId(),
                f.getProduit().getNom(),
                f.getProduit().getSlug(),
                f.getDateAjout()
        );
    }

    private FavoriMagasinResponse toMagasinResponse(FavoriMagasin f) {
        return new FavoriMagasinResponse(
                f.getMagasin().getId(),
                f.getMagasin().getNom(),
                f.getDateAjout(),
                f.isPrincipal()
        );
    }
}
