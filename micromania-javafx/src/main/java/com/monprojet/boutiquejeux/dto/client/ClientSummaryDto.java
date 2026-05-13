package com.monprojet.boutiquejeux.dto.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Résultat de GET /clients?q=... (page de résultats de recherche)
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ClientSummaryDto {

    public Long   id;
    public String nom;
    public String prenom;
    public String email;
    public String telephone;
    public String typeFidelite;

    public String getNomComplet() {
        return prenom + " " + nom;
    }
}
