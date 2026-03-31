package fr.micromania.entity;

import fr.micromania.entity.commande.LigneFacture;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "vente_unite")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class VenteUnite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_vente_unite")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_ligne_facture", nullable = false)
    private LigneFacture ligneFacture;

    @Column(name = "numero_serie", unique = true, length = 100)
    private String numeroSerie;

    @Column(name = "etat_unite", length = 50)
    private String etatUnite;

    @Column(name = "source_stock", nullable = false, length = 20)
    private String sourceStock = "NEUF";

    @Column(name = "date_creation", nullable = false, updatable = false)
    private LocalDateTime dateCreation;

    @PrePersist
    protected void onCreate() {
        dateCreation = LocalDateTime.now();
    }
}
