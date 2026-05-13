package com.monprojet.boutiquejeux.dto.api.catalog;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** Édition d'un variant (objet imbriqué dans ProduitVariantResponse). */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ApiEditionRef(Long id, String code, String libelle) {}
