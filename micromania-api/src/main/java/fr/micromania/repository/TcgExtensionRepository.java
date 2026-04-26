package fr.micromania.repository;

import fr.micromania.entity.tcg.TcgExtension;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TcgExtensionRepository extends JpaRepository<TcgExtension, Long> {
    List<TcgExtension> findByTcgJeuIdAndActifTrueOrderByNomAsc(Long idTcgJeu);
}
