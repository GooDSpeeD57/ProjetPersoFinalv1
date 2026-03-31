package fr.micromania.entity.referentiel;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "taux_tva")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TauxTva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_taux_tva")
    private Long id;

    @Column(name = "code", nullable = false, unique = true, length = 50)
    private String code;

    @Column(name = "description")
    private String description;

    @Column(name = "taux", nullable = false, precision = 5, scale = 2)
    private BigDecimal taux;

    @Column(name = "actif", nullable = false)
    private boolean actif = true;
}
