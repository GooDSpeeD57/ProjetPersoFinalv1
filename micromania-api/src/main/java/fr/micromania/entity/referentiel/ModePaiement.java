package fr.micromania.entity.referentiel;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "mode_paiement")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ModePaiement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_mode_paiement")
    private Long id;

    @Column(name = "code", nullable = false, unique = true, length = 50)
    private String code;
}
