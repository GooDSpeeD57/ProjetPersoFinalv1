package fr.micromania.repository;

import fr.micromania.entity.referentiel.EtatCarteTcg;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface EtatCarteTcgRepository extends JpaRepository<EtatCarteTcg, Long> {
    List<EtatCarteTcg> findAllByOrderByCodeAsc();
}
