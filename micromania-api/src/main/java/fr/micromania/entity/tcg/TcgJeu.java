package fr.micromania.entity.tcg;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tcg_jeu")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TcgJeu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tcg_jeu")
    private Long id;

    @Column(name = "code", nullable = false, unique = true, length = 50)
    private String code;

    @Column(name = "nom", nullable = false, unique = true, length = 100)
    private String nom;

    @Column(name = "editeur", length = 150)
    private String editeur;

    @Column(name = "actif", nullable = false)
    private boolean actif = true;
}
