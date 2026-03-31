package fr.micromania.entity;

import fr.micromania.entity.referentiel.StatutAbonnement;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "abonnement_client")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AbonnementClient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_abonnement")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_client", nullable = false)
    private Client client;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_statut_abonnement", nullable = false)
    private StatutAbonnement statutAbonnement;

    @Column(name = "date_debut", nullable = false)
    private LocalDate dateDebut;

    @Column(name = "date_fin", nullable = false)
    private LocalDate dateFin;

    @Column(name = "montant_paye", nullable = false, precision = 10, scale = 2)
    private BigDecimal montantPaye;

    @Column(name = "date_paiement", nullable = false)
    private LocalDateTime datePaiement;

    @Column(name = "renouvellement_auto", nullable = false)
    private boolean renouvellementAuto = true;

    @Column(name = "date_resiliation")
    private LocalDateTime dateResiliation;
}
