package com.monprojet.boutiquejeux.dto.referentiel;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** Correspond à MagasinPublicResponse côté API (GET /magasins). */
@JsonIgnoreProperties(ignoreUnknown = true)
public class MagasinDto {
    public Long   id;
    public String nom;
    public String ville;
    public String codePostal;

    @Override
    public String toString() {
        if (nom == null) return "";
        return ville != null ? nom + " — " + ville : nom;
    }
}
