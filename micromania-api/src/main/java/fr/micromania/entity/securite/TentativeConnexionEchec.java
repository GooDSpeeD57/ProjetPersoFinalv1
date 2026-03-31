package fr.micromania.entity.securite;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tentative_connexion_echec",
    uniqueConstraints = @UniqueConstraint(columnNames = {"user_type", "email_tente"}))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TentativeConnexionEchec {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tentative")
    private Long id;

    @Column(name = "user_type", nullable = false, length = 10)
    private String userType;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "email_tente", length = 150)
    private String emailTente;

    @Column(name = "ip", nullable = false, length = 45)
    private String ip;

    @Column(name = "nb_tentatives", nullable = false)
    private int nbTentatives = 1;

    @Column(name = "premiere_tentative", nullable = false, updatable = false)
    private LocalDateTime premiereTentative;

    @Column(name = "derniere_tentative", nullable = false)
    private LocalDateTime derniereTentative;

    @Column(name = "bloque_jusqu_au")
    private LocalDateTime bloqueJusquAu;

    @PrePersist
    protected void onCreate() {
        premiereTentative = LocalDateTime.now();
        derniereTentative = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        derniereTentative = LocalDateTime.now();
    }
}
