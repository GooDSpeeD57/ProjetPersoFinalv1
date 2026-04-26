package com.monprojet.boutiquejeux.dto.api.referentiel;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** Entrée de référentiel GET /referentiel/editions. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ApiEdition(Long id, String code, String libelle) {}
