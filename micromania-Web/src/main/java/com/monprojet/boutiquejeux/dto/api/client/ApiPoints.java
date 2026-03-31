package com.monprojet.boutiquejeux.dto.api.client;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ApiPoints(
        int soldePoints,
        BigDecimal totalAchatsAnnuel,
        LocalDate dateDebutPeriode,
        String typeFidelite
) {}
