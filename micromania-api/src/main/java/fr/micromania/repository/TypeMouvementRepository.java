package fr.micromania.repository;

import fr.micromania.entity.referentiel.TypeMouvement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TypeMouvementRepository extends JpaRepository<TypeMouvement, Long> {
    Optional<TypeMouvement> findByCode(String code);
}