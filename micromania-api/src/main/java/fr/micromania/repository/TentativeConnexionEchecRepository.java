package fr.micromania.repository;

import fr.micromania.entity.securite.TentativeConnexionEchec;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface TentativeConnexionEchecRepository extends JpaRepository<TentativeConnexionEchec, Long> {

    Optional<TentativeConnexionEchec> findByUserTypeAndEmailTente(String userType, String emailTente);

    @Modifying
    @Query("DELETE FROM TentativeConnexionEchec t WHERE t.userType = :userType AND t.emailTente = :email")
    void deleteByUserTypeAndEmailTente(@Param("userType") String userType, @Param("email") String email);

    @Modifying
    @Query("DELETE FROM TentativeConnexionEchec t WHERE t.bloqueJusquAu < :now AND t.bloqueJusquAu IS NOT NULL")
    void deleteExpiredLocks(@Param("now") LocalDateTime now);
}
