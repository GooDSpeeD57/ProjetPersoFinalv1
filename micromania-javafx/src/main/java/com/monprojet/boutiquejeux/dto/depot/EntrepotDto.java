package com.monprojet.boutiquejeux.dto.depot;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class EntrepotDto {
    public Long   id;
    public String nom;
    public String code;
    public String telephone;
    public String email;
    public String responsable;
    public boolean actif;

    @Override public String toString() { return nom != null ? nom : code; }
}
