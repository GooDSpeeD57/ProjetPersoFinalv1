package fr.micromania.entity.catalog;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "produit")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Produit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_produit")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_categorie", nullable = false)
    private Categorie categorie;

    @Column(name = "nom", nullable = false, length = 255)
    private String nom;

    @Column(name = "slug", nullable = false, unique = true, length = 255)
    private String slug;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "resume_court", length = 500)
    private String resumeCourt;

    @Column(name = "date_sortie")
    private LocalDate dateSortie;

    @Column(name = "editeur", length = 150)
    private String editeur;

    @Column(name = "constructeur", length = 150)
    private String constructeur;

    @Column(name = "pegi")
    private Integer pegi;

    @Column(name = "marque", length = 150)
    private String marque;

    @Column(name = "actif", nullable = false)
    private boolean actif = true;

    @Column(name = "deleted", nullable = false)
    private boolean deleted = false;

    @Column(name = "niveau_acces_min", nullable = false, length = 20)
    private String niveauAccesMin = "NORMAL";

    @Column(name = "langue", nullable = false, length = 10)
    private String langue = "fr";

    @Column(name = "mis_en_avant", nullable = false)
    private boolean misEnAvant = false;

    @Column(name = "date_creation", nullable = false, updatable = false)
    private LocalDateTime dateCreation;

    @Column(name = "date_modification", nullable = false)
    private LocalDateTime dateModification;

    @OneToMany(mappedBy = "produit", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ProduitVariant> variants = new ArrayList<>();

    @OneToMany(mappedBy = "produit", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ProduitImage> images = new ArrayList<>();

    @OneToMany(mappedBy = "produit", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ProduitVideo> videos = new ArrayList<>();

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
