package fr.micromania.entity.commande;

import fr.micromania.entity.Client;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "promotion_usage")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PromotionUsage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_promotion_usage")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_promotion", nullable = false)
    private Promotion promotion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_client")
    private Client client;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_commande")
    private Commande commande;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_facture")
    private Facture facture;

    @Column(name = "montant_commande_ht", nullable = false, precision = 10, scale = 2)
    private BigDecimal montantCommandeHt = BigDecimal.ZERO;

    @Column(name = "montant_remise", nullable = false, precision = 10, scale = 2)
    private BigDecimal montantRemise = BigDecimal.ZERO;

    @Column(name = "date_utilisation", nullable = false, updatable = false)
    private LocalDateTime dateUtilisation;

    @PrePersist
    protected void onCreate() {
        dateUtilisation = LocalDateTime.now();
    }
}
