package fr.micromania.entity;

import fr.micromania.entity.catalog.ProduitVariant;
import fr.micromania.entity.tcg.TcgCarteReference;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "reprise_ligne")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class RepriseeLigne {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_reprise_ligne")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_reprise", nullable = false)
    private Reprise reprise;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_variant")
    private ProduitVariant variant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_tcg_carte_reference")
    private TcgCarteReference tcgCarteReference;

    @Column(name = "description_libre", length = 255)
    private String descriptionLibre;

    @Column(name = "quantite", nullable = false)
    private int quantite = 1;

    @Column(name = "etat_general", length = 50)
    private String etatGeneral;

    @Column(name = "prix_estime_unitaire", nullable = false, precision = 10, scale = 2)
    private BigDecimal prixEstimeUnitaire = BigDecimal.ZERO;

    @Column(name = "prix_valide_unitaire", precision = 10, scale = 2)
    private BigDecimal prixValideUnitaire;

    @Column(name = "cree_stock_occasion", nullable = false)
    private boolean creeStockOccasion = false;

    @Column(name = "numero_serie", length = 100)
    private String numeroSerie;

    @Column(name = "commentaires", columnDefinition = "TEXT")
    private String commentaires;
}
