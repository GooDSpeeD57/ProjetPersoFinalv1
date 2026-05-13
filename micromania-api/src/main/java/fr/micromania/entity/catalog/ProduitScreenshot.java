package fr.micromania.entity.catalog;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "produit_screenshot")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ProduitScreenshot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_screenshot")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_produit", nullable = false)
    private Produit produit;

    @Column(name = "url", nullable = false, length = 255)
    private String url;

    @Column(name = "alt", nullable = false, length = 255)
    private String alt;

    @Column(name = "ordre_affichage", nullable = false)
    private int ordreAffichage = 0;

    @Column(name = "date_creation", nullable = false, updatable = false)
    private LocalDateTime dateCreation;

    @PrePersist
    protected void onCreate() {
        dateCreation = LocalDateTime.now();
    }
}
