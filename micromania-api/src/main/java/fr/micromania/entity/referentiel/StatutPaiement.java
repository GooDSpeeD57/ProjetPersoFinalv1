package fr.micromania.entity.referentiel;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "statut_paiement")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class StatutPaiement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_statut_paiement")
    private Long id;

    @Column(name = "code", nullable = false, unique = true, length = 50)
    private String code;
}
