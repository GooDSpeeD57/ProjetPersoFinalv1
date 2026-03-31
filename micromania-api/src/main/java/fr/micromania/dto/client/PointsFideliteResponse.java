package fr.micromania.dto.client;

import java.math.BigDecimal;
import java.time.LocalDate;

public record PointsFideliteResponse(
    int soldePoints,
    BigDecimal totalAchatsAnnuel,
    LocalDate dateDebutPeriode,
    String typeFidelite
) {}
