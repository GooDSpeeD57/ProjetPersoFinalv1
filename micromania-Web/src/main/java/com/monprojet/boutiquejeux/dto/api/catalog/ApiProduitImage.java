package com.monprojet.boutiquejeux.dto.api.catalog;

public record ApiProduitImage(
        Long id,
        Long variantId,
        String url,
        String alt,
        boolean principale,
        int ordreAffichage
) {}
