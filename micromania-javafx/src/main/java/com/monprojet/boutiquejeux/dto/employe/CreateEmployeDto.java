package com.monprojet.boutiquejeux.dto.employe;

/** Miroir de CreateEmployeRequest côté API. */
public class CreateEmployeDto {
    public String nom;
    public String prenom;
    public String email;
    public String telephone;
    public String motDePasse;
    public Long   idRole;
    public Long   idMagasin;
    public String dateEmbauche; // ISO date "yyyy-MM-dd"
}
