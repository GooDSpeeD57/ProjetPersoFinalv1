package fr.micromania.repository;

import fr.micromania.entity.commande.Promotion;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PromotionRepository extends JpaRepository<Promotion, Long> {

    Optional<Promotion> findByCodePromoAndActifTrue(String codePromo);

    /** Promotions en cours à cet instant */
    @Query("""
        SELECT p FROM Promotion p
        WHERE p.actif = true
          AND :now BETWEEN p.dateDebut AND p.dateFin
        """)
    List<Promotion> findActives(@Param("now") LocalDateTime now);

    /** Nombre d'utilisations par client pour une promo */
    @Query("""
        SELECT COUNT(pu) FROM PromotionUsage pu
        WHERE pu.promotion.id = :idPromotion
          AND pu.client.id = :idClient
        """)
    long countUtilisationsParClient(
        @Param("idPromotion") Long idPromotion,
        @Param("idClient") Long idClient
    );
}
