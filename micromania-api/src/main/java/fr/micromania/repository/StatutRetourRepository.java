package fr.micromania.repository;

import fr.micromania.entity.referentiel.StatutRetour;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface StatutRetourRepository extends JpaRepository<StatutRetour, Long> {
    Optional<StatutRetour> findByCode(String code);
}
