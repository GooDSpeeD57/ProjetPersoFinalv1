package fr.micromania.repository;

import fr.micromania.entity.commande.Panier;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PanierRepository extends JpaRepository<Panier, Long> {

    /** Panier actif du client sur un canal donné */
    @Query("""
        SELECT p FROM Panier p
        LEFT JOIN FETCH p.lignes l
        LEFT JOIN FETCH l.variant
        WHERE p.client.id = :idClient
          AND p.canalVente.code = :canal
          AND p.statutPanier.code = 'ACTIF'
        """)
    Optional<Panier> findPanierActif(
        @Param("idClient") Long idClient,
        @Param("canal") String canal
    );

    /** Paniers abandonnés depuis plus de N heures (pour relance) */
    @Query("""
        SELECT p FROM Panier p
        WHERE p.statutPanier.code = 'ACTIF'
          AND p.dateDerniereActivite < :limite
        """)
    List<Panier> findPaniersAbandonnes(@Param("limite") LocalDateTime limite);
}
