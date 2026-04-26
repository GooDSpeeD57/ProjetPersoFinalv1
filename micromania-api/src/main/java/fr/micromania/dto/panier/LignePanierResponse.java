package fr.micromania.dto.panier;

import java.math.BigDecimal;

public record LignePanierResponse(
    Long id,
    Long idVariant,
    String nomCommercial,
    String sku,
    String imageUrl,
    int quantite,
    BigDecimal prixUnitaire,
    BigDecimal montantLigne,
    Long typeGarantieId,
    String garantieLabel,
    BigDecimal garantiePrix
) {}
