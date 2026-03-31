package fr.micromania.entity.referentiel;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "type_mouvement")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TypeMouvement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_type_mouvement")
    private Long id;

    @Column(name = "code", nullable = false, unique = true, length = 50)
    private String code;
}
