package fr.micromania.repository;

import fr.micromania.entity.referentiel.StatutFacture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StatutFactureRepository extends JpaRepository<StatutFacture, Long> {
    Optional<StatutFacture> findByCode(String code);
}
