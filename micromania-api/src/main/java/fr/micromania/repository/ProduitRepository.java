package fr.micromania.repository;

import fr.micromania.entity.catalog.Produit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface ProduitRepository extends JpaRepository<Produit, Long> {

    Optional<Produit> findBySlugAndDeletedFalse(String slug);

    Optional<Produit> findByIdAndDeletedFalse(Long id);

    boolean existsBySlugAndDeletedFalse(String slug);

    @Query("""
        SELECT p FROM Produit p
        WHERE p.deleted = false AND p.actif = true
          AND (:idCategorie IS NULL OR p.categorie.id = :idCategorie)
          AND (:q IS NULL OR
               LOWER(p.nom) LIKE LOWER(CONCAT('%', :q, '%')) OR
               LOWER(p.editeur) LIKE LOWER(CONCAT('%', :q, '%')))
          AND (:niveauAccesMin IS NULL OR p.niveauAccesMin = :niveauAccesMin)
        """)
    Page<Produit> search(
        @Param("q") String query,
        @Param("idCategorie") Long idCategorie,
        @Param("niveauAccesMin") String niveauAccesMin,
        Pageable pageable
    );

    @Query("SELECT p FROM Produit p WHERE p.deleted = false AND p.actif = true AND p.misEnAvant = true")
    java.util.List<Produit> findMisEnAvant();

    @Modifying
    @Query("UPDATE Produit p SET p.deleted = true, p.actif = false WHERE p.id = :id")
    void softDelete(@Param("id") Long id);
}
