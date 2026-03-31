package fr.micromania.entity.stock;

import fr.micromania.entity.Magasin;
import fr.micromania.entity.catalog.ProduitVariant;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "stock_magasin",
    uniqueConstraints = @UniqueConstraint(columnNames = {"id_variant", "id_magasin"}))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class StockMagasin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_stock_magasin")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_variant", nullable = false)
    private ProduitVariant variant;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_magasin", nullable = false)
    private Magasin magasin;

    @Column(name = "quantite_neuf", nullable = false)
    private int quantiteNeuf = 0;

    @Column(name = "quantite_occasion", nullable = false)
    private int quantiteOccasion = 0;

    @Column(name = "quantite_reprise", nullable = false)
    private int quantiteReprise = 0;

    @Column(name = "quantite_reservee", nullable = false)
    private int quantiteReservee = 0;

    @Column(name = "quantite_disponible", nullable = false)
    private int quantiteDisponible = 0;

    @Version
    @Column(name = "version", nullable = false)
    private Long version;

    @Column(name = "date_modification", nullable = false)
    private LocalDateTime dateModification;

    @PrePersist @PreUpdate
    protected void onUpdate() {
        dateModification = LocalDateTime.now();
    }
}
