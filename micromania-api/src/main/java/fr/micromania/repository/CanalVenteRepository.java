package fr.micromania.repository;

import fr.micromania.entity.referentiel.CanalVente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CanalVenteRepository extends JpaRepository<CanalVente, Long> {
    Optional<CanalVente> findByCode(String code);
}