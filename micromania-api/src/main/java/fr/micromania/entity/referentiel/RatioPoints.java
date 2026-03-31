package fr.micromania.entity.referentiel;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "ratio_points",
    uniqueConstraints = @UniqueConstraint(columnNames = {"id_type_categorie", "id_type_fidelite"}))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class RatioPoints {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_ratio")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_type_categorie", nullable = false)
    private TypeCategorie typeCategorie;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_type_fidelite", nullable = false)
    private TypeFidelite typeFidelite;

    @Column(name = "ratio", nullable = false, precision = 5, scale = 2)
    private BigDecimal ratio;
}
