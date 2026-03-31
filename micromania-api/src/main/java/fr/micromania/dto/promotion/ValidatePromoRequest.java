package fr.micromania.dto.promotion;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import java.math.BigDecimal;

@Builder
public record ValidatePromoRequest(
    @NotBlank
    String codePromo,

    Long idClient,

    @NotNull @DecimalMin("0.00")
    BigDecimal montantCommande
) {}
