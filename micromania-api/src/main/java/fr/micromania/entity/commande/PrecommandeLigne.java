package fr.micromania.entity.commande;

import fr.micromania.entity.catalog.ProduitVariant;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "precommande_ligne")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PrecommandeLigne {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_precommande_ligne")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_precommande", nullable = false)
    private Precommande precommande;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_variant", nullable = false)
    private ProduitVariant variant;

    @Column(name = "quantite", nullable = false)
    private int quantite;

    @Column(name = "prix_unitaire_estime", nullable = false, precision = 10, scale = 2)
    private BigDecimal prixUnitaireEstime;

    @Column(name = "montant_ligne_estime", nullable = false, precision = 10, scale = 2)
    private BigDecimal montantLigneEstime;
}
