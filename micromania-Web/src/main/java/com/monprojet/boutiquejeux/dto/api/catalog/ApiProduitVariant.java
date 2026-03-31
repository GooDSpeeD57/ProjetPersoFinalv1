package com.monprojet.boutiquejeux.dto.api.catalog;

import java.math.BigDecimal;

public record ApiProduitVariant(
        Long id,
        String sku,
        String ean,
        String nomCommercial,
        ApiPlateforme plateforme,
        String formatProduit,
        String statutProduit,
        String edition,
        String couleur,
        String langueVente,
        boolean scelle,
        boolean estDemat,
        boolean estTcgUnitaire,
        boolean estReprise,
        boolean necessiteNumeroSerie,
        ApiTauxTva tauxTva,
        BigDecimal prixWeb,
        BigDecimal prixMagasin,
        boolean actif
) {}
