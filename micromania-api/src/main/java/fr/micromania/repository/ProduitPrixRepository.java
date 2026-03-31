package fr.micromania.repository;

import fr.micromania.entity.catalog.ProduitPrix;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface ProduitPrixRepository extends JpaRepository<ProduitPrix, Long> {

    /** Prix actif en ce moment pour un variant et un canal de vente donnés */
    @Query("""
        SELECT pp FROM ProduitPrix pp
        WHERE pp.variant.id     = :idVariant
          AND pp.canalVente.code = :canalVente
          AND pp.actif          = true
          AND pp.dateDebut      <= :now
          AND (pp.dateFin IS NULL OR pp.dateFin > :now)
        """)
    Optional<ProduitPrix> findPrixActif(
        @Param("idVariant") Long idVariant,
        @Param("canalVente") String canalVente,
        @Param("now") LocalDateTime now
    );

    /** Désactive les prix en cours avant insertion d'un nouveau pour le même canal */
    @Modifying
    @Query("""
        UPDATE ProduitPrix pp SET pp.actif = false
        WHERE pp.variant.id = :idVariant
          AND pp.canalVente.id = :idCanalVente
          AND pp.actif = true
        """)
    void desactiverPrixActifs(
        @Param("idVariant") Long idVariant,
        @Param("idCanalVente") Long idCanalVente
    );
}
