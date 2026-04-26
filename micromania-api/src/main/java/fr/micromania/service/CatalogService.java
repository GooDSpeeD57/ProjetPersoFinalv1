package fr.micromania.service;

import fr.micromania.dto.catalog.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;



public interface CatalogService {

    ProduitResponse getProduitById(Long id);

    ProduitResponse getProduitBySlug(String slug);

    AvisProduitClientResponse getMonAvisProduit(Long idClient, Long idProduit);

    boolean peutSoumettreAvis(Long idClient, Long idProduit);

    AvisProduitClientResponse soumettreAvisProduit(Long idClient, Long idProduit, CreateAvisProduitRequest request);

    Page<ProduitSummary> search(String query, Long idCategorie, String niveauAccesMin,
                               String plateforme, String famille, String etat, String tri, Pageable pageable);

    List<ProduitSummary> getMisEnAvant();

    /** Catalogue public : une entrée par variant actif (1 variant = 1 vignette). */
    Page<VariantCatalogueSummary> searchCatalogue(String q, Long idCategorie,
                                                   String plateforme, String famille,
                                                   String etat, String edition,
                                                   String tri, Pageable pageable);

    ProduitResponse creerProduit(CreateProduitRequest request);

    ProduitResponse modifierProduit(Long id, CreateProduitRequest request);

    void supprimerProduit(Long id);

    ProduitVariantResponse creerVariant(CreateVariantRequest request);

    ProduitVariantResponse modifierVariant(Long idVariant, CreateVariantRequest request);

    ProduitVariantResponse toggleActifVariant(Long idVariant, boolean actif);

    PrixResponse setPrice(SetPrixRequest request);

    List<CategorieResponse> getCategories();

    CategorieResponse getCategorieById(Long id);

    Page<CataloguePosSummary> getCataloguePOS(Long idMagasin, String q, String plateforme, String etat, Pageable pageable);

    ProduitImageDto ajouterImage(Long idVariant, CreateProduitImageRequest request);

    /** Upload physique d'un fichier image → sauvegarde disque + enregistrement en base. */
    ProduitImageDto uploadImage(Long idVariant, MultipartFile file, String alt, boolean principale);

    /** Modification partielle d'une image existante (url, alt, principale). */
    ProduitImageDto modifierImage(Long idVariant, Long imageId, UpdateProduitImageRequest request);

    void supprimerImage(Long idVariant, Long imageId);

    List<ProduitVideoResponse> getVideos(Long idProduit);

    ProduitVideoResponse ajouterVideo(Long idProduit, CreateProduitVideoRequest request);

    void supprimerVideo(Long idProduit, Long idVideo);

    // ── Screenshots (niveau produit, communs à tous les variants) ────────────
    List<ScreenshotDto> getScreenshots(Long idProduit);

    ScreenshotDto ajouterScreenshot(Long idProduit, CreateScreenshotRequest request);

    ScreenshotDto uploadScreenshot(Long idProduit, MultipartFile file, String alt, int ordre);

    void supprimerScreenshot(Long idProduit, Long idScreenshot);
}
