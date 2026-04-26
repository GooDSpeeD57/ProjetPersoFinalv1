package fr.micromania.repository;

import fr.micromania.entity.referentiel.StatutPlanning;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StatutPlanningRepository extends JpaRepository<StatutPlanning, Long> {
    Optional<StatutPlanning> findByCode(String code);
}
