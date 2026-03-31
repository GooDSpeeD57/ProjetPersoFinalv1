package fr.micromania.entity;

import fr.micromania.entity.catalog.ProduitVariant;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "cle_produit")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CleProduit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cle_produit")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_variant", nullable = false)
    private ProduitVariant variant;

    @Column(name = "cle_activation", nullable = false, unique = true, length = 255)
    private String cleActivation;

    @Column(name = "utilisee", nullable = false)
    private boolean utilisee = false;

    @Column(name = "date_creation", nullable = false, updatable = false)
    private LocalDateTime dateCreation;

    @Column(name = "date_utilisation")
    private LocalDateTime dateUtilisation;

    @PrePersist
    protected void onCreate() {
        dateCreation = LocalDateTime.now();
    }
}
