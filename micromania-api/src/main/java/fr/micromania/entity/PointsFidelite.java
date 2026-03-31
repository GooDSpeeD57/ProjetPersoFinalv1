package fr.micromania.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "points_fidelite")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PointsFidelite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_points_fidelite")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_client", nullable = false, unique = true)
    private Client client;

    @Column(name = "solde_points", nullable = false)
    private int soldePoints = 0;

    @Column(name = "total_achats_annuel", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAchatsAnnuel;

    @Column(name = "date_debut_periode", nullable = false)
    private LocalDate dateDebutPeriode;

    @Column(name = "date_modification", nullable = false)
    private LocalDateTime dateModification;

    @PrePersist @PreUpdate
    protected void onUpdate() {
        dateModification = LocalDateTime.now();
    }
}
