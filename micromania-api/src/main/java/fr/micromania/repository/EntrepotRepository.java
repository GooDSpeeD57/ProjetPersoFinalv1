package fr.micromania.repository;

import fr.micromania.entity.Entrepot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface EntrepotRepository extends JpaRepository<Entrepot, Long> {

    List<Entrepot> findByActifTrue();

    Optional<Entrepot> findByCode(String code);
}
