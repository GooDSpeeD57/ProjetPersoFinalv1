package fr.micromania.entity.referentiel;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "type_retour")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TypeRetour {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_type_retour")
    private Long id;

    @Column(name = "code", nullable = false, unique = true, length = 50)
    private String code;
}
