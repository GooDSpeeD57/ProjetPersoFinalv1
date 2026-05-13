package fr.micromania.service;

import fr.micromania.dto.favori.FavoriMagasinResponse;
import fr.micromania.dto.favori.FavoriProduitResponse;

import java.util.List;

/**
 * Gestion des favoris produits et magasins d'un client.
 */
public interface FavoriService {

    List<FavoriProduitResponse> getFavorisProduits(Long idClient);

    FavoriProduitResponse ajouterFavoriProduit(Long idClient, Long idProduit);

    void supprimerFavoriProduit(Long idClient, Long idProduit);

    List<FavoriMagasinResponse> getFavorisMagasins(Long idClient);

    FavoriMagasinResponse ajouterFavoriMagasin(Long idClient, Long idMagasin);

    void supprimerFavoriMagasin(Long idClient, Long idMagasin);
}
