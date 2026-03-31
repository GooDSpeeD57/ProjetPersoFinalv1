package fr.micromania.entity.stock;

import fr.micromania.entity.Entrepot;
import fr.micromania.entity.Magasin;
import fr.micromania.entity.catalog.ProduitVariant;
import fr.micromania.entity.referentiel.TypeMouvement;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "mouvement_stock")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class MouvementStock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_mouvement")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_variant", nullable = false)
    private ProduitVariant variant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_magasin")
    private Magasin magasin;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_entrepot")
    private Entrepot entrepot;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_type_mouvement", nullable = false)
    private TypeMouvement typeMouvement;

    @Column(name = "quantite", nullable = false)
    private int quantite;

    @Column(name = "source_stock", nullable = false, length = 20)
    private String sourceStock = "NEUF";

    @Column(name = "date_mouvement", nullable = false, updatable = false)
    private LocalDateTime dateMouvement;

    @Column(name = "commentaire", columnDefinition = "TEXT")
    private String commentaire;

    @PrePersist
    protected void onCreate() {
        dateMouvement = LocalDateTime.now();
    }
}
