package fr.micromania.repository;

import fr.micromania.entity.ClientAuthProvider;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ClientAuthProviderRepository extends JpaRepository<ClientAuthProvider, Long> {
    List<ClientAuthProvider> findByClientId(Long idClient);
}
