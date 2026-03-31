package fr.micromania.entity.referentiel;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "type_fidelite")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TypeFidelite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_type_fidelite")
    private Long id;

    @Column(name = "code", nullable = false, unique = true, length = 50)
    private String code;

    @Column(name = "description")
    private String description;

    @Column(name = "points_par_euro", nullable = false, precision = 5, scale = 2)
    private BigDecimal pointsParEuro;

    @Column(name = "seuil_upgrade_euro", precision = 10, scale = 2)
    private BigDecimal seuilUpgradeEuro;

    @Column(name = "prix_abonnement", precision = 10, scale = 2)
    private BigDecimal prixAbonnement;
}
