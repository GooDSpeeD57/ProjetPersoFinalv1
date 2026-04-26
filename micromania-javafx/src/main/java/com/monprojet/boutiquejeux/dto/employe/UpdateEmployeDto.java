package com.monprojet.boutiquejeux.dto.employe;

/** Miroir de UpdateEmployeRequest côté API. Tous les champs sont optionnels (null = inchangé). */
public class UpdateEmployeDto {
    public String  nom;
    public String  prenom;
    public String  email;
    public String  telephone;
    public String  motDePasse;
    public Long    idRole;
    public Long    idMagasin;
    public String  dateEmbauche;
    public Boolean actif;
}
