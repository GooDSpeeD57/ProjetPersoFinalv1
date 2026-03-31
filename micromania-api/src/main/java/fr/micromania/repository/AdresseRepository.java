package fr.micromania.repository;

import fr.micromania.entity.Adresse;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface AdresseRepository extends JpaRepository<Adresse, Long> {

    List<Adresse> findByClientId(Long idClient);

    Optional<Adresse> findByClientIdAndEstDefautTrue(Long idClient);

    List<Adresse> findByMagasinId(Long idMagasin);

    /** Réinitialise toutes les adresses par défaut d'un client avant d'en définir une nouvelle */
    @Modifying
    @Query("UPDATE Adresse a SET a.estDefaut = false WHERE a.client.id = :idClient")
    void resetDefautByClient(@Param("idClient") Long idClient);
}
