package fr.micromania.security;

import fr.micromania.repository.TentativeConnexionEchecRepository;
import fr.micromania.repository.TokenBlacklistRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class TokenBlacklistCleanup {

    private final TokenBlacklistRepository tokenBlacklistRepository;
    private final TentativeConnexionEchecRepository tentativeRepository;

    @Scheduled(fixedRate = 3_600_000)
    @Transactional
    public void purger() {
        LocalDateTime now = LocalDateTime.now();
        tokenBlacklistRepository.deleteExpired(now);
        tentativeRepository.deleteExpiredLocks(now);
        log.debug("Purge blacklist JWT et tentatives expirées effectuée");
    }
}
