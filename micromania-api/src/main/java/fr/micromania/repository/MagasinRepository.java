package fr.micromania.repository;

import fr.micromania.entity.Magasin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MagasinRepository extends JpaRepository<Magasin, Long> {

    List<Magasin> findByActifTrue();
}
