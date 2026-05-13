package fr.micromania.repository;

import fr.micromania.entity.referentiel.TypeReduction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TypeReductionRepository extends JpaRepository<TypeReduction, Long> {
    Optional<TypeReduction> findByCode(String code);
}
