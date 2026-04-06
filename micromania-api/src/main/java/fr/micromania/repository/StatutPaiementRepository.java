package fr.micromania.repository;

import fr.micromania.entity.referentiel.StatutPaiement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StatutPaiementRepository extends JpaRepository<StatutPaiement, Long> {
    Optional<StatutPaiement> findByCode(String code);
}
