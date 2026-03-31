package fr.micromania.entity.securite;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "session_active")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class SessionActive {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_session")
    private Long id;

    @Column(name = "user_type", nullable = false, length = 10)
    private String userType;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "token_session", nullable = false, unique = true, length = 512)
    private String tokenSession;

    @Column(name = "ip_creation", nullable = false, length = 45)
    private String ipCreation;

    @Column(name = "ip_derniere_activite", length = 45)
    private String ipDerniereActivite;

    @Column(name = "user_agent", length = 512)
    private String userAgent;

    @Column(name = "actif", nullable = false)
    private boolean actif = true;

    @Column(name = "date_creation", nullable = false, updatable = false)
    private LocalDateTime dateCreation;

    @Column(name = "date_expiration", nullable = false)
    private LocalDateTime dateExpiration;

    @Column(name = "date_derniere_activite", nullable = false)
    private LocalDateTime dateDerniereActivite;

    @Column(name = "motif_invalidation", length = 100)
    private String motifInvalidation;

    @PrePersist
    protected void onCreate() {
        dateCreation = LocalDateTime.now();
        dateDerniereActivite = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        dateDerniereActivite = LocalDateTime.now();
    }
}
