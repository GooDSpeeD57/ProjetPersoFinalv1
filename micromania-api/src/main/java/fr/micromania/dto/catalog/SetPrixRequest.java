package fr.micromania.dto.catalog;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record SetPrixRequest(
    @NotNull
    Long idVariant,

    @NotNull
    Long idCanalVente,

    @NotNull @DecimalMin("0.01")
    BigDecimal prix,

    @NotNull
    LocalDateTime dateDebut,

    LocalDateTime dateFin
) {}
