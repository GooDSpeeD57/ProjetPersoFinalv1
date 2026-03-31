package fr.micromania.entity.tcg;

import fr.micromania.entity.Magasin;
import fr.micromania.entity.catalog.ProduitVariant;
import fr.micromania.entity.referentiel.EtatCarteTcg;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "tcg_carte_inventaire")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TcgCarteInventaire {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tcg_carte_inventaire")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_tcg_carte_reference", nullable = false)
    private TcgCarteReference tcgCarteReference;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_variant")
    private ProduitVariant variant;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_magasin", nullable = false)
    private Magasin magasin;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_etat_carte_tcg", nullable = false)
    private EtatCarteTcg etatCarteTcg;

    @Column(name = "langue", nullable = false, length = 10)
    private String langue = "fr";

    @Column(name = "foil", nullable = false)
    private boolean foil = false;

    @Column(name = "reverse_foil", nullable = false)
    private boolean reverseFoil = false;

    @Column(name = "alternate_art", nullable = false)
    private boolean alternateArt = false;

    @Column(name = "gradation", length = 50)
    private String gradation;

    @Column(name = "numero_serie_interne", unique = true, length = 100)
    private String numeroSerieInterne;

    @Column(name = "prix_achat", precision = 10, scale = 2)
    private BigDecimal prixAchat;

    @Column(name = "prix_vente", nullable = false, precision = 10, scale = 2)
    private BigDecimal prixVente;

    @Column(name = "provenance", nullable = false, length = 30)
    private String provenance = "STOCK";

    @Column(name = "disponible", nullable = false)
    private boolean disponible = true;

    @Column(name = "date_creation", nullable = false, updatable = false)
    private LocalDateTime dateCreation;

    @PrePersist
    protected void onCreate() {
        dateCreation = LocalDateTime.now();
    }
}
