package com.monprojet.boutiquejeux.dto.api.catalog;

public record ApiProduitImage(
        Long id,
        String url,
        String alt,
        boolean principale,
        int ordreAffichage
) {}
