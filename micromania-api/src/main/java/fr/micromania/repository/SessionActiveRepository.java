package fr.micromania.repository;

import fr.micromania.entity.securite.SessionActive;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface SessionActiveRepository extends JpaRepository<SessionActive, Long> {
    Optional<SessionActive> findByTokenSession(String tokenSession);
    List<SessionActive> findByUserIdAndUserTypeAndActifTrue(Long userId, String userType);
}
