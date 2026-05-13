package fr.micromania.repository;

import fr.micromania.entity.referentiel.TypeRetour;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TypeRetourRepository extends JpaRepository<TypeRetour, Long> {
    List<TypeRetour> findAllByOrderByCodeAsc();
}
