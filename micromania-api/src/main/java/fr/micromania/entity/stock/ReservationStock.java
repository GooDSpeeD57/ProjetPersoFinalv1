package fr.micromania.entity.stock;

import fr.micromania.entity.Entrepot;
import fr.micromania.entity.Magasin;
import fr.micromania.entity.catalog.ProduitVariant;
import fr.micromania.entity.commande.Commande;
import fr.micromania.entity.commande.Panier;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "reservation_stock")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ReservationStock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_reservation_stock")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_commande")
    private Commande commande;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_panier")
    private Panier panier;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_variant", nullable = false)
    private ProduitVariant variant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_magasin")
    private Magasin magasin;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_entrepot")
    private Entrepot entrepot;

    @Column(name = "quantite", nullable = false)
    private int quantite;

    @Column(name = "expire_le")
    private LocalDateTime expireLe;

    @Column(name = "date_creation", nullable = false, updatable = false)
    private LocalDateTime dateCreation;

    @PrePersist
    protected void onCreate() {
        dateCreation = LocalDateTime.now();
    }
}
