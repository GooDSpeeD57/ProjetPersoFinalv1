package fr.micromania.entity;

import fr.micromania.entity.commande.Facture;
import fr.micromania.entity.referentiel.StatutRetour;
import fr.micromania.entity.referentiel.TypeRetour;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "retour_produit")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class RetourProduit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_retour")
    private Long id;

    @Column(name = "reference_retour", nullable = false, unique = true, length = 50)
    private String referenceRetour;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_facture", nullable = false)
    private Facture facture;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_client")
    private Client client;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_statut_retour", nullable = false)
    private StatutRetour statutRetour;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_type_retour", nullable = false)
    private TypeRetour typeRetour;

    @Column(name = "motif_retour", length = 255)
    private String motifRetour;

    @Column(name = "date_demande", nullable = false, updatable = false)
    private LocalDateTime dateDemande;

    @Column(name = "date_traitement")
    private LocalDateTime dateTraitement;

    @Column(name = "montant_rembourse", nullable = false, precision = 10, scale = 2)
    private BigDecimal montantRembourse = BigDecimal.ZERO;

    @OneToMany(mappedBy = "retourProduit", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<RetourLigne> lignes = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        dateDemande = LocalDateTime.now();
    }
}
