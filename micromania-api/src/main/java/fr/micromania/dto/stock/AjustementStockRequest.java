package fr.micromania.dto.stock;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record AjustementStockRequest(
    @NotNull
    Long idVariant,

    Long idMagasin,
    Long idEntrepot,

    @NotBlank
    String sourceStock,

    @NotNull
    Integer delta,

    String commentaire
) {}
