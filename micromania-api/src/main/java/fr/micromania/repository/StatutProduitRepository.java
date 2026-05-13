package fr.micromania.repository;

import fr.micromania.entity.referentiel.StatutProduit;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface StatutProduitRepository extends JpaRepository<StatutProduit, Long> {
    List<StatutProduit> findAllByOrderByCodeAsc();
}
