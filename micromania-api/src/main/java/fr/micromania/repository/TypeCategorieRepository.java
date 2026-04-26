package fr.micromania.repository;

import fr.micromania.entity.referentiel.TypeCategorie;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TypeCategorieRepository extends JpaRepository<TypeCategorie, Long> {
    List<TypeCategorie> findAllByOrderByCodeAsc();
}
