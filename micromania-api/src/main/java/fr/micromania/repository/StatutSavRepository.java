package fr.micromania.repository;

import fr.micromania.entity.referentiel.StatutSav;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StatutSavRepository extends JpaRepository<StatutSav, Long> {
    Optional<StatutSav> findByCode(String code);
}