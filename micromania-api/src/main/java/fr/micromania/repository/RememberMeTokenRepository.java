package fr.micromania.repository;

import fr.micromania.entity.RememberMeToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface RememberMeTokenRepository extends JpaRepository<RememberMeToken, String> {

    Optional<RememberMeToken> findBySerieAndUserType(String serie, String userType);

    long deleteBySerie(String serie);

    long deleteByUsernameAndUserType(String username, String userType);

    @Modifying
    @Query("DELETE FROM RememberMeToken t WHERE t.dateDerniere < :threshold")
    void deleteOlderThan(@Param("threshold") LocalDateTime threshold);
}
