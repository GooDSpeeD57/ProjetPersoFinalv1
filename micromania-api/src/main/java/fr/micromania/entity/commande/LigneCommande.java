package fr.micromania.entity.commande;

import fr.micromania.entity.catalog.ProduitPrix;
import fr.micromania.entity.catalog.ProduitVariant;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "ligne_commande")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class LigneCommande {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_ligne_commande")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_commande", nullable = false)
    private Commande commande;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_variant", nullable = false)
    private ProduitVariant variant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_prix")
    private ProduitPrix prix;

    @Column(name = "quantite", nullable = false)
    private int quantite;

    @Column(name = "prix_unitaire", nullable = false, precision = 10, scale = 2)
    private BigDecimal prixUnitaire;

    @Column(name = "montant_ligne", nullable = false, precision = 10, scale = 2)
    private BigDecimal montantLigne;
}
