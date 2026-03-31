package fr.micromania.entity.commande;

import fr.micromania.entity.catalog.ProduitPrix;
import fr.micromania.entity.catalog.ProduitVariant;
import fr.micromania.entity.referentiel.TauxTva;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "ligne_facture")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class LigneFacture {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_ligne_facture")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_facture", nullable = false)
    private Facture facture;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_ligne_commande")
    private LigneCommande ligneCommande;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_variant", nullable = false)
    private ProduitVariant variant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_prix")
    private ProduitPrix prix;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_taux_tva")
    private TauxTva tauxTva;

    @Column(name = "quantite", nullable = false)
    private int quantite;

    @Column(name = "prix_unitaire", nullable = false, precision = 10, scale = 2)
    private BigDecimal prixUnitaire;

    @Column(name = "taux_tva_applique", nullable = false, precision = 5, scale = 2)
    private BigDecimal tauxTvaApplique;

    @Column(name = "montant_ligne", nullable = false, precision = 10, scale = 2)
    private BigDecimal montantLigne;

    @Column(name = "montant_ht_ligne", nullable = false, precision = 10, scale = 2)
    private BigDecimal montantHtLigne = BigDecimal.ZERO;

    @Column(name = "montant_tva_ligne", nullable = false, precision = 10, scale = 2)
    private BigDecimal montantTvaLigne = BigDecimal.ZERO;
}
