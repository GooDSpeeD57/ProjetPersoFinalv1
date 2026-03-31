package fr.micromania.dto.precommande;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import java.math.BigDecimal;

@Builder
public record PrecommandeLigneRequest(
    @NotNull
    Long idVariant,

    @NotNull @Min(1)
    Integer quantite,

    @NotNull @DecimalMin("0.01")
    BigDecimal prixUnitaireEstime
) {}
