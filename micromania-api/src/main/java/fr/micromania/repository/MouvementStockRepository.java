package fr.micromania.repository;

import fr.micromania.entity.stock.MouvementStock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;

@Repository
public interface MouvementStockRepository extends JpaRepository<MouvementStock, Long> {

    @Query("""
        SELECT m FROM MouvementStock m
        WHERE (:idVariant IS NULL OR m.variant.id = :idVariant)
          AND (:idMagasin IS NULL OR m.magasin.id = :idMagasin)
          AND (:from IS NULL OR m.dateMouvement >= :from)
          AND (:to   IS NULL OR m.dateMouvement <= :to)
        ORDER BY m.dateMouvement DESC
        """)
    Page<MouvementStock> filter(
        @Param("idVariant") Long idVariant,
        @Param("idMagasin") Long idMagasin,
        @Param("from") LocalDateTime from,
        @Param("to") LocalDateTime to,
        Pageable pageable
    );
}
