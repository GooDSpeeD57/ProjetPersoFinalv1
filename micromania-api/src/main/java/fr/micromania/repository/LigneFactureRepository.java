package fr.micromania.repository;

import fr.micromania.entity.commande.LigneFacture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface LigneFactureRepository extends JpaRepository<LigneFacture, Long> {
    List<LigneFacture> findByFactureId(Long idFacture);

    /** Lignes de facture avec garantie souscrite pour un client donné. */
    @Query("""
        SELECT lf FROM LigneFacture lf
        JOIN lf.facture f
        WHERE f.client.id = :idClient
          AND lf.garantieLabel IS NOT NULL
        ORDER BY f.dateFacture DESC
        """)
    List<LigneFacture> findByClientIdWithGarantieLabel(@Param("idClient") Long idClient);
}
