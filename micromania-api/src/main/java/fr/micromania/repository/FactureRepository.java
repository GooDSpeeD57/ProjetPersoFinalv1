package fr.micromania.repository;

import fr.micromania.entity.commande.Facture;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface FactureRepository extends JpaRepository<Facture, Long> {

    Optional<Facture> findByReferenceFacture(String reference);

    Page<Facture> findByClientIdOrderByDateFactureDesc(Long idClient, Pageable pageable);

    Page<Facture> findByMagasinIdOrderByDateFactureDesc(Long idMagasin, Pageable pageable);

    @Query("""
        SELECT f FROM Facture f
        WHERE (:idMagasin IS NULL OR f.magasin.id = :idMagasin)
          AND (:contexte  IS NULL OR f.contexteVente.code = :contexte)
          AND (:from      IS NULL OR f.dateFacture >= :from)
          AND (:to        IS NULL OR f.dateFacture <= :to)
        ORDER BY f.dateFacture DESC
        """)
    Page<Facture> filter(
        @Param("idMagasin") Long idMagasin,
        @Param("contexte") String contexte,
        @Param("from") LocalDateTime from,
        @Param("to") LocalDateTime to,
        Pageable pageable
    );

    boolean existsByCommandeId(Long idCommande);
}
