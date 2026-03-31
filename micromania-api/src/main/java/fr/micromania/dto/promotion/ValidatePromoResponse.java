package fr.micromania.dto.promotion;

import java.math.BigDecimal;

public record ValidatePromoResponse(
    boolean valide,
    String message,
    BigDecimal montantRemise
) {}
