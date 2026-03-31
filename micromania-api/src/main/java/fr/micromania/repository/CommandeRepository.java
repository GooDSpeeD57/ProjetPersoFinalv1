package fr.micromania.repository;

import fr.micromania.entity.commande.Commande;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CommandeRepository extends JpaRepository<Commande, Long> {

    Optional<Commande> findByReferenceCommande(String reference);

    Page<Commande> findByClientIdOrderByDateCommandeDesc(Long idClient, Pageable pageable);

    @Query("""
        SELECT c FROM Commande c
        WHERE (:idClient IS NULL OR c.client.id = :idClient)
          AND (:statut   IS NULL OR c.statutCommande.code = :statut)
          AND (:from     IS NULL OR c.dateCommande >= :from)
          AND (:to       IS NULL OR c.dateCommande <= :to)
        ORDER BY c.dateCommande DESC
        """)
    Page<Commande> filter(
        @Param("idClient") Long idClient,
        @Param("statut") String statut,
        @Param("from") LocalDateTime from,
        @Param("to") LocalDateTime to,
        Pageable pageable
    );

    /** Commandes à expédier depuis un entrepôt */
    @Query("""
        SELECT c FROM Commande c
        WHERE c.entrepotExpedition.id = :idEntrepot
          AND c.statutCommande.code IN ('PAYEE', 'PREPARATION')
        ORDER BY c.dateCommande ASC
        """)
    List<Commande> findAExpedierDepuisEntrepot(@Param("idEntrepot") Long idEntrepot);

    /** Commandes à préparer pour retrait magasin */
    @Query("""
        SELECT c FROM Commande c
        WHERE c.magasinRetrait.id = :idMagasin
          AND c.statutCommande.code IN ('PAYEE', 'PREPARATION')
        ORDER BY c.dateCommande ASC
        """)
    List<Commande> findARetirerMagasin(@Param("idMagasin") Long idMagasin);
}
