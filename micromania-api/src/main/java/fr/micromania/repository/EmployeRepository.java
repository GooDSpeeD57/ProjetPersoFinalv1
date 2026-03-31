package fr.micromania.repository;

import fr.micromania.entity.Employe;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeRepository extends JpaRepository<Employe, Long> {

    Optional<Employe> findByEmailAndDeletedFalse(String email);

    Optional<Employe> findByIdAndDeletedFalse(Long id);

    boolean existsByEmailAndDeletedFalse(String email);

    List<Employe> findByMagasinIdAndDeletedFalse(Long idMagasin);

    @Query("""
        SELECT e FROM Employe e
        JOIN e.role r
        WHERE e.deleted = false AND r.code = :role
        """)
    List<Employe> findByRoleCode(@Param("role") String role);

    @Query("""
        SELECT e FROM Employe e
        WHERE e.deleted = false
          AND (LOWER(e.nom)   LIKE LOWER(CONCAT('%', :q, '%')) OR
               LOWER(e.prenom) LIKE LOWER(CONCAT('%', :q, '%')) OR
               LOWER(e.email) LIKE LOWER(CONCAT('%', :q, '%')))
        """)
    Page<Employe> search(@Param("q") String query, Pageable pageable);

    @Modifying
    @Query("UPDATE Employe e SET e.deleted = true, e.actif = false WHERE e.id = :id")
    void softDelete(@Param("id") Long id);
}
