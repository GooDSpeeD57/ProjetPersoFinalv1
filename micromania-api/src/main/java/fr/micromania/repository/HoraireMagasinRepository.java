package fr.micromania.repository;

import fr.micromania.entity.HoraireMagasin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface HoraireMagasinRepository extends JpaRepository<HoraireMagasin, Long> {

    /** Tous les horaires d'un magasin, triés Lun → Dim */
    List<HoraireMagasin> findByMagasinIdOrderByJourSemaine(Long idMagasin);

    /** Horaire pour un jour précis */
    Optional<HoraireMagasin> findByMagasinIdAndJourSemaine(Long idMagasin, int jourSemaine);

    /** Supprime tous les horaires d'un magasin (avant de les recréer en bloc) */
    @Modifying
    @Query("DELETE FROM HoraireMagasin h WHERE h.magasin.id = :idMagasin")
    void deleteByMagasinId(@Param("idMagasin") Long idMagasin);
}
