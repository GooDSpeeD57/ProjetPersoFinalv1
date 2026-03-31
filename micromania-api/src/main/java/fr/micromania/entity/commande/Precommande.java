package fr.micromania.entity.commande;

import fr.micromania.entity.Client;
import fr.micromania.entity.referentiel.*;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "precommande")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Precommande {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_precommande")
    private Long id;

    @Column(name = "reference_precommande", nullable = false, unique = true, length = 50)
    private String referencePrecommande;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_client", nullable = false)
    private Client client;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_statut_precommande", nullable = false)
    private StatutPrecommande statutPrecommande;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_canal_vente", nullable = false)
    private CanalVente canalVente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_mode_paiement")
    private ModePaiement modePaiement;

    @Column(name = "acompte_paye", nullable = false, precision = 10, scale = 2)
    private BigDecimal acomptePaye = BigDecimal.ZERO;

    @Column(name = "montant_total_estime", nullable = false, precision = 10, scale = 2)
    private BigDecimal montantTotalEstime = BigDecimal.ZERO;

    @Column(name = "date_precommande", nullable = false, updatable = false)
    private LocalDateTime datePrecommande;

    @Column(name = "date_disponibilite_estimee")
    private LocalDateTime dateDisponibiliteEstimee;

    @Column(name = "date_conversion_commande")
    private LocalDateTime dateConversionCommande;

    @Column(name = "commentaire_client", columnDefinition = "TEXT")
    private String commentaireClient;

    @OneToMany(mappedBy = "precommande", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<PrecommandeLigne> lignes = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        datePrecommande = LocalDateTime.now();
    }
}
