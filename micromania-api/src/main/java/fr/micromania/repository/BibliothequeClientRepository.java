package fr.micromania.repository;

import fr.micromania.entity.BibliothequeClient;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BibliothequeClientRepository extends JpaRepository<BibliothequeClient, Long> {
    List<BibliothequeClient> findByClientId(Long idClient);
}
