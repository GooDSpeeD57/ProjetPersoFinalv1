package fr.micromania.repository;

import fr.micromania.entity.tcg.TcgCarteReference;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TcgCarteReferenceRepository extends JpaRepository<TcgCarteReference, Long> {
}
