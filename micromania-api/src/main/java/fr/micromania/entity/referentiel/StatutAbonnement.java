package fr.micromania.entity.referentiel;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "statut_abonnement")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class StatutAbonnement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_statut_abonnement")
    private Long id;

    @Column(name = "code", nullable = false, unique = true, length = 50)
    private String code;
}
