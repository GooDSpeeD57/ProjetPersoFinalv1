package fr.micromania.entity.catalog;

import fr.micromania.entity.referentiel.*;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "produit_variant")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ProduitVariant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_variant")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_produit", nullable = false)
    private Produit produit;

    @Column(name = "sku", nullable = false, unique = true, length = 100)
    private String sku;

    @Column(name = "ean", unique = true, length = 50)
    private String ean;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_plateforme")
    private Plateforme plateforme;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_format_produit", nullable = false)
    private FormatProduit formatProduit;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_statut_produit", nullable = false)
    private StatutProduit statutProduit;

    @OneToMany(mappedBy = "variant", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ProduitPrix> prix = new ArrayList<>();

    @Column(name = "nom_commercial", nullable = false, length = 255)
    private String nomCommercial;

    @Column(name = "reference_fournisseur", length = 100)
    private String referenceFournisseur;

    @Column(name = "edition", length = 100)
    private String edition;

    @Column(name = "couleur", length = 100)
    private String couleur;

    @Column(name = "taille", length = 50)
    private String taille;

    @Column(name = "capacite_stockage", length = 100)
    private String capaciteStockage;

    @Column(name = "langue_vente", nullable = false, length = 10)
    private String langueVente = "fr";

    @Column(name = "scelle", nullable = false)
    private boolean scelle = false;

    @Column(name = "est_demat", nullable = false)
    private boolean estDemat = false;

    @Column(name = "est_tcg_unitaire", nullable = false)
    private boolean estTcgUnitaire = false;

    @Column(name = "est_reprise", nullable = false)
    private boolean estReprise = false;

    @Column(name = "necessite_numero_serie", nullable = false)
    private boolean necessiteNumeroSerie = false;

    @Column(name = "poids_grammes")
    private Integer poidsGrammes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_taux_tva")
    private TauxTva tauxTva;

    @Column(name = "actif", nullable = false)
    private boolean actif = true;

    @Version
    @Column(name = "version", nullable = false)
    private Long version;

    @Column(name = "date_creation", nullable = false, updatable = false)
    private LocalDateTime dateCreation;

    @Column(name = "date_modification", nullable = false)
    private LocalDateTime dateModification;

    @PrePersist
    protected void onCreate() {
        dateCreation = LocalDateTime.now();
        dateModification = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        dateModification = LocalDateTime.now();
    }
}
