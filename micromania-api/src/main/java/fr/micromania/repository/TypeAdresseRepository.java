package fr.micromania.repository;

import fr.micromania.entity.referentiel.TypeAdresse;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TypeAdresseRepository extends JpaRepository<TypeAdresse, Long> {
    Optional<TypeAdresse> findByCode(String code);
}
