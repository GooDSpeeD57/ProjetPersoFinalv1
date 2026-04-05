package com.monprojet.boutiquejeux.dto.api.catalog;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ApiProduitSummary(
        Long id,
        String nom,
        String slug,
        String categorie,
        String imageUrl,
        String imageAlt,
        BigDecimal prixNeuf,
        BigDecimal prixOccasion,
        boolean disponible,
        boolean misEnAvant,
        Integer pegi,
        Double noteMoyenne,
        long nbAvis
) {}
