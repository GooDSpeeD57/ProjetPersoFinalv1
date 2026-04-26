package fr.micromania.repository;

import fr.micromania.entity.CleProduit;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CleProduitRepository extends JpaRepository<CleProduit, Long> {
    Optional<CleProduit> findFirstByVariantIdAndUtiliseeFalse(Long idVariant);
}
