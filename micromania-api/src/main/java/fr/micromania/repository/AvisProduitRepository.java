package fr.micromania.repository;

import fr.micromania.entity.catalog.AvisProduit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface AvisProduitRepository extends JpaRepository<AvisProduit, Long> {

    interface ProduitAvisStatsProjection {
        Long getProduitId();
        long getNbAvis();
        Double getNoteMoyenne();
    }

    @EntityGraph(attributePaths = {"statutAvis"})
    Optional<AvisProduit> findByClientIdAndProduitId(Long idClient, Long idProduit);

    boolean existsByClientIdAndProduitId(Long idClient, Long idProduit);

    @Query("""
        SELECT CASE WHEN COUNT(lf.id) > 0 THEN true ELSE false END
        FROM LigneFacture lf
        JOIN lf.facture f
        JOIN lf.variant pv
        WHERE f.client.id = :idClient
          AND pv.produit.id = :idProduit
        """)
    boolean hasClientPurchasedProduct(@Param("idClient") Long idClient, @Param("idProduit") Long idProduit);

    Page<AvisProduit> findByProduitIdAndStatutAvisCode(Long idProduit, String code, Pageable pageable);

    Page<AvisProduit> findByStatutAvisCode(String code, Pageable pageable);

    @EntityGraph(attributePaths = {"client"})
    List<AvisProduit> findTop5ByProduitIdAndStatutAvisCodeOrderByDateCreationDesc(Long idProduit, String code);

    long countByProduitIdAndStatutAvisCode(Long idProduit, String code);

    @Query("""
        SELECT AVG(a.note) FROM AvisProduit a
        JOIN a.statutAvis s
        WHERE a.produit.id = :idProduit AND s.code = 'APPROUVE'
        """)
    Double findNoteMoyenne(@Param("idProduit") Long idProduit);

    @Query("""
        SELECT a.produit.id as produitId,
               COUNT(a.id) as nbAvis,
               AVG(a.note) as noteMoyenne
        FROM AvisProduit a
        JOIN a.statutAvis s
        WHERE a.produit.id IN :ids AND s.code = 'APPROUVE'
        GROUP BY a.produit.id
        """)
    List<ProduitAvisStatsProjection> findStatsByProduitIds(@Param("ids") Collection<Long> ids);
}
