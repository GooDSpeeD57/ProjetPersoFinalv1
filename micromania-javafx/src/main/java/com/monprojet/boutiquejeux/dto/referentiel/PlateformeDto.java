package com.monprojet.boutiquejeux.dto.referentiel;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** Correspond à PlatformeDto côté API (GET /referentiel/plateformes). */
@JsonIgnoreProperties(ignoreUnknown = true)
public class PlateformeDto {
    public Long   id;
    public String code;
    public String libelle;

    @Override
    public String toString() {
        if (libelle != null && !libelle.isBlank()) return libelle;
        return code != null ? code : "";
    }
}
