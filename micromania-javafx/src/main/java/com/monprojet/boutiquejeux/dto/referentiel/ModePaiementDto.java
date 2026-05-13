package com.monprojet.boutiquejeux.dto.referentiel;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
@JsonIgnoreProperties(ignoreUnknown = true)
public class ModePaiementDto {
    public Long id;
    public String code;
    @Override public String toString() { return code; }
}
