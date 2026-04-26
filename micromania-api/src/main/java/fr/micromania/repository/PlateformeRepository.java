package fr.micromania.repository;

import fr.micromania.entity.referentiel.Plateforme;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PlateformeRepository extends JpaRepository<Plateforme, Long> {
    List<Plateforme> findAllByOrderByLibelleAsc();
}
