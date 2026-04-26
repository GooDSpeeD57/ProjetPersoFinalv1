package com.monprojet.boutiquejeux.dto.api.catalog;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ApiScreenshot(
        Long   id,
        Long   produitId,
        String url,
        String alt,
        int    ordreAffichage
) {}
