package fr.micromania.repository;

import fr.micromania.entity.AbonnementClient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AbonnementClientRepository extends JpaRepository<AbonnementClient, Long> {

    Optional<AbonnementClient> findTopByClientIdAndStatutAbonnementCodeAndDateFinGreaterThanEqualOrderByDateFinDesc(
        Long idClient,
        String codeStatut,
        LocalDate date
    );

    Optional<AbonnementClient> findTopByClientIdOrderByDateFinDesc(Long idClient);

    List<AbonnementClient> findByClientIdAndStatutAbonnementCodeAndDateFinBefore(Long idClient, String codeStatut, LocalDate date);
}
