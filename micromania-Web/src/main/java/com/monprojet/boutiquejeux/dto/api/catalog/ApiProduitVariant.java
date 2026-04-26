package com.monprojet.boutiquejeux.dto.api.catalog;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ApiProduitVariant(
        Long id,
        String sku,
        String ean,
        String nomCommercial,
        ApiPlateforme plateforme,
        String formatProduit,
        String statutProduit,
        ApiEditionRef edition,
        String couleur,
        String langueVente,
        boolean scelle,
        boolean estDemat,
        boolean estTcgUnitaire,
        boolean estReprise,
        boolean necessiteNumeroSerie,
        ApiTauxTva tauxTva,
        BigDecimal prixNeuf,
        BigDecimal prixOccasion,
        BigDecimal prixReprise,
        BigDecimal prixLocation,
        boolean actif
) {}
