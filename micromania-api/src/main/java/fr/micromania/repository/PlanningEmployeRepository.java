package fr.micromania.repository;

import fr.micromania.entity.PlanningEmploye;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PlanningEmployeRepository extends JpaRepository<PlanningEmploye, Long> {

    /** Toutes les entrées d'un magasin sur une plage de dates (pour la vue semaine). */
    @Query("""
        SELECT p FROM PlanningEmploye p
        JOIN FETCH p.employe e
        JOIN FETCH e.role r
        JOIN FETCH p.statutPlanning s
        WHERE e.magasin.id = :magasinId
          AND p.dateTravail BETWEEN :debut AND :fin
          AND e.deleted = false
        ORDER BY e.nom, e.prenom, p.dateTravail
        """)
    List<PlanningEmploye> findSemaine(
        @Param("magasinId") Long magasinId,
        @Param("debut")     LocalDate debut,
        @Param("fin")       LocalDate fin
    );

    /** Entrées d'un employé sur une plage de dates. */
    List<PlanningEmploye> findByEmployeIdAndDateTravailBetweenOrderByDateTravail(
        Long employeId, LocalDate debut, LocalDate fin);
}
