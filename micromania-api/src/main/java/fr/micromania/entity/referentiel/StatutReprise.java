package fr.micromania.entity.referentiel;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "statut_reprise")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class StatutReprise {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_statut_reprise")
    private Long id;

    @Column(name = "code", nullable = false, unique = true, length = 50)
    private String code;
}
