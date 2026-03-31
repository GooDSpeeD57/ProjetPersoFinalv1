package fr.micromania.repository;

import fr.micromania.entity.PointsFidelite;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface PointsFideliteRepository extends JpaRepository<PointsFidelite, Long> {

    Optional<PointsFidelite> findByClientId(Long idClient);

    @Modifying
    @Query("UPDATE PointsFidelite p SET p.soldePoints = p.soldePoints + :delta WHERE p.client.id = :idClient")
    void addPoints(@Param("idClient") Long idClient, @Param("delta") int delta);

    @Modifying
    @Query("""
        UPDATE PointsFidelite p
        SET p.totalAchatsAnnuel = p.totalAchatsAnnuel + :montant
        WHERE p.client.id = :idClient
        """)
    void addAchatAnnuel(@Param("idClient") Long idClient, @Param("montant") java.math.BigDecimal montant);
}
