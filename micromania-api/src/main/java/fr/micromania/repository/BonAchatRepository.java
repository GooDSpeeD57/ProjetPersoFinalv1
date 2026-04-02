package fr.micromania.repository;

import fr.micromania.entity.commande.BonAchat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface BonAchatRepository extends JpaRepository<BonAchat, Long> {

    Optional<BonAchat> findByCodeBonAndUtiliseFalse(String codeBon);
    Optional<BonAchat> findByIdAndClientIdAndUtiliseFalse(Long idBonAchat, Long idClient);

    long countByClientIdAndPointsUtilisesAndValeur(Long idClient, int pointsUtilises, BigDecimal valeur);

    List<BonAchat> findByClientIdOrderByDateCreationDesc(Long idClient);
}
