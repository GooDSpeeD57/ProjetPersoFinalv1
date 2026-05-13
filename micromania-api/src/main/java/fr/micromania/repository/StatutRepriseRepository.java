package fr.micromania.repository;

import fr.micromania.entity.referentiel.StatutReprise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StatutRepriseRepository extends JpaRepository<StatutReprise, Long> {
    Optional<StatutReprise> findByCode(String code);
}
