package fr.micromania.repository;

import fr.micromania.entity.catalog.ProduitPrix;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface ProduitPrixRepository extends JpaRepository<ProduitPrix, Long> {

    /** Prix actif pour un variant donné. */
    @Query("""
        SELECT pp FROM ProduitPrix pp
        WHERE pp.variant.id = :idVariant
          AND pp.actif      = true
        ORDER BY pp.dateDebut DESC
        """)
    Optional<ProduitPrix> findPrixActif(@Param("idVariant") Long idVariant);

    /** Désactive le prix actif avant insertion d'un nouveau pour ce variant. */
    @Modifying
    @Query("""
        UPDATE ProduitPrix pp SET pp.actif = false
        WHERE pp.variant.id = :idVariant
          AND pp.actif = true
        """)
    void desactiverPrixActifs(@Param("idVariant") Long idVariant);
}
