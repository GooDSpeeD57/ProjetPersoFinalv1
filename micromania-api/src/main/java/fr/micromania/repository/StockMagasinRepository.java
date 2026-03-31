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
}
