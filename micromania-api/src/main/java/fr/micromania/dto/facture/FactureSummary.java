package fr.micromania.dto.facture;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record FactureSummary(
    Long id,
    String referenceFacture,
    String statutFacture,
    String magasin,
    BigDecimal montantFinal,
    LocalDateTime dateFacture
) {}
