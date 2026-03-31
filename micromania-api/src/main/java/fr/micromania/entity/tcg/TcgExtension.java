package fr.micromania.entity.tcg;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "tcg_extension")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TcgExtension {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tcg_extension")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_tcg_jeu", nullable = false)
    private TcgJeu tcgJeu;

    @Column(name = "code", nullable = false, unique = true, length = 50)
    private String code;

    @Column(name = "nom", nullable = false, length = 150)
    private String nom;

    @Column(name = "date_sortie")
    private LocalDate dateSortie;

    @Column(name = "actif", nullable = false)
    private boolean actif = true;
}
