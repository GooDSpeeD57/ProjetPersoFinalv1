package fr.micromania.entity.commande;

import fr.micromania.entity.referentiel.ModePaiement;
import fr.micromania.entity.referentiel.StatutPaiement;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "paiement_transaction")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PaiementTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_paiement_transaction")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_commande")
    private Commande commande;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_precommande")
    private Precommande precommande;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_mode_paiement", nullable = false)
    private ModePaiement modePaiement;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_statut_paiement", nullable = false)
    private StatutPaiement statutPaiement;

    @Column(name = "provider_reference", length = 255)
    private String providerReference;

    @Column(name = "montant", nullable = false, precision = 10, scale = 2)
    private BigDecimal montant;

    @Column(name = "devise", nullable = false, length = 10)
    private String devise = "EUR";

    @Column(name = "date_creation", nullable = false, updatable = false)
    private LocalDateTime dateCreation;

    @Column(name = "date_confirmation")
    private LocalDateTime dateConfirmation;

    @Column(name = "commentaire", length = 255)
    private String commentaire;

    @PrePersist
    protected void onCreate() {
        dateCreation = LocalDateTime.now();
    }
}
