package fr.micromania.repository;

import fr.micromania.entity.commande.LignePanier;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface LignePanierRepository extends JpaRepository<LignePanier, Long> {

    Optional<LignePanier> findByPanierIdAndVariantId(Long idPanier, Long idVariant);

    @Modifying
    @Query("DELETE FROM LignePanier l WHERE l.panier.id = :idPanier")
    void deleteByPanierId(@Param("idPanier") Long idPanier);
}
