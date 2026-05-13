package fr.micromania.entity.referentiel;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "edition_produit")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class EditionProduit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_edition")
    private Long id;

    @Column(name = "code", nullable = false, unique = true, length = 50)
    private String code;

    @Column(name = "libelle", nullable = false, length = 100)
    private String libelle;

    @Column(name = "ordre_affichage", nullable = false)
    private int ordreAffichage = 0;

    @Column(name = "actif", nullable = false)
    private boolean actif = true;
}
