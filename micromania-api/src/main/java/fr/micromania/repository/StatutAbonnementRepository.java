package fr.micromania.repository;

import fr.micromania.entity.referentiel.StatutAbonnement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StatutAbonnementRepository extends JpaRepository<StatutAbonnement, Long> {
    Optional<StatutAbonnement> findByCode(String code);
}
