package com.monprojet.boutiquejeux.dto.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Résultat de GET /clients/{id} (profil complet avec points fidélité)
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ClientDetailDto {

    public Long   id;
    public String nom;
    public String prenom;
    public String email;
    public String telephone;
    public String typeFidelite;
    public int    soldePoints;
    public String dateInscription;

    public String getNomComplet() {
        return prenom + " " + nom;
    }
}
