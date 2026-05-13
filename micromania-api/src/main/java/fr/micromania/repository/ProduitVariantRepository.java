package fr.micromania.repository;

import fr.micromania.entity.catalog.ProduitVariant;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProduitVariantRepository extends JpaRepository<ProduitVariant, Long>,
        JpaSpecificationExecutor<ProduitVariant> {

    Optional<ProduitVariant> findBySkuAndActifTrue(String sku);

    Optional<ProduitVariant> findByEanAndActifTrue(String ean);

    List<ProduitVariant> findByProduitIdAndActifTrue(Long idProduit);

    /** Variantes disponibles pour une plateforme donnée */
    @Query("""
        SELECT v FROM ProduitVariant v
        WHERE v.actif = true
          AND v.plateforme.code = :codePlateforme
          AND v.produit.deleted = false
        """)
    List<ProduitVariant> findByPlatformeCode(@Param("codePlateforme") String code);

    /** Variantes nécessitant un numéro de série (consoles / accessoires) */
    List<ProduitVariant> findByNecessiteNumeroSerieTrue();

    /** Variantes dématérialisées */
    List<ProduitVariant> findByEstDematTrueAndActifTrue();
}
