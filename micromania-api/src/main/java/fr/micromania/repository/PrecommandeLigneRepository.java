package fr.micromania.repository;

import fr.micromania.entity.commande.PrecommandeLigne;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PrecommandeLigneRepository extends JpaRepository<PrecommandeLigne, Long> {
    List<PrecommandeLigne> findByPrecommandeId(Long idPrecommande);
}
