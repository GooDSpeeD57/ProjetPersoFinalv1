package fr.micromania.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "avatar")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Avatar {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_avatar")
    private Long id;

    @Column(name = "nom", nullable = false, length = 100)
    private String nom;

    @Column(name = "url", nullable = false, length = 255)
    private String url;

    @Column(name = "alt", nullable = false, length = 255)
    private String alt;

    @Column(name = "decorative", nullable = false)
    private boolean decorative = false;

    @Column(name = "actif", nullable = false)
    private boolean actif = true;
}