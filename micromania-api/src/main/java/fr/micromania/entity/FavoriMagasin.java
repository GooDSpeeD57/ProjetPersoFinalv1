package fr.micromania.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "favori_magasin")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class FavoriMagasin {

    @EmbeddedId
    private FavoriMagasinId id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("idClient")
    @JoinColumn(name = "id_client")
    private Client client;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("idMagasin")
    @JoinColumn(name = "id_magasin")
    private Magasin magasin;

    @Column(name = "date_ajout", nullable = false, updatable = false)
    private LocalDateTime dateAjout;

    @Column(name = "principal", nullable = false)
    private boolean principal = false;

    @PrePersist
    protected void onCreate() {
        dateAjout = LocalDateTime.now();
    }
}
