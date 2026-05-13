package com.monprojet.boutiquejeux.dto.api.referentiel;

import java.math.BigDecimal;

public record ApiTypeGarantie(
        Long id,
        String code,
        String description,
        Integer dureeMois,
        BigDecimal prixExtension,
        Long categorieId
) {}
