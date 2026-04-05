package fr.micromania.repository;

import fr.micromania.entity.referentiel.StatutPrecommande;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StatutPrecommandeRepository extends JpaRepository<StatutPrecommande, Long> {
    Optional<StatutPrecommande> findByCode(String code);
}