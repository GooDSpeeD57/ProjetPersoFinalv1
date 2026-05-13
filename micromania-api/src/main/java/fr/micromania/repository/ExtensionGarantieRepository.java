package fr.micromania.repository;

import fr.micromania.entity.ExtensionGarantie;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExtensionGarantieRepository extends JpaRepository<ExtensionGarantie, Long> {

    /** Dernière extension enregistrée pour une garantie donnée (une seule par garantie côté métier). */
    java.util.Optional<ExtensionGarantie> findTopByGarantieIdOrderByDateAchatDesc(Long garantieId);
}
