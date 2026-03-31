package com.monprojet.boutiquejeux.dto.api.facture;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ApiFactureSummary(
        Long id,
        String referenceFacture,
        String statutFacture,
        String magasin,
        BigDecimal montantFinal,
        LocalDateTime dateFacture
) {}
