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

    @DecimalMin("0.01")
    BigDecimal prixNeuf,

    @DecimalMin("0.01")
    BigDecimal prixOccasion,

    @DecimalMin("0.01")
    BigDecimal prixReprise,

    @DecimalMin("0.01")
    BigDecimal prixLocation,

    @NotNull
    LocalDateTime dateDebut,

    LocalDateTime dateFin
) {}
