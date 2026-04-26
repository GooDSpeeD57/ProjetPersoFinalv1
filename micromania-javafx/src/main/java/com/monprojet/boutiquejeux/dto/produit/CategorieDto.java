package com.monprojet.boutiquejeux.dto.produit;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** Correspond à CategorieResponse côté API (GET /categories). */
@JsonIgnoreProperties(ignoreUnknown = true)
public class CategorieDto {
    public Long    id;
    public String  nom;
    public String  description;
    public boolean actif;

    @Override public String toString() { return nom != null ? nom : ""; }
}
