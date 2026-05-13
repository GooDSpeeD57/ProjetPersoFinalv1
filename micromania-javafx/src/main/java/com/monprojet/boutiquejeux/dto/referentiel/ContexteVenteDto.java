package com.monprojet.boutiquejeux.dto.referentiel;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
@JsonIgnoreProperties(ignoreUnknown = true)
public class ContexteVenteDto {
    public Long id;
    public String code;
    public String description;
}
