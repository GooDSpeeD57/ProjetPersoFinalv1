package fr.micromania.entity.tcg;

import fr.micromania.entity.catalog.Produit;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tcg_carte_reference",
    uniqueConstraints = @UniqueConstraint(columnNames = {"id_tcg_extension", "nom_carte", "numero_carte"}))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TcgCarteReference {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tcg_carte_reference")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_tcg_extension", nullable = false)
    private TcgExtension tcgExtension;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_produit")
    private Produit produit;

    @Column(name = "nom_carte", nullable = false, length = 255)
    private String nomCarte;

    @Column(name = "numero_carte", length = 50)
    private String numeroCarte;

    @Column(name = "rarete", length = 50)
    private String rarete;

    @Column(name = "type_carte", length = 100)
    private String typeCarte;

    @Column(name = "sous_type", length = 100)
    private String sousType;

    @Column(name = "element_couleur", length = 100)
    private String elementCouleur;

    @Column(name = "pv_attaque", length = 50)
    private String pvAttaque;

    @Column(name = "artiste", length = 150)
    private String artiste;

    @Column(name = "illustration_url", length = 255)
    private String illustrationUrl;
}
