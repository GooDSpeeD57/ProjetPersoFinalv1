package fr.micromania.entity.catalog;

import fr.micromania.entity.Client;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "favori_produit")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class FavoriProduit {

    @EmbeddedId
    private FavoriProduitId id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("idClient")
    @JoinColumn(name = "id_client")
    private Client client;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("idProduit")
    @JoinColumn(name = "id_produit")
    private Produit produit;

    @Column(name = "date_ajout", nullable = false, updatable = false)
    private LocalDateTime dateAjout;

    @PrePersist
    protected void onCreate() {
        dateAjout = LocalDateTime.now();
    }
}
