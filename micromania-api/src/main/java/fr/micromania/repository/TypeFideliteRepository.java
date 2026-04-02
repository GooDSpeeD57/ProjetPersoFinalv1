package fr.micromania.repository;

import fr.micromania.entity.referentiel.TypeFidelite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TypeFideliteRepository extends JpaRepository<TypeFidelite, Long> {
    Optional<TypeFidelite> findByCode(String code);
}
