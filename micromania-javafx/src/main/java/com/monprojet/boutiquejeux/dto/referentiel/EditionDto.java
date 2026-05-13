package com.monprojet.boutiquejeux.dto.referentiel;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** Edition d'un variant produit (Standard, Day One, Deluxe, Collector…). */
@JsonIgnoreProperties(ignoreUnknown = true)
public class EditionDto {
    public Long   id;
    public String code;
    public String libelle;

    @Override
    public String toString() {
        return libelle != null && !libelle.isBlank() ? libelle : code;
    }
}
