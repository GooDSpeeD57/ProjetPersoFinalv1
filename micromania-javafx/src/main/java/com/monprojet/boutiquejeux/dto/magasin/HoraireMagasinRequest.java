package com.monprojet.boutiquejeux.dto.magasin;

/** Corps envoyé à PUT /magasins/{id}/horaires pour un jour */
public class HoraireMagasinRequest {
    public int     jourSemaine;    // 1 = Lundi … 7 = Dimanche
    public String  heureOuverture; // "HH:mm" ou null si ferme
    public String  heureFermeture; // "HH:mm" ou null si ferme
    public boolean ferme;
}
