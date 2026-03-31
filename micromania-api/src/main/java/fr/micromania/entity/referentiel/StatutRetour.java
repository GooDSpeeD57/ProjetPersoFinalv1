package fr.micromania.entity.referentiel;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "statut_retour")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class StatutRetour {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_statut_retour")
    private Long id;

    @Column(name = "code", nullable = false, unique = true, length = 50)
    private String code;
}
