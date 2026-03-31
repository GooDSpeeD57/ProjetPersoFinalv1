package fr.micromania.entity.catalog;

import jakarta.persistence.Embeddable;
import lombok.*;
import java.io.Serializable;

@Embeddable
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @EqualsAndHashCode
public class FavoriProduitId implements Serializable {
    private Long idClient;
    private Long idProduit;
}
