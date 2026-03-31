package fr.micromania.entity.referentiel;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "statut_precommande")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class StatutPrecommande {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_statut_precommande")
    private Long id;

    @Column(name = "code", nullable = false, unique = true, length = 50)
    private String code;
}
