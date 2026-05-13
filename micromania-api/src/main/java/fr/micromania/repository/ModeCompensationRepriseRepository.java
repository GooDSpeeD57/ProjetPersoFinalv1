package fr.micromania.repository;

import fr.micromania.entity.referentiel.ModeCompensationReprise;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ModeCompensationRepriseRepository extends JpaRepository<ModeCompensationReprise, Long> {
    List<ModeCompensationReprise> findAllByOrderByCodeAsc();
}
