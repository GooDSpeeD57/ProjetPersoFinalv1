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
        BigDecimal montantLigne
) {}
