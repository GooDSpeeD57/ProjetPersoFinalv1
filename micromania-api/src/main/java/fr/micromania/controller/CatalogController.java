package fr.micromania.controller;

import fr.micromania.dto.catalog.*;
import fr.micromania.service.CatalogService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class CatalogController {

    private final CatalogService catalogService;

    // ── Catégories (public) ────────────────────────────────────

    @GetMapping("/categories")
    public ResponseEntity<List<CategorieResponse>> getCategories() {
        return ResponseEntity.ok(catalogService.getCategories());
    }

    @GetMapping("/categories/{id}")
    public ResponseEntity<CategorieResponse> getCategorie(@PathVariable Long id) {
        return ResponseEntity.ok(catalogService.getCategorieById(id));
    }

    // ── Produits (public) ──────────────────────────────────────

    @GetMapping("/produits")
    public ResponseEntity<Page<ProduitSummary>> search(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) Long categorie,
            @RequestParam(required = false) String niveau,
            @RequestParam(required = false) String plateforme,
            @RequestParam(required = false) String famille,
            @RequestParam(required = false) String etat,
            @RequestParam(required = false) String tri,
            @PageableDefault(size = 24) Pageable pageable) {

        return ResponseEntity.ok(catalogService.search(q, categorie, niveau, plateforme, famille, etat, tri, pageable));
    }

    @GetMapping("/produits/mis-en-avant")
    public ResponseEntity<List<ProduitSummary>> getMisEnAvant() {
        return ResponseEntity.ok(catalogService.getMisEnAvant());
    }

    /** Catalogue public : 1 vignette par variant. */
    @GetMapping("/catalogue")
    public ResponseEntity<Page<VariantCatalogueSummary>> catalogue(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) Long   categorie,
            @RequestParam(required = false) String plateforme,
            @RequestParam(required = false) String famille,
            @RequestParam(required = false) String etat,
            @RequestParam(required = false) String edition,
            @RequestParam(required = false) String tri,
            @PageableDefault(size = 24) Pageable pageable) {

        return ResponseEntity.ok(
                catalogService.searchCatalogue(q, categorie, plateforme, famille, etat, edition, tri, pageable));
    }

    @GetMapping("/produits/{id}")
    public ResponseEntity<ProduitResponse> getProduitById(@PathVariable Long id) {
        return ResponseEntity.ok(catalogService.getProduitById(id));
    }

    @GetMapping("/produits/slug/{slug}")
    public ResponseEntity<ProduitResponse> getProduitBySlug(@PathVariable String slug) {
        return ResponseEntity.ok(catalogService.getProduitBySlug(slug));
    }


    @GetMapping("/produits/{idProduit}/avis/eligible")
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<Map<String, Boolean>> peutSoumettreAvis(
            @AuthenticationPrincipal Long idClient,
            @PathVariable Long idProduit) {

        boolean eligible = catalogService.peutSoumettreAvis(idClient, idProduit);
        return ResponseEntity.ok(Map.of("eligible", eligible));
    }

    @GetMapping("/produits/{idProduit}/avis/me")
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<AvisProduitClientResponse> getMonAvisProduit(
            @AuthenticationPrincipal Long idClient,
            @PathVariable Long idProduit) {

        AvisProduitClientResponse avis = catalogService.getMonAvisProduit(idClient, idProduit);
        return avis == null ? ResponseEntity.noContent().build() : ResponseEntity.ok(avis);
    }

    @PostMapping("/produits/{idProduit}/avis")
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<AvisProduitClientResponse> soumettreAvisProduit(
            @AuthenticationPrincipal Long idClient,
            @PathVariable Long idProduit,
            @Valid @RequestBody CreateAvisProduitRequest request) {

        return ResponseEntity.status(HttpStatus.CREATED)
            .body(catalogService.soumettreAvisProduit(idClient, idProduit, request));
    }

    // ── Catalogue POS (employés) ──────────────────────────────

    @GetMapping("/produits/catalogue-pos")
    @PreAuthorize("hasAnyRole('VENDEUR','MANAGER','ADMIN')")
    public ResponseEntity<Page<CataloguePosSummary>> getCataloguePOS(
            @RequestParam Long idMagasin,
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String plateforme,
            @RequestParam(required = false) String etat,
            @PageableDefault(size = 50) Pageable pageable) {

        return ResponseEntity.ok(catalogService.getCataloguePOS(idMagasin, q, plateforme, etat, pageable));
    }

    // ── Gestion catalogue (back-office) ───────────────────────

    @PostMapping("/produits")
    @PreAuthorize("hasAnyRole('MANAGER','ADMIN')")
    public ResponseEntity<ProduitResponse> creerProduit(
            @Valid @RequestBody CreateProduitRequest request) {

        return ResponseEntity.status(HttpStatus.CREATED)
            .body(catalogService.creerProduit(request));
    }

    @PutMapping("/produits/{id}")
    @PreAuthorize("hasAnyRole('MANAGER','ADMIN')")
    public ResponseEntity<ProduitResponse> modifierProduit(
            @PathVariable Long id,
            @Valid @RequestBody CreateProduitRequest request) {

        return ResponseEntity.ok(catalogService.modifierProduit(id, request));
    }

    @DeleteMapping("/produits/{id}")
    @PreAuthorize("hasAnyRole('MANAGER','ADMIN')")
    public ResponseEntity<Void> supprimerProduit(@PathVariable Long id) {
        catalogService.supprimerProduit(id);
        return ResponseEntity.noContent().build();
    }

    // ── Variantes ──────────────────────────────────────────────

    @PostMapping("/variants")
    @PreAuthorize("hasAnyRole('MANAGER','ADMIN')")
    public ResponseEntity<ProduitVariantResponse> creerVariant(
            @Valid @RequestBody CreateVariantRequest request) {

        return ResponseEntity.status(HttpStatus.CREATED)
            .body(catalogService.creerVariant(request));
    }

    @PutMapping("/variants/{id}")
    @PreAuthorize("hasAnyRole('MANAGER','ADMIN')")
    public ResponseEntity<ProduitVariantResponse> modifierVariant(
            @PathVariable Long id,
            @Valid @RequestBody CreateVariantRequest request) {

        return ResponseEntity.ok(catalogService.modifierVariant(id, request));
    }

    @PatchMapping("/variants/{id}/actif")
    @PreAuthorize("hasAnyRole('MANAGER','ADMIN')")
    public ResponseEntity<ProduitVariantResponse> toggleActif(
            @PathVariable Long id,
            @RequestBody ToggleActifRequest request) {

        return ResponseEntity.ok(catalogService.toggleActifVariant(id, request.actif()));
    }

    // ── Prix ───────────────────────────────────────────────────

    @PostMapping("/prix")
    @PreAuthorize("hasAnyRole('MANAGER','ADMIN')")
    public ResponseEntity<PrixResponse> setPrice(
            @Valid @RequestBody SetPrixRequest request) {

        return ResponseEntity.status(HttpStatus.CREATED)
            .body(catalogService.setPrice(request));
    }

    // ── Images variant ─────────────────────────────────────────

    @PostMapping("/variants/{id}/images")
    @PreAuthorize("hasAnyRole('MANAGER','ADMIN')")
    public ResponseEntity<ProduitImageDto> ajouterImage(
            @PathVariable Long id,
            @Valid @RequestBody CreateProduitImageRequest request) {

        return ResponseEntity.status(HttpStatus.CREATED)
            .body(catalogService.ajouterImage(id, request));
    }

    @PostMapping(value = "/variants/{id}/images/upload", consumes = org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('MANAGER','ADMIN')")
    public ResponseEntity<ProduitImageDto> uploadImage(
            @PathVariable Long id,
            @RequestPart("file") org.springframework.web.multipart.MultipartFile file,
            @RequestParam(defaultValue = "")    String  alt,
            @RequestParam(defaultValue = "false") boolean principale) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(catalogService.uploadImage(id, file, alt, principale));
    }

    @PatchMapping("/variants/{id}/images/{imageId}")
    @PreAuthorize("hasAnyRole('MANAGER','ADMIN')")
    public ResponseEntity<ProduitImageDto> modifierImage(
            @PathVariable Long id,
            @PathVariable Long imageId,
            @Valid @RequestBody UpdateProduitImageRequest request) {

        return ResponseEntity.ok(catalogService.modifierImage(id, imageId, request));
    }

    @DeleteMapping("/variants/{id}/images/{imageId}")
    @PreAuthorize("hasAnyRole('MANAGER','ADMIN')")
    public ResponseEntity<Void> supprimerImage(
            @PathVariable Long id,
            @PathVariable Long imageId) {

        catalogService.supprimerImage(id, imageId);
        return ResponseEntity.noContent().build();
    }

    // ── Vidéos produit ─────────────────────────────────────────

    @GetMapping("/produits/{id}/videos")
    public ResponseEntity<List<ProduitVideoResponse>> getVideos(@PathVariable Long id) {
        return ResponseEntity.ok(catalogService.getVideos(id));
    }

    @PostMapping("/produits/{id}/videos")
    @PreAuthorize("hasAnyRole('MANAGER','ADMIN')")
    public ResponseEntity<ProduitVideoResponse> ajouterVideo(
            @PathVariable Long id,
            @Valid @RequestBody CreateProduitVideoRequest request) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(catalogService.ajouterVideo(id, request));
    }

    @DeleteMapping("/produits/{id}/videos/{idVideo}")
    @PreAuthorize("hasAnyRole('MANAGER','ADMIN')")
    public ResponseEntity<Void> supprimerVideo(
            @PathVariable Long id,
            @PathVariable Long idVideo) {

        catalogService.supprimerVideo(id, idVideo);
        return ResponseEntity.noContent().build();
    }

    // ── Screenshots produit (communs à tous les variants) ──────

    @GetMapping("/produits/{id}/screenshots")
    public ResponseEntity<List<ScreenshotDto>> getScreenshots(@PathVariable Long id) {
        return ResponseEntity.ok(catalogService.getScreenshots(id));
    }

    @PostMapping("/produits/{id}/screenshots")
    @PreAuthorize("hasAnyRole('MANAGER','ADMIN')")
    public ResponseEntity<ScreenshotDto> ajouterScreenshot(
            @PathVariable Long id,
            @Valid @RequestBody CreateScreenshotRequest request) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(catalogService.ajouterScreenshot(id, request));
    }

    @PostMapping(value = "/produits/{id}/screenshots/upload",
                 consumes = org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('MANAGER','ADMIN')")
    public ResponseEntity<ScreenshotDto> uploadScreenshot(
            @PathVariable Long id,
            @RequestPart("file") org.springframework.web.multipart.MultipartFile file,
            @RequestParam(defaultValue = "") String alt,
            @RequestParam(defaultValue = "0") int ordre) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(catalogService.uploadScreenshot(id, file, alt, ordre));
    }

    @DeleteMapping("/produits/{id}/screenshots/{idScreenshot}")
    @PreAuthorize("hasAnyRole('MANAGER','ADMIN')")
    public ResponseEntity<Void> supprimerScreenshot(
            @PathVariable Long id,
            @PathVariable Long idScreenshot) {

        catalogService.supprimerScreenshot(id, idScreenshot);
        return ResponseEntity.noContent().build();
    }
}
