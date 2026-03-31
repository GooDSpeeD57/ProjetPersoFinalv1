package fr.micromania.entity.securite;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "connexion_log")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ConnexionLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_connexion_log")
    private Long id;

    @Column(name = "user_type", nullable = false, length = 10)
    private String userType;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "email_tente", length = 150)
    private String emailTente;

    @Column(name = "ip", nullable = false, length = 45)
    private String ip;

    @Column(name = "user_agent", length = 512)
    private String userAgent;

    @Column(name = "succes", nullable = false)
    private boolean succes = false;

    @Column(name = "motif_echec", length = 100)
    private String motifEchec;

    @Column(name = "provider_auth", length = 50)
    private String providerAuth;

    @Column(name = "date_connexion", nullable = false, updatable = false)
    private LocalDateTime dateConnexion;

    @PrePersist
    protected void onCreate() {
        dateConnexion = LocalDateTime.now();
    }
}
