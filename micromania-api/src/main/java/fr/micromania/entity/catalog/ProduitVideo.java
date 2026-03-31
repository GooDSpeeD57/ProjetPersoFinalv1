package fr.micromania.entity.catalog;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "produit_video")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ProduitVideo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_video")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_produit", nullable = false)
    private Produit produit;

    @Column(name = "url", nullable = false, length = 255)
    private String url;

    @Column(name = "titre", nullable = false, length = 255)
    private String titre;

    @Column(name = "ordre_affichage", nullable = false)
    private int ordreAffichage = 0;

    @Column(name = "langue", nullable = false, length = 10)
    private String langue = "fr";

    @Column(name = "sous_titres_url", length = 255)
    private String sousTitresUrl;

    @Column(name = "audio_desc_url", length = 255)
    private String audioDescUrl;

    @Column(name = "transcription", columnDefinition = "TEXT")
    private String transcription;

    @Column(name = "date_creation", nullable = false, updatable = false)
    private LocalDateTime dateCreation;

    @PrePersist
    protected void onCreate() {
        dateCreation = LocalDateTime.now();
    }
}
