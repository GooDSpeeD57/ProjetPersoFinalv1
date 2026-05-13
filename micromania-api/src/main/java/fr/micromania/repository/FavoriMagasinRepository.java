package fr.micromania.repository;

import fr.micromania.entity.FavoriMagasin;
import fr.micromania.entity.FavoriMagasinId;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface FavoriMagasinRepository extends JpaRepository<FavoriMagasin, FavoriMagasinId> {
    List<FavoriMagasin> findByClientId(Long idClient);
    boolean existsByClientIdAndMagasinId(Long idClient, Long idMagasin);
}
