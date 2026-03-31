package com.monprojet.boutiquejeux.dto.api.catalog;

import java.math.BigDecimal;

public record ApiProduitSummary(
        Long id,
        String nom,
        String slug,
        String categorie,
        String imageUrl,
        String imageAlt,
        BigDecimal prixMinimal,
        boolean disponible,
        boolean misEnAvant,
        Integer pegi
) {}
