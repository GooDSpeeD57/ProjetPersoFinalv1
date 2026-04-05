package fr.micromania.dto.magasin;

import java.math.BigDecimal;

public record MagasinProximiteResponse(
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
