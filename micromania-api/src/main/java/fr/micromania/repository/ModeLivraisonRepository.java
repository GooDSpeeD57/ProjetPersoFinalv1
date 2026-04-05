package fr.micromania.repository;

import fr.micromania.entity.referentiel.ModeLivraison;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ModeLivraisonRepository extends JpaRepository<ModeLivraison, Long> {
    Optional<ModeLivraison> findByCode(String code);
}