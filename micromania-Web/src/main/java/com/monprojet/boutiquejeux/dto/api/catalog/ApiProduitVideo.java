package com.monprojet.boutiquejeux.dto.api.catalog;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ApiProduitVideo(
        Long   id,
        String url,
        String titre,
        int    ordreAffichage,
        String langue
) {}
