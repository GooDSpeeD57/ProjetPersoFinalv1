package fr.micromania.repository;

import fr.micromania.entity.commande.HistoriquePoints;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HistoriquePointsRepository extends JpaRepository<HistoriquePoints, Long> {

    List<HistoriquePoints> findByClientIdOrderByDateOperationDesc(Long idClient);

    @Query("""
        SELECT COALESCE(SUM(CASE WHEN h.points > 0 THEN h.points ELSE 0 END), 0)
        FROM HistoriquePoints h
        WHERE h.client.id = :idClient
        """)
    Long sumPositivePointsByClient(@Param("idClient") Long idClient);
}
