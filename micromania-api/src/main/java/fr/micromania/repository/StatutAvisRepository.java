package fr.micromania.repository;

import fr.micromania.entity.referentiel.StatutAvis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StatutAvisRepository extends JpaRepository<StatutAvis, Long> {
    Optional<StatutAvis> findByCode(String code);
}
