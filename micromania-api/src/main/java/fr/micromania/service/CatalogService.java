package fr.micromania.service;

import fr.micromania.dto.catalog.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface CatalogService {

    ProduitResponse getProduitById(Long id);

    ProduitResponse getProduitBySlug(String slug);

    AvisProduitClientResponse getMonAvisProduit(Long idClient, Long idProduit);

    AvisProduitClientResponse soumettreAvisProduit(Long idClient, Long idProduit, CreateAvisProduitRequest request);

    Page<ProduitSummary> search(String query, Long idCategorie, String niveauAccesMin, Pageable pageable);

    List<ProduitSummary> getMisEnAvant();

    ProduitResponse creerProduit(CreateProduitRequest request);

    ProduitResponse modifierProduit(Long id, CreateProduitRequest request);

    void supprimerProduit(Long id);

    ProduitVariantResponse creerVariant(CreateVariantRequest request);

    ProduitVariantResponse modifierVariant(Long idVariant, CreateVariantRequest request);

    PrixResponse setPrice(SetPrixRequest request);

    List<CategorieResponse> getCategories();

    CategorieResponse getCategorieById(Long id);
}
