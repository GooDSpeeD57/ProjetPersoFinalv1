package com.monprojet.boutiquejeux.dto.referentiel;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** Format de produit (physique, dématérialisé, collector…). */
@JsonIgnoreProperties(ignoreUnknown = true)
public class FormatProduitDto {
    public Long   id;
    public String code;
    public String description;

    @Override
    public String toString() {
        return description != null && !description.isBlank() ? description : code;
    }
}
