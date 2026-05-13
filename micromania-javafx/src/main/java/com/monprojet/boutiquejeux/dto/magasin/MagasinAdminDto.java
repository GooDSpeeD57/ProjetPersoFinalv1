package com.monprojet.boutiquejeux.dto.magasin;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MagasinAdminDto {
    public Long    id;
    public String  nom;
    public String  telephone;
    public String  email;
    public boolean actif;
    public String  dateCreation;
    public String  dateModification;

    // Getters requis par PropertyValueFactory (JavaFX TableView)
    public Long    getId()              { return id; }
    public String  getNom()             { return nom; }
    public String  getTelephone()       { return telephone; }
    public String  getEmail()           { return email; }
    public boolean isActif()            { return actif; }
    public String  getDateCreation()    { return dateCreation; }
    public String  getDateModification(){ return dateModification; }

    @Override
    public String toString() { return nom != null ? nom : ""; }
}
