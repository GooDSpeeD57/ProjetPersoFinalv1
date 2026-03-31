package fr.micromania.dto.panier;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record UpdateLignePanierRequest(
    @NotNull @Min(1)
    Integer quantite
) {}
