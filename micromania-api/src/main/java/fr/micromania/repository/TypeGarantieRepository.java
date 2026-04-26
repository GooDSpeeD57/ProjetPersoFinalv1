package fr.micromania.repository;

import fr.micromania.entity.referentiel.TypeGarantie;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TypeGarantieRepository extends JpaRepository<TypeGarantie, Long> {
    List<TypeGarantie> findAllByOrderByCodeAsc();

    /** Retourne les types liés à une catégorie donnée + ceux sans catégorie (universels). */
    @org.springframework.data.jpa.repository.Query(
        "SELECT tg FROM TypeGarantie tg WHERE tg.categorie.id = :categorieId OR tg.categorie IS NULL ORDER BY tg.code ASC")
    List<TypeGarantie> findByCategorieIdOrUniversel(@org.springframework.data.repository.query.Param("categorieId") Long categorieId);
}
