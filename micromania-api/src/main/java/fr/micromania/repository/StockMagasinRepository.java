package fr.micromania.repository;

import fr.micromania.entity.stock.StockMagasin;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface StockMagasinRepository extends JpaRepository<StockMagasin, Long> {

    Optional<StockMagasin> findByVariantIdAndMagasinId(Long idVariant, Long idMagasin);

    /** Verrou pessimiste pour les mouvements de stock (évite les courses) */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM StockMagasin s WHERE s.variant.id = :idVariant AND s.magasin.id = :idMagasin")
    Optional<StockMagasin> findByVariantIdAndMagasinIdForUpdate(
        @Param("idVariant") Long idVariant,
        @Param("idMagasin") Long idMagasin
    );

    List<StockMagasin> findByMagasinId(Long idMagasin);

    List<StockMagasin> findByVariantId(Long idVariant);

    /** Variantes en rupture dans un magasin */
    @Query("""
        SELECT s FROM StockMagasin s
        WHERE s.magasin.id = :idMagasin
          AND s.quantiteDisponible = 0
        """)
    List<StockMagasin> findRuptureByMagasin(@Param("idMagasin") Long idMagasin);

    /**
     * Catalogue POS : variants avec stock dans un magasin.
     * Filtrable par texte, plateforme et état (NEUF / OCCASION / LOCATION).
     * etat = null → tous les états confondus.
     */
    @Query("""
        SELECT s FROM StockMagasin s
        JOIN FETCH s.variant v
        JOIN FETCH v.produit p
        LEFT JOIN FETCH v.plateforme pl
        LEFT JOIN FETCH v.formatProduit fp
        LEFT JOIN FETCH v.statutProduit sp
        WHERE s.magasin.id = :idMagasin
          AND v.actif = true
          AND p.deleted = false
          AND (s.quantiteNeuf > 0 OR s.quantiteOccasion > 0)
          AND (:q IS NULL OR LOWER(v.nomCommercial) LIKE LOWER(CONCAT('%', :q, '%'))
                         OR LOWER(v.sku) LIKE LOWER(CONCAT('%', :q, '%'))
                         OR LOWER(p.nom) LIKE LOWER(CONCAT('%', :q, '%')))
          AND (:plateforme IS NULL OR pl.code = :plateforme)
          AND (:etat IS NULL OR UPPER(sp.code) = UPPER(:etat))
        """)
    org.springframework.data.domain.Page<StockMagasin> findCataloguePOS(
        @Param("idMagasin") Long idMagasin,
        @Param("q") String q,
        @Param("plateforme") String plateforme,
        @Param("etat") String etat,
        org.springframework.data.domain.Pageable pageable);
}
