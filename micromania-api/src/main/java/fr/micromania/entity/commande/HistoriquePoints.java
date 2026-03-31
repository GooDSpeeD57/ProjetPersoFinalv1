package fr.micromania.entity.commande;

import fr.micromania.entity.Client;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "historique_points")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class HistoriquePoints {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_historique")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_client", nullable = false)
    private Client client;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_facture")
    private Facture facture;

    @Column(name = "type_operation", nullable = false, length = 30)
    private String typeOperation;

    @Column(name = "points", nullable = false)
    private int points;

    @Column(name = "commentaire", length = 255)
    private String commentaire;

    @Column(name = "date_operation", nullable = false, updatable = false)
    private LocalDateTime dateOperation;

    @PrePersist
    protected void onCreate() {
        dateOperation = LocalDateTime.now();
    }
}
