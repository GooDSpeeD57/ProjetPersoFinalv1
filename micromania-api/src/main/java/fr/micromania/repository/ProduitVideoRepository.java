package fr.micromania.repository;

import fr.micromania.entity.catalog.ProduitVideo;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProduitVideoRepository extends JpaRepository<ProduitVideo, Long> {
    List<ProduitVideo> findByProduitIdOrderByOrdreAffichageAsc(Long idProduit);
}
