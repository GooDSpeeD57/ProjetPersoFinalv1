package fr.micromania.repository;

import fr.micromania.entity.Adresse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AdresseRepository extends JpaRepository<Adresse, Long> {

    List<Adresse> findByClientId(Long idClient);

    Optional<Adresse> findByClientIdAndEstDefautTrue(Long idClient);

    List<Adresse> findByMagasinId(Long idMagasin);

    Optional<Adresse> findFirstByMagasinIdAndEstDefautTrue(Long idMagasin);

    @Query("""
        SELECT a
        FROM Adresse a
        JOIN FETCH a.magasin m
        WHERE a.magasin IS NOT NULL
          AND m.actif = true
        """)
    List<Adresse> findAllMagasinAddressesActives();

    @Modifying
    @Query("UPDATE Adresse a SET a.estDefaut = false WHERE a.client.id = :idClient")
    void resetDefautByClient(@Param("idClient") Long idClient);
}
