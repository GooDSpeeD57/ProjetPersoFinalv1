package fr.micromania.entity.catalog;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "produit_prix")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ProduitPrix {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_prix")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_variant", nullable = false)
    private ProduitVariant variant;

    @Column(name = "prix_neuf", precision = 10, scale = 2)
    private BigDecimal prixNeuf;

    @Column(name = "prix_occasion", precision = 10, scale = 2)
    private BigDecimal prixOccasion;

    @Column(name = "prix_reprise", precision = 10, scale = 2)
    private BigDecimal prixReprise;

    @Column(name = "prix_location", precision = 10, scale = 2)
    private BigDecimal prixLocation;

    @Column(name = "date_debut", nullable = false)
    private LocalDateTime dateDebut;

    @Column(name = "date_fin")
    private LocalDateTime dateFin;

    @Column(name = "actif", nullable = false)
    private boolean actif = true;
}
