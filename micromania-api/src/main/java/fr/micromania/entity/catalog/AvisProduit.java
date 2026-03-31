package fr.micromania.entity.catalog;

import fr.micromania.entity.Client;
import fr.micromania.entity.Employe;
import fr.micromania.entity.referentiel.StatutAvis;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "avis_produit",
    uniqueConstraints = @UniqueConstraint(columnNames = {"id_client", "id_produit"}))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AvisProduit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_avis")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_client", nullable = false)
    private Client client;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_produit", nullable = false)
    private Produit produit;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_statut_avis", nullable = false)
    private StatutAvis statutAvis;

    @Column(name = "note", nullable = false)
    private byte note;

    @Column(name = "commentaire", columnDefinition = "TEXT")
    private String commentaire;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_employe_moderateur")
    private Employe employeModerateur;

    @Column(name = "motif_moderation", length = 255)
    private String motifModeration;

    @Column(name = "date_creation", nullable = false, updatable = false)
    private LocalDateTime dateCreation;

    @Column(name = "date_modification", nullable = false)
    private LocalDateTime dateModification;

    @Column(name = "date_moderation")
    private LocalDateTime dateModeration;

    @PrePersist
    protected void onCreate() {
        dateCreation = LocalDateTime.now();
        dateModification = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        dateModification = LocalDateTime.now();
    }
}
