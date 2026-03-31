package fr.micromania.entity;

import jakarta.persistence.Embeddable;
import lombok.*;
import java.io.Serializable;

@Embeddable
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @EqualsAndHashCode
public class FavoriMagasinId implements Serializable {
    private Long idClient;
    private Long idMagasin;
}
