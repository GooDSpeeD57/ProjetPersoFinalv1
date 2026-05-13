package fr.micromania.repository;

import fr.micromania.entity.catalog.ProduitImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface ProduitImageRepository extends JpaRepository<ProduitImage, Long> {

    /** Retire le drapeau principale de toutes les images d'un variant. */
    @Modifying
    @Query("UPDATE ProduitImage i SET i.principale = false WHERE i.variant.id = :idVariant")
    void clearPrincipaleByVariantId(Long idVariant);
}
