package fr.micromania.repository;

import fr.micromania.entity.Client;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {

    Optional<Client> findByEmailAndDeletedFalse(String email);

    Optional<Client> findByPseudoAndDeletedFalse(String pseudo);

    Optional<Client> findByIdAndDeletedFalse(Long id);

    boolean existsByEmailAndDeletedFalse(String email);

    boolean existsByPseudoAndDeletedFalse(String pseudo);

    boolean existsByTelephoneAndDeletedFalse(String telephone);

    Optional<Client> findByTokenVerificationEmail(String token);

    Optional<Client> findByNumeroCarteFidelite(String numeroCarteFidelite);

    /** Recherche full-text sur pseudo / nom / prénom / email */
    @Query("""
        SELECT c FROM Client c
        WHERE c.deleted = false
          AND (
              LOWER(c.pseudo)  LIKE LOWER(CONCAT('%', :q, '%')) OR
              LOWER(c.nom)     LIKE LOWER(CONCAT('%', :q, '%')) OR
              LOWER(c.prenom)  LIKE LOWER(CONCAT('%', :q, '%')) OR
              LOWER(c.email)   LIKE LOWER(CONCAT('%', :q, '%'))
          )
        """)
    Page<Client> search(@Param("q") String query, Pageable pageable);

    /** Clients actifs par type de fidélité */
    @Query("""
        SELECT c FROM Client c
        JOIN c.typeFidelite tf
        WHERE c.deleted = false AND c.actif = true
          AND tf.code = :code
        """)
    List<Client> findByTypeFideliteCode(@Param("code") String code);

    /** Comptes en attente d'activation (créés par employé) */
    @Query("""
        SELECT c FROM Client c
        WHERE c.deleted = false
          AND c.creeParEmploye = true
          AND c.compteActive = false
        """)
    List<Client> findComptesEnAttenteActivation();

    /** Tokens de vérification expirés à purger */
    @Query("""
        SELECT c FROM Client c
        WHERE c.tokenVerificationExpireLe < :now
          AND c.emailVerifie = false
          AND c.deleted = false
        """)
    List<Client> findTokensExpires(@Param("now") LocalDateTime now);

    /** Soft-delete : marque deleted=true sans supprimer */
    @Modifying
    @Query("UPDATE Client c SET c.deleted = true, c.actif = false WHERE c.id = :id")
    void softDelete(@Param("id") Long id);
}
