package fr.micromania.entity;

import fr.micromania.entity.referentiel.ModeCompensationReprise;
import fr.micromania.entity.referentiel.StatutReprise;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "reprise")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Reprise {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_reprise")
    private Long id;

    @Column(name = "reference_reprise", nullable = false, unique = true, length = 50)
    private String referenceReprise;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_client")
    private Client client;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_employe", nullable = false)
    private Employe employe;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_magasin", nullable = false)
    private Magasin magasin;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_statut_reprise", nullable = false)
    private StatutReprise statutReprise;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_mode_compensation_reprise", nullable = false)
    private ModeCompensationReprise modeCompensationReprise;

    @Column(name = "montant_total_estime", nullable = false, precision = 10, scale = 2)
    private BigDecimal montantTotalEstime = BigDecimal.ZERO;

    @Column(name = "montant_total_valide", nullable = false, precision = 10, scale = 2)
    private BigDecimal montantTotalValide = BigDecimal.ZERO;

    @Column(name = "commentaire", columnDefinition = "TEXT")
    private String commentaire;

    @Column(name = "date_creation", nullable = false, updatable = false)
    private LocalDateTime dateCreation;

    @Column(name = "date_validation")
    private LocalDateTime dateValidation;

    @OneToMany(mappedBy = "reprise", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<RepriseeLigne> lignes = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        dateCreation = LocalDateTime.now();
    }
}
