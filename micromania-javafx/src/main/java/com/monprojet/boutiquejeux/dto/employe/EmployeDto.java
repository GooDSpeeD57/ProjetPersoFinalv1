package com.monprojet.boutiquejeux.dto.employe;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Miroir de EmployeResponse (Spring Boot API).
 * Retourné par GET /employes/me et GET /employes/{id}
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class EmployeDto {

    public Long   id;
    public String nom;
    public String prenom;
    public String email;
    public String telephone;
    @JsonProperty("role")
    public String roleCode;      // ROLE_VENDEUR / ROLE_MANAGER / ROLE_ADMIN
    public String roleLibelle;
    public Long   magasinId;
    public String magasinNom;
    public String dateEmbauche;
    public boolean actif;
    public String dateCreation;

    // Getters requis par PropertyValueFactory (JavaFX TableView)
    public Long   getId()           { return id; }
    public String getNom()          { return nom; }
    public String getPrenom()       { return prenom; }
    public String getEmail()        { return email; }
    public String getTelephone()    { return telephone; }
    public String getRoleCode()     { return roleCode; }
    public String getRoleLibelle()  { return roleLibelle; }
    public Long   getMagasinId()    { return magasinId; }
    public String getMagasinNom()   { return magasinNom; }
    public String getDateEmbauche() { return dateEmbauche; }
    public boolean isActif()        { return actif; }
    public String getDateCreation() { return dateCreation; }

    public String getNomComplet() {
        return (prenom != null ? prenom : "") + " " + (nom != null ? nom : "");
    }
}
