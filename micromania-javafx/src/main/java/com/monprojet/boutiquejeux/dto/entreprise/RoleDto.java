package com.monprojet.boutiquejeux.dto.entreprise;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RoleDto {
    public Long   id;
    public String code;
    public String libelle;

    // Getters requis par PropertyValueFactory (JavaFX TableView)
    public Long   getId()      { return id; }
    public String getCode()    { return code; }
    public String getLibelle() { return libelle; }

    @Override
    public String toString() { return libelle != null ? libelle : code; }
}
