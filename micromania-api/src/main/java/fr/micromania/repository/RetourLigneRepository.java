package fr.micromania.repository;

import fr.micromania.entity.RetourLigne;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RetourLigneRepository extends JpaRepository<RetourLigne, Long> {
    List<RetourLigne> findByRetourProduitId(Long idRetour);
}
