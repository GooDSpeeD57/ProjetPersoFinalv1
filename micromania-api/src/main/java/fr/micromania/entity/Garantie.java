package fr.micromania.entity;

import fr.micromania.entity.referentiel.TypeGarantie;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "garantie")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Garantie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_garantie")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_vente_unite", nullable = false, unique = true)
    private VenteUnite venteUnite;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_type_garantie", nullable = false)
    private TypeGarantie typeGarantie;

    @Column(name = "date_debut", nullable = false)
    private LocalDate dateDebut;

    @Column(name = "date_fin", nullable = false)
    private LocalDate dateFin;

    @Column(name = "est_etendue", nullable = false)
    private boolean estEtendue = false;

    @Column(name = "date_extension")
    private LocalDate dateExtension;
}
