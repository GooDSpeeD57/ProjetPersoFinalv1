package fr.micromania.repository;

import fr.micromania.entity.catalog.ProduitScreenshot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProduitScreenshotRepository extends JpaRepository<ProduitScreenshot, Long> {

    List<ProduitScreenshot> findByProduitIdOrderByOrdreAffichageAsc(Long idProduit);
}
