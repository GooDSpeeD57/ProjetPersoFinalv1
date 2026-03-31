package fr.micromania.dto.facture;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import java.math.BigDecimal;

@Builder
public record LigneFactureRequest(
    @NotNull
    Long idVariant,

    @NotNull @Min(1)
    Integer quantite,

    @NotNull @DecimalMin("0.00")
    BigDecimal prixUnitaire
) {}
