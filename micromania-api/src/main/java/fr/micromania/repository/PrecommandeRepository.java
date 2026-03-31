package fr.micromania.repository;

import fr.micromania.entity.commande.Precommande;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface PrecommandeRepository extends JpaRepository<Precommande, Long> {

    Optional<Precommande> findByReferencePrecommande(String reference);

    Page<Precommande> findByClientIdOrderByDatePrecommandeDesc(Long idClient, Pageable pageable);

    @Query("""
        SELECT p FROM Precommande p
        WHERE p.statutPrecommande.code = :statut
        ORDER BY p.datePrecommande ASC
        """)
    Page<Precommande> findByStatut(@Param("statut") String statut, Pageable pageable);
}
