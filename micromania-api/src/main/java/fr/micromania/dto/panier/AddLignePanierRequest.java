package fr.micromania.dto.panier;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record AddLignePanierRequest(
    @NotNull
    Long idVariant,

    @NotNull @Min(1)
    Integer quantite,

    @NotNull
    Long idCanalVente
) {}
