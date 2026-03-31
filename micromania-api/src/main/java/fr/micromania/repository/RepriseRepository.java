package fr.micromania.repository;

import fr.micromania.entity.Reprise;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface RepriseRepository extends JpaRepository<Reprise, Long> {

    Optional<Reprise> findByReferenceReprise(String reference);

    @Query("""
        SELECT r FROM Reprise r
        WHERE (:idMagasin IS NULL OR r.magasin.id = :idMagasin)
          AND (:statut IS NULL OR r.statutReprise.code = :statut)
          AND (:idClient IS NULL OR r.client.id = :idClient)
        ORDER BY r.dateCreation DESC
        """)
    Page<Reprise> filter(
        @Param("idMagasin") Long idMagasin,
        @Param("statut") String statut,
        @Param("idClient") Long idClient,
        Pageable pageable
    );
}
