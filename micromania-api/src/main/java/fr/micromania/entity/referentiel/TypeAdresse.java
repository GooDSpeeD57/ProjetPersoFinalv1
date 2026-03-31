package fr.micromania.entity.referentiel;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "type_adresse")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TypeAdresse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_type_adresse")
    private Long id;

    @Column(name = "code", nullable = false, unique = true, length = 50)
    private String code;
}
