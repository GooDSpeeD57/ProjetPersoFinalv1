package fr.micromania.repository;

import fr.micromania.entity.Garantie;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface GarantieRepository extends JpaRepository<Garantie, Long> {

    Optional<Garantie> findByVenteUniteId(Long idVenteUnite);

    /** Garanties expirant dans les 30 prochains jours (alertes SAV) */
    @Query("""
        SELECT g FROM Garantie g
        WHERE g.dateFin BETWEEN :today AND :limite
        """)
    List<Garantie> findExpirantBientot(
        @Param("today") LocalDate today,
        @Param("limite") LocalDate limite
    );
}
