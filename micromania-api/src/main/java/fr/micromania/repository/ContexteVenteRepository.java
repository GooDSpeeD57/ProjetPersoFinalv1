package fr.micromania.repository;

import fr.micromania.entity.referentiel.ContexteVente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ContexteVenteRepository extends JpaRepository<ContexteVente, Long> {
    Optional<ContexteVente> findByCode(String code);
}
