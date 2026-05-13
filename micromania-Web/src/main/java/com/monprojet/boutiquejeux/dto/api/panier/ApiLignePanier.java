package com.monprojet.boutiquejeux.dto.api.panier;

import java.math.BigDecimal;

public record ApiLignePanier(
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
        BigDecimal garantiePrix,
        Long categorieId,
        String statutProduit
) {
    /** Prix garantie × quantité (0 si pas de garantie). */
    public BigDecimal garantieTotale() {
        return garantiePrix != null ? garantiePrix.multiply(BigDecimal.valueOf(quantite)) : BigDecimal.ZERO;
    }
}
