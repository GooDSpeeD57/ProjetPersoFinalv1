package com.monprojet.boutiquejeux.dto.api.facture;

import java.math.BigDecimal;

public record ApiLigneFacture(
    Long idVariant,
    String nomCommercial,
    String sku,
    int quantite,
    BigDecimal prixUnitaire,
    BigDecimal tauxTvaApplique,
    BigDecimal montantLigne,
    BigDecimal montantHtLigne,
    BigDecimal montantTvaLigne,
    String garantieLabel,
    BigDecimal garantiePrix
) {
    /** Prix garantie × quantité (0 si pas de garantie). */
    public BigDecimal garantieTotale() {
        return garantiePrix != null ? garantiePrix.multiply(BigDecimal.valueOf(quantite)) : BigDecimal.ZERO;
    }
}
