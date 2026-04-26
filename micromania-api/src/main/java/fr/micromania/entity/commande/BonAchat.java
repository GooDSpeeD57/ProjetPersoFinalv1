package fr.micromania.entity.commande;

import fr.micromania.entity.Client;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "bon_achat")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class BonAchat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_bon_achat")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_client", nullable = false)
    private Client client;

    @Column(name = "code_bon", nullable = false, unique = true, length = 50)
    private String codeBon;

    @Column(name = "valeur", nullable = false, precision = 10, scale = 2)
    private BigDecimal valeur;

    @Column(name = "points_utilises", nullable = false)
    private int pointsUtilises;

    @Column(name = "utilise", nullable = false)
    private boolean utilise = false;

    /** Référence circulaire volontairement lazy — jointure avec facture */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_facture")
    private Facture facture;

    @Column(name = "date_creation", nullable = false, updatable = false)
    private LocalDateTime dateCreation;

    @Column(name = "date_expiration")
    private LocalDateTime dateExpiration;

    @Column(name = "date_utilisation")
    private LocalDateTime dateUtilisation;

    @PrePersist
    protected void onCreate() {
        dateCreation    = LocalDateTime.now();
        dateExpiration  = dateCreation.plusMonths(6);
    }
}
