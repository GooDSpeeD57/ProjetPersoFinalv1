package fr.micromania.repository;

import fr.micromania.entity.securite.TokenBlacklist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface TokenBlacklistRepository extends JpaRepository<TokenBlacklist, String> {

    boolean existsByJti(String jti);

    @Modifying
    @Query("DELETE FROM TokenBlacklist t WHERE t.expireLe < :now")
    void deleteExpired(@Param("now") LocalDateTime now);
}
