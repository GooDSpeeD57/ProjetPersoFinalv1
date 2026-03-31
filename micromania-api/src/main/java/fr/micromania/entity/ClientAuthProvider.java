package fr.micromania.entity;

import fr.micromania.entity.referentiel.ProviderAuth;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "client_auth_provider",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"id_client", "id_provider_auth"}),
        @UniqueConstraint(columnNames = {"id_provider_auth", "provider_user_id"})
    })
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ClientAuthProvider {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_client_auth_provider")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_client", nullable = false)
    private Client client;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_provider_auth", nullable = false)
    private ProviderAuth providerAuth;

    @Column(name = "provider_user_id", nullable = false, length = 255)
    private String providerUserId;

    @Column(name = "email_provider", length = 150)
    private String emailProvider;

    @Column(name = "date_liaison", nullable = false, updatable = false)
    private LocalDateTime dateLiaison;

    @Column(name = "actif", nullable = false)
    private boolean actif = true;

    @PrePersist
    protected void onCreate() {
        dateLiaison = LocalDateTime.now();
    }
}
