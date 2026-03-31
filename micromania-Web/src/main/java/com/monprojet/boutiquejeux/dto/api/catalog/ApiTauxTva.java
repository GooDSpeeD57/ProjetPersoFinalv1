package com.monprojet.boutiquejeux.dto.api.catalog;

import java.math.BigDecimal;

public record ApiTauxTva(
        Long id,
        String code,
        BigDecimal taux
) {}
