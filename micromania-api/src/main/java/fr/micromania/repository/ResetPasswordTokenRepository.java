package fr.micromania.repository;

import fr.micromania.entity.ResetPasswordToken;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ResetPasswordTokenRepository extends JpaRepository<ResetPasswordToken, Long> {

    Optional<ResetPasswordToken> findByTokenAndUtiliseFalseAndExpireLeAfter(String token, LocalDateTime now);

    List<ResetPasswordToken> findByClientId(Long idClient);

    @Modifying
    @Query("DELETE FROM ResetPasswordToken t WHERE t.expireLe < :now")
    void deleteExpired(@Param("now") LocalDateTime now);
}
