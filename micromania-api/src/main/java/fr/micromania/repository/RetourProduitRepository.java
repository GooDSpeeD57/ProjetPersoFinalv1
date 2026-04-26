package fr.micromania.repository;

import fr.micromania.entity.RetourProduit;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RetourProduitRepository extends JpaRepository<RetourProduit, Long> {
    List<RetourProduit> findByFactureId(Long idFacture);
}
