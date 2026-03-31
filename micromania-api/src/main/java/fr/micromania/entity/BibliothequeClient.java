package fr.micromania.entity;

import fr.micromania.entity.catalog.ProduitVariant;
import fr.micromania.entity.commande.Facture;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "bibliotheque_client",
    uniqueConstraints = @UniqueConstraint(columnNames = {"id_client", "id_variant", "id_facture"}))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class BibliothequeClient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_bibliotheque")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_client", nullable = false)
    private Client client;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_variant", nullable = false)
    private ProduitVariant variant;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_facture", nullable = false)
    private Facture facture;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cle_produit")
    private CleProduit cleProduit;

    @Column(name = "date_attribution", nullable = false, updatable = false)
    private LocalDateTime dateAttribution;

    @PrePersist
    protected void onCreate() {
        dateAttribution = LocalDateTime.now();
    }
}
