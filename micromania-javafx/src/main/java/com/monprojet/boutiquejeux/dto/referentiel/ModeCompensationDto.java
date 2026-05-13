package com.monprojet.boutiquejeux.dto.referentiel;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** Miroir de ModeCompensationReprise (référentiel). */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ModeCompensationDto {
    public Long   id;
    public String code;
    public String description;

    @Override
    public String toString() {
        return description != null && !description.isBlank() ? description : (code != null ? code : "");
    }
}
