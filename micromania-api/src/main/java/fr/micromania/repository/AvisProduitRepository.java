package fr.micromania.repository;

import fr.micromania.entity.catalog.AvisProduit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface AvisProduitRepository extends JpaRepository<AvisProduit, Long> {

    Optional<AvisProduit> findByClientIdAndProduitId(Long idClient, Long idProduit);

    boolean existsByClientIdAndProduitId(Long idClient, Long idProduit);

    Page<AvisProduit> findByProduitIdAndStatutAvisCode(Long idProduit, String code, Pageable pageable);

    Page<AvisProduit> findByStatutAvisCode(String code, Pageable pageable);

    @Query("""
        SELECT AVG(a.note) FROM AvisProduit a
        JOIN a.statutAvis s
        WHERE a.produit.id = :idProduit AND s.code = 'APPROUVE'
        """)
    Double findNoteMoyenne(@Param("idProduit") Long idProduit);
}
