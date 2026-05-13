package fr.micromania.repository;

import fr.micromania.entity.referentiel.ProviderAuth;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ProviderAuthRepository extends JpaRepository<ProviderAuth, Long> {
    Optional<ProviderAuth> findByCode(String code);
}
