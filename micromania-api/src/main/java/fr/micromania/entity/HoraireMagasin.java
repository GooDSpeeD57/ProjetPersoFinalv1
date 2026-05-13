package fr.micromania.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalTime;

/**
 * Horaire d'ouverture d'un magasin pour un jour de la semaine donné.
 * jourSemaine suit la norme ISO-8601 : 1 = Lundi … 7 = Dimanche.
 */
@Entity
@Table(
    name = "horaire_magasin",
    uniqueConstraints = @UniqueConstraint(
        name = "uq_horaire_magasin_jour",
        columnNames = {"id_magasin", "jour_semaine"}
    )
)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class HoraireMagasin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /** Magasin concerné */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_magasin", nullable = false)
    private Magasin magasin;

    /** Jour ISO : 1 = Lundi, 7 = Dimanche */
    @Column(name = "jour_semaine", nullable = false)
    private int jourSemaine;

    /** Heure d'ouverture (null si ferme = true) */
    @Column(name = "heure_ouverture")
    private LocalTime heureOuverture;

    /** Heure de fermeture (null si ferme = true) */
    @Column(name = "heure_fermeture")
    private LocalTime heureFermeture;

    /** true = magasin fermé ce jour-là */
    @Column(name = "ferme", nullable = false)
    private boolean ferme = false;
}
