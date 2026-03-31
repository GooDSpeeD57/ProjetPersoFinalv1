package fr.micromania.repository;

import fr.micromania.entity.referentiel.StatutPanier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StatutPanierRepository extends JpaRepository<StatutPanier, Long> {
    Optional<StatutPanier> findByCode(String code);
}