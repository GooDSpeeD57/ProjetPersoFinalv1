package fr.micromania.dto.catalog;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PrixResponse(
    Long id,
    Long idVariant,
    BigDecimal prixNeuf,
    BigDecimal prixOccasion,
    BigDecimal prixReprise,
    BigDecimal prixLocation,
    LocalDateTime dateDebut,
    LocalDateTime dateFin,
    boolean actif
) {}
