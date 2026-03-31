package fr.micromania.dto.reprise;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import java.math.BigDecimal;

@Builder
public record ValiderRepriseLigneRequest(
    @NotNull
    Long idRepriseLigne,

    @NotNull @DecimalMin("0.00")
    BigDecimal prixValideUnitaire,

    boolean creeStockOccasion
) {}
