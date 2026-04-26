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
) {}
