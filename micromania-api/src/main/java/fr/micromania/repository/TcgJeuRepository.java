package fr.micromania.repository;

import fr.micromania.entity.tcg.TcgJeu;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TcgJeuRepository extends JpaRepository<TcgJeu, Long> {
    List<TcgJeu> findByActifTrueOrderByNomAsc();
}
