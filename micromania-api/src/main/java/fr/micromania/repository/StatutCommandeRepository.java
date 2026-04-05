package fr.micromania.repository;

import fr.micromania.entity.referentiel.StatutCommande;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StatutCommandeRepository extends JpaRepository<StatutCommande, Long> {
    Optional<StatutCommande> findByCode(String code);
}