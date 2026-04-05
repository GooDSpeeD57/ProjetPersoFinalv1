package com.monprojet.boutiquejeux.dto.api.magasin;

import java.math.BigDecimal;

public record ApiMagasinProche(
        Long id,
        String nom,
        String telephone,
        String email,
        String rue,
        String complement,
        String ville,
        String codePostal,
        String pays,
        BigDecimal latitude,
        BigDecimal longitude,
        Double distanceKm,
        String libelleDistance
) {}
