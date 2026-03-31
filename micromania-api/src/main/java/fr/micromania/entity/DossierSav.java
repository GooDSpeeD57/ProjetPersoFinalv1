package fr.micromania.entity;

import fr.micromania.entity.referentiel.StatutSav;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "dossier_sav")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class DossierSav {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_dossier_sav")
    private Long id;

    @Column(name = "reference_sav", nullable = false, unique = true, length = 50)
    private String referenceSav;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_vente_unite", nullable = false)
    private VenteUnite venteUnite;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_garantie")
    private Garantie garantie;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_statut_sav", nullable = false)
    private StatutSav statutSav;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_employe")
    private Employe employe;

    @Column(name = "panne_declaree", columnDefinition = "TEXT")
    private String panneDeclaree;

    @Column(name = "diagnostic", columnDefinition = "TEXT")
    private String diagnostic;

    @Column(name = "solution_apportee", columnDefinition = "TEXT")
    private String solutionApportee;

    @Column(name = "date_ouverture", nullable = false, updatable = false)
    private LocalDateTime dateOuverture;

    @Column(name = "date_cloture")
    private LocalDateTime dateCloture;

    @PrePersist
    protected void onCreate() {
        dateOuverture = LocalDateTime.now();
    }
}
