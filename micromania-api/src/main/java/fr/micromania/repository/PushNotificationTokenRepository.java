package fr.micromania.repository;

import fr.micromania.entity.PushNotificationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PushNotificationTokenRepository extends JpaRepository<PushNotificationToken, Long> {
    List<PushNotificationToken> findByClientIdAndActifTrue(Long idClient);
}
