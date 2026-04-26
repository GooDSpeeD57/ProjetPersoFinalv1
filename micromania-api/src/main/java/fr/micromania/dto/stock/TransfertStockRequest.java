package fr.micromania.dto.stock;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record TransfertStockRequest(
        @NotNull Long   idVariant,
        @NotNull Long   idEntrepotSource,
        @NotNull Long   idMagasinDestination,
        @NotNull @Min(1) int quantite,
        String          commentaire
) {}
