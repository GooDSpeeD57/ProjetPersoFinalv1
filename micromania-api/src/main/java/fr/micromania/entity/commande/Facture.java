package fr.micromania.entity.commande;

import fr.micromania.entity.Client;
import fr.micromania.entity.Employe;
import fr.micromania.entity.Magasin;
import fr.micromania.entity.referentiel.*;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "facture")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Facture {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_facture")
    private Long id;

    @Column(name = "reference_facture", nullable = false, unique = true, length = 50)
    private String referenceFacture;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_commande")
    private Commande commande;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_client")
    private Client client;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_magasin", nullable = false)
    private Magasin magasin;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_employe")
    private Employe employe;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_mode_paiement", nullable = false)
    private ModePaiement modePaiement;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_statut_facture", nullable = false)
    private StatutFacture statutFacture;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_bon_achat")
    private BonAchat bonAchat;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_contexte_vente", nullable = false)
    private ContexteVente contexteVente;

    @Column(name = "date_facture", nullable = false, updatable = false)
    private LocalDateTime dateFacture;

    @Column(name = "montant_total", nullable = false, precision = 10, scale = 2)
    private BigDecimal montantTotal = BigDecimal.ZERO;

    @Column(name = "montant_ht_total", nullable = false, precision = 10, scale = 2)
    private BigDecimal montantHtTotal = BigDecimal.ZERO;

    @Column(name = "montant_tva_total", nullable = false, precision = 10, scale = 2)
    private BigDecimal montantTvaTotal = BigDecimal.ZERO;

    @Column(name = "montant_remise", nullable = false, precision = 10, scale = 2)
    private BigDecimal montantRemise = BigDecimal.ZERO;

    @Column(name = "montant_final", nullable = false, precision = 10, scale = 2)
    private BigDecimal montantFinal = BigDecimal.ZERO;

    @Column(name = "nom_client", length = 100)
    private String nomClient;

    @Column(name = "email_client", length = 150)
    private String emailClient;

    @Column(name = "telephone_client", length = 20)
    private String telephoneClient;

    @Version
    @Column(name = "version", nullable = false)
    private Long version;

    @OneToMany(mappedBy = "facture", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<LigneFacture> lignes = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        dateFacture = LocalDateTime.now();
    }
}
