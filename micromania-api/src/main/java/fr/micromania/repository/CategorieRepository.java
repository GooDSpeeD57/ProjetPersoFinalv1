package fr.micromania.repository;

import fr.micromania.entity.catalog.Categorie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface CategorieRepository extends JpaRepository<Categorie, Long> {

    List<Categorie> findByActifTrue();

    Optional<Categorie> findByNomIgnoreCase(String nom);

    List<Categorie> findByTypeCategorieCode(String code);
}
