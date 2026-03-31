package fr.micromania.entity.commande;

import fr.micromania.entity.Client;
import fr.micromania.entity.referentiel.CanalVente;
import fr.micromania.entity.referentiel.StatutPanier;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "panier")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Panier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_panier")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_client", nullable = false)
    private Client client;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_statut_panier", nullable = false)
    private StatutPanier statutPanier;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_canal_vente", nullable = false)
    private CanalVente canalVente;

    @Column(name = "code_promo", length = 50)
    private String codePromo;

    @Column(name = "date_creation", nullable = false, updatable = false)
    private LocalDateTime dateCreation;

    @Column(name = "date_derniere_activite", nullable = false)
    private LocalDateTime dateDerniereActivite;

    @OneToMany(mappedBy = "panier", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<LignePanier> lignes = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        dateCreation = LocalDateTime.now();
        dateDerniereActivite = LocalDateTime.now();
    }
}
