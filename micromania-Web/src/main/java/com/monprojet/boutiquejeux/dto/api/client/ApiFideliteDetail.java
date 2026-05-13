package com.monprojet.boutiquejeux.dto.api.client;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record ApiFideliteDetail(
    int soldePoints,
    BigDecimal totalAchatsAnnuel,
    LocalDate dateDebutPeriode,
    String typeFidelite,
    BigDecimal pointsParEuroBase,
    List<RatioDetail> ratiosParCategorie,
    int seuilBon10,
    int pointsCycleBon10,
    int pointsAvantBon10,
    int progressionBon10Percent,
    int seuilBon20,
    int pointsCycleBon20,
    int pointsAvantBon20,
    int progressionBon20Percent,
    int totalPointsGagnes
) {
    public record RatioDetail(String categorieCode, String categorieDescription, BigDecimal ratio) {}
}
