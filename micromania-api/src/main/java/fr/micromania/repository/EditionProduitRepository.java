package fr.micromania.repository;

import fr.micromania.entity.referentiel.EditionProduit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EditionProduitRepository extends JpaRepository<EditionProduit, Long> {
    List<EditionProduit> findByActifTrueOrderByOrdreAffichageAsc();
}
