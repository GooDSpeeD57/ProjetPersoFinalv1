package fr.micromania.repository;

import fr.micromania.entity.VenteUnite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface VenteUniteRepository extends JpaRepository<VenteUnite, Long> {

    Optional<VenteUnite> findByNumeroSerie(String numeroSerie);
}
