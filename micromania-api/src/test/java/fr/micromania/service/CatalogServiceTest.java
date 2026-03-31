package fr.micromania.service;

import fr.micromania.dto.catalog.*;
import fr.micromania.entity.catalog.*;
import fr.micromania.mapper.CatalogMapper;
import fr.micromania.repository.*;
import fr.micromania.service.impl.CatalogServiceImpl;
import fr.micromania.util.TestFixtures;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CatalogService — tests unitaires")
class CatalogServiceTest {

    @Mock ProduitRepository        produitRepository;
    @Mock ProduitVariantRepository variantRepository;
    @Mock ProduitPrixRepository    prixRepository;
    @Mock CategorieRepository      categorieRepository;
    @Mock CatalogMapper            catalogMapper;

    @InjectMocks CatalogServiceImpl catalogService;

    Produit        produit;
    ProduitVariant variant;

    @BeforeEach
    void setUp() {
        produit = TestFixtures.produit();
        variant = TestFixtures.variant();
        produit.getVariants().add(variant);
        produit.getImages().add(buildImage());
    }

    // ── getProduitBySlug ──────────────────────────────────────
    @Test
    @DisplayName("getProduitBySlug — retourne le produit existant")
    void getProduitBySlug_ok() {
        ProduitResponse response = buildProduitResponse();
        when(produitRepository.findBySlugAndDeletedFalse("spider-man-ps5"))
            .thenReturn(Optional.of(produit));
        when(catalogMapper.toProduitResponse(produit)).thenReturn(response);

        ProduitResponse result = catalogService.getProduitBySlug("spider-man-ps5");

        assertThat(result.nom()).isEqualTo("Spider-Man PS5");
        verify(produitRepository).findBySlugAndDeletedFalse("spider-man-ps5");
    }

    @Test
    @DisplayName("getProduitBySlug — lève EntityNotFoundException si slug inconnu")
    void getProduitBySlug_inexistant() {
        when(produitRepository.findBySlugAndDeletedFalse("inexistant"))
            .thenReturn(Optional.empty());

        assertThatThrownBy(() -> catalogService.getProduitBySlug("inexistant"))
            .isInstanceOf(EntityNotFoundException.class);
    }

    // ── creerProduit ──────────────────────────────────────────
    @Test
    @DisplayName("creerProduit — persiste et retourne le produit")
    void creerProduit_ok() {
        CreateProduitRequest request = new CreateProduitRequest(
            1L, "Nouveau Jeu", "nouveau-jeu", "Description", "Résumé",
            null, "Ubisoft", null, 12, "Ubisoft", "NORMAL", false
        );

        when(produitRepository.existsBySlugAndDeletedFalse("nouveau-jeu")).thenReturn(false);
        when(categorieRepository.findById(1L)).thenReturn(Optional.of(TestFixtures.categorieJeux()));
        when(produitRepository.save(any())).thenReturn(produit);
        when(catalogMapper.toProduitResponse(produit)).thenReturn(buildProduitResponse());

        ProduitResponse result = catalogService.creerProduit(request);

        assertThat(result).isNotNull();
        verify(produitRepository).save(any(Produit.class));
    }

    @Test
    @DisplayName("creerProduit — refuse si slug déjà utilisé")
    void creerProduit_slugDuplique() {
        CreateProduitRequest request = new CreateProduitRequest(
            1L, "Test", "spider-man-ps5", null, null,
            null, null, null, null, null, "NORMAL", false
        );
        when(produitRepository.existsBySlugAndDeletedFalse("spider-man-ps5")).thenReturn(true);

        assertThatThrownBy(() -> catalogService.creerProduit(request))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("spider-man-ps5");
    }

    // ── creerVariant ──────────────────────────────────────────
    @Test
    @DisplayName("creerVariant — crée le variant et le prix web associé")
    void creerVariant_avecPrixWeb() {
        CreateVariantRequest request = new CreateVariantRequest(
            1L, "NOUVEAU-SKU", null, null, 1L, 1L,
            "Nouveau Variant", null, null, "fr",
            false, false, false, false, null,
            new BigDecimal("49.99"), null
        );

        when(produitRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(produit));
        when(variantRepository.findBySkuAndActifTrue("NOUVEAU-SKU")).thenReturn(Optional.empty());
        when(variantRepository.save(any())).thenReturn(variant);
        when(catalogMapper.toVariantResponse(variant)).thenReturn(buildVariantResponse());

        ProduitVariantResponse result = catalogService.creerVariant(request);

        assertThat(result).isNotNull();
        verify(variantRepository).save(any(ProduitVariant.class));
        verify(prixRepository).desactiverPrixActifs(anyLong(), anyLong());
    }

    @Test
    @DisplayName("creerVariant — refuse si SKU déjà existant")
    void creerVariant_skuDuplique() {
        CreateVariantRequest request = new CreateVariantRequest(
            1L, "SPIDER-MAN-PS5-NEUF", null, null, 1L, 1L,
            "Dupliqué", null, null, "fr",
            false, false, false, false, null, null, null
        );
        when(produitRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(produit));
        when(variantRepository.findBySkuAndActifTrue("SPIDER-MAN-PS5-NEUF"))
            .thenReturn(Optional.of(variant));

        assertThatThrownBy(() -> catalogService.creerVariant(request))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("SPIDER-MAN-PS5-NEUF");
    }

    // ── supprimerProduit ──────────────────────────────────────
    @Test
    @DisplayName("supprimerProduit — appelle le soft-delete")
    void supprimerProduit_ok() {
        when(produitRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(produit));

        catalogService.supprimerProduit(1L);

        verify(produitRepository).softDelete(1L);
    }

    // ── getCategories ─────────────────────────────────────────
    @Test
    @DisplayName("getCategories — retourne toutes les catégories actives")
    void getCategories_ok() {
        when(categorieRepository.findByActifTrue())
            .thenReturn(List.of(TestFixtures.categorieJeux()));
        when(catalogMapper.toCategorieResponse(any()))
            .thenReturn(new CategorieResponse(1L, "Jeux Video", null, "JEU", true));

        List<CategorieResponse> result = catalogService.getCategories();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).nom()).isEqualTo("Jeux Video");
    }

    // ── Helpers ────────────────────────────────────────────────
    private ProduitImage buildImage() {
        ProduitImage img = new ProduitImage();
        img.setId(1L); img.setPrincipale(true);
        img.setUrl("/images/spider-man.jpg"); img.setAlt("Spider-Man");
        img.setProduit(produit);
        return img;
    }

    private ProduitResponse buildProduitResponse() {
        return new ProduitResponse(1L, "Spider-Man PS5", "spider-man-ps5",
            "Description", "Résumé", null, "Sony", null, 16, "Sony",
            "NORMAL", "fr", false, null, List.of(), List.of());
    }

    private ProduitVariantResponse buildVariantResponse() {
        return new ProduitVariantResponse(1L, "SPIDER-MAN-PS5-NEUF", null,
            "Spider-Man PS5 Neuf", null, "PHYSIQUE", "NEUF",
            null, null, "fr", false, false, false, false, false, null,
            new BigDecimal("69.99"), null, true);
    }
}
