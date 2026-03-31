package fr.micromania;

import fr.micromania.entity.catalog.Categorie;
import fr.micromania.entity.catalog.Produit;
import fr.micromania.entity.catalog.ProduitPrix;
import fr.micromania.entity.catalog.ProduitVariant;
import fr.micromania.entity.referentiel.CanalVente;
import fr.micromania.entity.referentiel.FormatProduit;
import fr.micromania.entity.referentiel.StatutProduit;
import fr.micromania.entity.referentiel.TypeCategorie;
import fr.micromania.repository.CategorieRepository;
import fr.micromania.repository.ProduitPrixRepository;
import fr.micromania.repository.ProduitRepository;
import fr.micromania.repository.ProduitVariantRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DisplayName("Catalog — tests intégration JPA")
class CatalogIntegrationTest {

    @Autowired ProduitRepository produitRepository;
    @Autowired ProduitVariantRepository variantRepository;
    @Autowired ProduitPrixRepository prixRepository;
    @Autowired CategorieRepository categorieRepository;
    @Autowired EntityManager entityManager;

    Categorie categorie;
    Produit produit;

    @BeforeEach
    void setUp() {
        TypeCategorie typeCategorie = entityManager.createQuery(
                        "select tc from TypeCategorie tc where tc.code = :code", TypeCategorie.class)
                .setParameter("code", "JEU")
                .getSingleResult();

        categorie = new Categorie();
        categorie.setNom("Jeux Video Test");
        categorie.setTypeCategorie(typeCategorie);
        categorie.setActif(true);
        categorie = categorieRepository.save(categorie);

        produit = new Produit();
        produit.setNom("The Legend of Zelda");
        produit.setSlug("zelda-tears-kingdom");
        produit.setCategorie(categorie);
        produit.setNiveauAccesMin("NORMAL");
        produit.setActif(true);
        produit.setDeleted(false);
        produit = produitRepository.save(produit);
    }

    @Test
    @DisplayName("findBySlug — retrouve le produit par son slug unique")
    void findBySlug_ok() {
        Optional<Produit> found = produitRepository.findBySlugAndDeletedFalse("zelda-tears-kingdom");

        assertThat(found).isPresent();
        assertThat(found.get().getNom()).isEqualTo("The Legend of Zelda");
    }

    @Test
    @DisplayName("findBySlug — vide si produit soft-deleted")
    void findBySlug_supprime() {
        produitRepository.softDelete(produit.getId());
        produitRepository.flush();

        Optional<Produit> found = produitRepository.findBySlugAndDeletedFalse("zelda-tears-kingdom");

        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("search — trouve par nom partiel")
    void search_parNom() {
        var results = produitRepository.search("zelda", null, null, PageRequest.of(0, 10));

        assertThat(results.getContent())
                .extracting(Produit::getSlug)
                .contains("zelda-tears-kingdom");
    }

    @Test
    @DisplayName("search — filtre par catégorie")
    void search_parCategorie() {
        var results = produitRepository.search(null, categorie.getId(), null, PageRequest.of(0, 10));

        assertThat(results.getContent())
                .extracting(Produit::getCategorie)
                .extracting(Categorie::getId)
                .contains(categorie.getId());

        assertThat(results.getContent())
                .extracting(Produit::getSlug)
                .contains("zelda-tears-kingdom");
    }

    @Test
    @DisplayName("search — retourne vide si catégorie inconnue")
    void search_categorieInconnue() {
        var results = produitRepository.search(null, 9999L, null, PageRequest.of(0, 10));

        assertThat(results.getContent()).isEmpty();
    }

    @Test
    @DisplayName("findPrixActif — retourne le prix en cours")
    void findPrixActif_ok() {
        FormatProduit formatProduit = entityManager.createQuery(
                        "select fp from FormatProduit fp where fp.code = :code", FormatProduit.class)
                .setParameter("code", "PHYSIQUE")
                .getSingleResult();

        StatutProduit statutProduit = entityManager.createQuery(
                        "select sp from StatutProduit sp where sp.code = :code", StatutProduit.class)
                .setParameter("code", "NEUF")
                .getSingleResult();

        CanalVente canalVente = entityManager.createQuery(
                        "select cv from CanalVente cv where cv.code = :code", CanalVente.class)
                .setParameter("code", "WEB")
                .getSingleResult();

        ProduitVariant variant = new ProduitVariant();
        variant.setSku("ZELDA-NEUF");
        variant.setNomCommercial("Zelda Neuf");
        variant.setProduit(produit);
        variant.setFormatProduit(formatProduit);
        variant.setStatutProduit(statutProduit);
        variant.setActif(true);
        variant = variantRepository.save(variant);

        ProduitPrix prix = new ProduitPrix();
        prix.setVariant(variant);
        prix.setCanalVente(canalVente);
        prix.setPrix(new BigDecimal("59.99"));
        prix.setDateDebut(LocalDateTime.now().minusHours(1));
        prix.setActif(true);
        prixRepository.save(prix);

        Optional<ProduitPrix> found = prixRepository.findPrixActif(
                variant.getId(), "WEB", LocalDateTime.now());

        assertThat(found).isPresent();
        assertThat(found.get().getPrix()).isEqualByComparingTo("59.99");
    }

    @Test
    @DisplayName("findPrixActif — vide si prix expiré")
    void findPrixActif_expire() {
        FormatProduit formatProduit = entityManager.createQuery(
                        "select fp from FormatProduit fp where fp.code = :code", FormatProduit.class)
                .setParameter("code", "PHYSIQUE")
                .getSingleResult();

        StatutProduit statutProduit = entityManager.createQuery(
                        "select sp from StatutProduit sp where sp.code = :code", StatutProduit.class)
                .setParameter("code", "NEUF")
                .getSingleResult();

        CanalVente canalVente = entityManager.createQuery(
                        "select cv from CanalVente cv where cv.code = :code", CanalVente.class)
                .setParameter("code", "WEB")
                .getSingleResult();

        ProduitVariant variant = new ProduitVariant();
        variant.setSku("ZELDA-OCC");
        variant.setNomCommercial("Zelda Occ");
        variant.setProduit(produit);
        variant.setFormatProduit(formatProduit);
        variant.setStatutProduit(statutProduit);
        variant.setActif(true);
        variant = variantRepository.save(variant);

        ProduitPrix prixExpire = new ProduitPrix();
        prixExpire.setVariant(variant);
        prixExpire.setCanalVente(canalVente);
        prixExpire.setPrix(new BigDecimal("49.99"));
        prixExpire.setDateDebut(LocalDateTime.now().minusDays(10));
        prixExpire.setDateFin(LocalDateTime.now().minusDays(1));
        prixExpire.setActif(true);
        prixRepository.save(prixExpire);

        Optional<ProduitPrix> found = prixRepository.findPrixActif(
                variant.getId(), "WEB", LocalDateTime.now());

        assertThat(found).isEmpty();
    }
}