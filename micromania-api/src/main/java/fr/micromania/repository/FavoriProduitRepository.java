package fr.micromania.repository;

import fr.micromania.entity.catalog.FavoriProduit;
import fr.micromania.entity.catalog.FavoriProduitId;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface FavoriProduitRepository extends JpaRepository<FavoriProduit, FavoriProduitId> {
    List<FavoriProduit> findByClientId(Long idClient);
    boolean existsByClientIdAndProduitId(Long idClient, Long idProduit);
}
