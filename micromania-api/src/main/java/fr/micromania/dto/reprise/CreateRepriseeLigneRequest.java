package fr.micromania.dto.reprise;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import java.math.BigDecimal;

@Builder
public record CreateRepriseeLigneRequest(
    Long idVariant,
    Long idTcgCarteReference,
    String descriptionLibre,

    @NotNull @Min(1)
    Integer quantite,

    @Size(max = 50)
    String etatGeneral,

    @NotNull @DecimalMin("0.00")
    BigDecimal prixEstimeUnitaire,

    String numeroSerie,
    String commentaires
) {
    @SuppressWarnings("unused")
    private @interface Size { int max(); }
}
