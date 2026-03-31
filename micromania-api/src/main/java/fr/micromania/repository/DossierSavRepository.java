package fr.micromania.repository;

import fr.micromania.entity.DossierSav;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface DossierSavRepository extends JpaRepository<DossierSav, Long> {

    Optional<DossierSav> findByReferenceSav(String reference);

    @Query("""
        SELECT d FROM DossierSav d
        WHERE (:statut IS NULL OR d.statutSav.code = :statut)
          AND (:idEmploye IS NULL OR d.employe.id = :idEmploye)
        ORDER BY d.dateOuverture DESC
        """)
    Page<DossierSav> filter(
        @Param("statut") String statut,
        @Param("idEmploye") Long idEmploye,
        Pageable pageable
    );

    Page<DossierSav> findByVenteUniteIdOrderByDateOuvertureDesc(Long idVenteUnite, Pageable pageable);
}
