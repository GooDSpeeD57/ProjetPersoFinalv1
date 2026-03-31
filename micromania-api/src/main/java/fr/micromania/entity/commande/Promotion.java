package fr.micromania.entity.commande;

import fr.micromania.entity.catalog.Categorie;
import fr.micromania.entity.catalog.ProduitVariant;
import fr.micromania.entity.referentiel.TypeReduction;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "promotion")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Promotion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_promotion")
    private Long id;

    @Column(name = "code_promo", nullable = false, unique = true, length = 50)
    private String codePromo;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_type_reduction", nullable = false)
    private TypeReduction typeReduction;

    @Column(name = "valeur", nullable = false, precision = 10, scale = 2)
    private BigDecimal valeur;

    @Column(name = "date_debut", nullable = false)
    private LocalDateTime dateDebut;

    @Column(name = "date_fin", nullable = false)
    private LocalDateTime dateFin;

    @Column(name = "montant_minimum_commande", precision = 10, scale = 2)
    private BigDecimal montantMinimumCommande;

    @Column(name = "nb_utilisations_max")
    private Integer nbUtilisationsMax;

    @Column(name = "nb_utilisations_max_client")
    private Integer nbUtilisationsMaxClient;

    @Column(name = "nb_utilisations_actuel", nullable = false)
    private int nbUtilisationsActuel = 0;

    @Column(name = "cumulable", nullable = false)
    private boolean cumulable = false;

    @Column(name = "actif", nullable = false)
    private boolean actif = true;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "promotion_variant",
        joinColumns = @JoinColumn(name = "id_promotion"),
        inverseJoinColumns = @JoinColumn(name = "id_variant")
    )
    @Builder.Default
    private Set<ProduitVariant> variants = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "promotion_categorie",
        joinColumns = @JoinColumn(name = "id_promotion"),
        inverseJoinColumns = @JoinColumn(name = "id_categorie")
    )
    @Builder.Default
    private Set<Categorie> categories = new HashSet<>();
}
