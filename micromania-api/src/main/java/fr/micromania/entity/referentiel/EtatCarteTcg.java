package fr.micromania.entity.referentiel;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "etat_carte_tcg")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class EtatCarteTcg {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_etat_carte_tcg")
    private Long id;

    @Column(name = "code", nullable = false, unique = true, length = 50)
    private String code;

    @Column(name = "libelle", nullable = false, length = 100)
    private String libelle;

    @Column(name = "coefficient_prix", nullable = false, precision = 5, scale = 2)
    private BigDecimal coefficientPrix;
}
