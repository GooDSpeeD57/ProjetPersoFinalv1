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
            @PageableDefault(size = 24) Pageable pageable) {

        return ResponseEntity.ok(catalogService.search(q, categorie, niveau, pageable));
    }

    @GetMapping("/produits/mis-en-avant")
    public ResponseEntity<List<ProduitSummary>> getMisEnAvant() {
        return ResponseEntity.ok(catalogService.getMisEnAvant());
    }

    @GetMapping("/produits/{id}")
    public ResponseEntity<ProduitResponse> getProduitById(@PathVariable Long id) {
        return ResponseEntity.ok(catalogService.getProduitById(id));
    }

    @GetMapping("/produits/slug/{slug}")
    public ResponseEntity<ProduitResponse> getProduitBySlug(@PathVariable String slug) {
        return ResponseEntity.ok(catalogService.getProduitBySlug(slug));
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

    // ── Prix ───────────────────────────────────────────────────

    @PostMapping("/prix")
    @PreAuthorize("hasAnyRole('MANAGER','ADMIN')")
    public ResponseEntity<PrixResponse> setPrice(
            @Valid @RequestBody SetPrixRequest request) {

        return ResponseEntity.status(HttpStatus.CREATED)
            .body(catalogService.setPrice(request));
    }
}
