package fr.micromania.entity.referentiel;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "canal_vente")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CanalVente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_canal_vente")
    private Long id;

    @Column(name = "code", nullable = false, unique = true, length = 50)
    private String code;
}
