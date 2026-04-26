package fr.micromania.repository;

import fr.micromania.entity.referentiel.TauxTva;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TauxTvaRepository extends JpaRepository<TauxTva, Long> {
    List<TauxTva> findAllByActifTrueOrderByTauxAsc();
}
