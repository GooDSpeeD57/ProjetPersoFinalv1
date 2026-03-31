package fr.micromania.repository;

import fr.micromania.entity.tcg.TcgCarteInventaire;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TcgCarteInventaireRepository extends JpaRepository<TcgCarteInventaire, Long> {

    @Query("""
        SELECT t FROM TcgCarteInventaire t
        WHERE t.disponible = true
          AND (:idMagasin  IS NULL OR t.magasin.id = :idMagasin)
          AND (:nomCarte   IS NULL OR LOWER(t.tcgCarteReference.nomCarte) LIKE LOWER(CONCAT('%', :nomCarte, '%')))
          AND (:codeEtat   IS NULL OR t.etatCarteTcg.code = :codeEtat)
          AND (:langue     IS NULL OR t.langue = :langue)
          AND (:foil       IS NULL OR t.foil = :foil)
          AND (:idJeu      IS NULL OR t.tcgCarteReference.tcgExtension.tcgJeu.id = :idJeu)
        ORDER BY t.tcgCarteReference.nomCarte ASC
        """)
    Page<TcgCarteInventaire> search(
        @Param("idMagasin") Long idMagasin,
        @Param("nomCarte") String nomCarte,
        @Param("codeEtat") String codeEtat,
        @Param("langue") String langue,
        @Param("foil") Boolean foil,
        @Param("idJeu") Long idJeu,
        Pageable pageable
    );
}
