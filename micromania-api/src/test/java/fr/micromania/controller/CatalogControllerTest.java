package fr.micromania.controller;

import fr.micromania.dto.catalog.CategorieResponse;
import fr.micromania.dto.catalog.ProduitResponse;
import fr.micromania.dto.catalog.ProduitSummary;
import fr.micromania.security.JwtAuthFilter;
import fr.micromania.security.SecurityConfig;
import fr.micromania.service.CatalogService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CatalogController.class)
@Import(SecurityConfig.class)
@DisplayName("CatalogController — tests slice WebMvc")
class CatalogControllerTest {

    @Autowired MockMvc mockMvc;

    @MockitoBean CatalogService catalogService;
    @MockitoBean JwtAuthFilter jwtAuthFilter;

    ProduitSummary produitSummary;

    @BeforeEach
    void setUp() throws Exception {
        produitSummary = new ProduitSummary(
                1L, "Spider-Man PS5", "spider-man-ps5",
                "Jeux Video", "/img/spider.jpg", "Spider-Man",
                new BigDecimal("69.99"), null, true, false, 16,
                null, 0L
        );

        doAnswer(invocation -> {
            ServletRequest request = invocation.getArgument(0);
            ServletResponse response = invocation.getArgument(1);
            FilterChain chain = invocation.getArgument(2);
            chain.doFilter(request, response);
            return null;
        }).when(jwtAuthFilter).doFilter(any(), any(), any());
    }

    @Test
    @DisplayName("GET /api/v1/produits — 200 sans authentification")
    void getProduits_sansAuth() throws Exception {
        Page<ProduitSummary> page = new PageImpl<>(List.of(produitSummary));
        when(catalogService.search(any(), any(), any(), any())).thenReturn(page);

        mockMvc.perform(get("/api/v1/produits"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].nom").value("Spider-Man PS5"))
                .andExpect(jsonPath("$.content[0].prixNeuf").value(69.99))
                .andExpect(jsonPath("$.content[0].prixOccasion").doesNotExist());
    }

    @Test
    @DisplayName("GET /api/v1/produits — pagination via query params")
    void getProduits_avecPagination() throws Exception {
        Page<ProduitSummary> page = new PageImpl<>(List.of());
        when(catalogService.search(any(), any(), any(), any())).thenReturn(page);

        mockMvc.perform(get("/api/v1/produits")
                        .param("page", "1")
                        .param("size", "5")
                        .param("q", "zelda"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /api/v1/produits/mis-en-avant — 200 public")
    void getMisEnAvant_ok() throws Exception {
        when(catalogService.getMisEnAvant()).thenReturn(List.of(produitSummary));

        mockMvc.perform(get("/api/v1/produits/mis-en-avant"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].slug").value("spider-man-ps5"));
    }

    @Test
    @DisplayName("GET /api/v1/produits/slug/{slug} — 200 avec détail produit")
    void getProduitBySlug_ok() throws Exception {
        ProduitResponse response = new ProduitResponse(
                1L, "Spider-Man PS5", "spider-man-ps5",
                "Desc", "Résumé", null, "Sony", null, 16, "Sony",
                "NORMAL", "fr", false, null, List.of(), List.of(),
                null, 0L, List.of()
        );
        when(catalogService.getProduitBySlug("spider-man-ps5")).thenReturn(response);

        mockMvc.perform(get("/api/v1/produits/slug/spider-man-ps5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nom").value("Spider-Man PS5"))
                .andExpect(jsonPath("$.pegi").value(16));
    }

    @Test
    @DisplayName("GET /api/v1/categories — retourne toutes les catégories actives")
    void getCategories_ok() throws Exception {
        when(catalogService.getCategories()).thenReturn(List.of(
                new CategorieResponse(1L, "Jeux Video", null, "JEU", true)
        ));

        mockMvc.perform(get("/api/v1/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nom").value("Jeux Video"))
                .andExpect(jsonPath("$[0].typeCategorie").value("JEU"));
    }

    @Test
    @DisplayName("POST /api/v1/produits — 401 sans authentification")
    void creerProduit_sansAuth_refuse() throws Exception {
        mockMvc.perform(post("/api/v1/produits")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isUnauthorized());
    }
}
