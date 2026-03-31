package fr.micromania.entity;

import fr.micromania.entity.referentiel.StatutPlanning;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "planning_employe")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PlanningEmploye {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_planning")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_employe", nullable = false)
    private Employe employe;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_statut_planning", nullable = false)
    private StatutPlanning statutPlanning;

    @Column(name = "date_travail", nullable = false)
    private LocalDate dateTravail;

    @Column(name = "heure_debut", nullable = false)
    private LocalTime heureDebut;

    @Column(name = "heure_fin", nullable = false)
    private LocalTime heureFin;

    @Column(name = "note_interne", columnDefinition = "TEXT")
    private String noteInterne;
}
