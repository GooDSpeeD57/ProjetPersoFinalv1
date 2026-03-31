package fr.micromania.repository;

import fr.micromania.entity.commande.BonAchat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface BonAchatRepository extends JpaRepository<BonAchat, Long> {

    Optional<BonAchat> findByCodeBonAndUtiliseFalse(String codeBon);

    List<BonAchat> findByClientIdOrderByDateCreationDesc(Long idClient);
}
