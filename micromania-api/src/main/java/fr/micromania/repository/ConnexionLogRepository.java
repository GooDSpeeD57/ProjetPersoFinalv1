package fr.micromania.repository;

import fr.micromania.entity.securite.ConnexionLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface ConnexionLogRepository extends JpaRepository<ConnexionLog, Long> {

    @Modifying
    @Query("DELETE FROM ConnexionLog c WHERE c.dateConnexion < :avant")
    void deleteOlderThan(@Param("avant") LocalDateTime avant);
}
