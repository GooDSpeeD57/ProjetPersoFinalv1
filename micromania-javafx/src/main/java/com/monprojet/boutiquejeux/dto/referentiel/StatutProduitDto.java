package com.monprojet.boutiquejeux.dto.referentiel;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** Statut d'un variant produit (neuf, occasion, reconditionné…). */
@JsonIgnoreProperties(ignoreUnknown = true)
public class StatutProduitDto {
    public Long   id;
    public String code;
    public String description;

    @Override
    public String toString() {
        return description != null && !description.isBlank() ? description : code;
    }
}
