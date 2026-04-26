package fr.micromania.repository;

import fr.micromania.entity.referentiel.RatioPoints;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RatioPointsRepository extends JpaRepository<RatioPoints, Long> {

    Optional<RatioPoints> findByTypeCategorieCodeAndTypeFideliteCode(String typeCategorieCode, String typeFideliteCode);

    List<RatioPoints> findAllByTypeFideliteCode(String typeFideliteCode);
}
