package fr.micromania.entity.commande;

import fr.micromania.entity.Adresse;
import fr.micromania.entity.Client;
import fr.micromania.entity.Entrepot;
import fr.micromania.entity.Magasin;
import fr.micromania.entity.referentiel.*;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "commande")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Commande {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_commande")
    private Long id;

    @Column(name = "reference_commande", nullable = false, unique = true, length = 50)
    private String referenceCommande;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_client", nullable = false)
    private Client client;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_statut_commande", nullable = false)
    private StatutCommande statutCommande;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_canal_vente", nullable = false)
    private CanalVente canalVente;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_mode_livraison", nullable = false)
    private ModeLivraison modeLivraison;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_adresse_livraison")
    private Adresse adresseLivraison;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_magasin_retrait")
    private Magasin magasinRetrait;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_entrepot_expedition")
    private Entrepot entrepotExpedition;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_mode_paiement")
    private ModePaiement modePaiement;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_bon_achat")
    private BonAchat bonAchat;

    @Column(name = "code_promo", length = 50)
    private String codePromo;

    @Column(name = "sous_total", nullable = false, precision = 10, scale = 2)
    private BigDecimal sousTotal = BigDecimal.ZERO;

    @Column(name = "montant_remise", nullable = false, precision = 10, scale = 2)
    private BigDecimal montantRemise = BigDecimal.ZERO;

    @Column(name = "frais_livraison", nullable = false, precision = 10, scale = 2)
    private BigDecimal fraisLivraison = BigDecimal.ZERO;

    @Column(name = "montant_total", nullable = false, precision = 10, scale = 2)
    private BigDecimal montantTotal = BigDecimal.ZERO;

    @Column(name = "commentaire_client", columnDefinition = "TEXT")
    private String commentaireClient;

    @Version
    @Column(name = "version", nullable = false)
    private Long version;

    @Column(name = "date_commande", nullable = false, updatable = false)
    private LocalDateTime dateCommande;

    @Column(name = "date_paiement")
    private LocalDateTime datePaiement;

    @Column(name = "date_preparation")
    private LocalDateTime datePreparation;

    @Column(name = "date_expedition")
    private LocalDateTime dateExpedition;

    @Column(name = "date_livraison_prevue")
    private LocalDateTime dateLivraisonPrevue;

    @Column(name = "date_livraison_reelle")
    private LocalDateTime dateLivraisonReelle;

    @Column(name = "date_retrait")
    private LocalDateTime dateRetrait;

    @OneToMany(mappedBy = "commande", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<LigneCommande> lignes = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        dateCommande = LocalDateTime.now();
    }
}
