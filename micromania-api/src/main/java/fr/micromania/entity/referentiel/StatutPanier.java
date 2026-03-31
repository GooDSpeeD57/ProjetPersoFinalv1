package fr.micromania.entity.referentiel;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "statut_panier")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class StatutPanier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_statut_panier")
    private Long id;

    @Column(name = "code", nullable = false, unique = true, length = 50)
    private String code;
}
