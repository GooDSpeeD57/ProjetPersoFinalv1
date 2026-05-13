package com.monprojet.boutiquejeux.dto.magasin;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** Horaire d'un jour renvoyé par GET /magasins/{id}/horaires */
@JsonIgnoreProperties(ignoreUnknown = true)
public class HoraireMagasinDto {
    public int     jourSemaine;    // 1 = Lundi … 7 = Dimanche
    public String  libelleJour;
    public String  heureOuverture; // "HH:mm:ss" ou null
    public String  heureFermeture; // "HH:mm:ss" ou null
    public boolean ferme;

    /** Renvoie "HH:mm" ou "" pour l'affichage dans les TextFields */
    public String ouvertureHHMM() {
        return toHHMM(heureOuverture);
    }

    public String fermetureHHMM() {
        return toHHMM(heureFermeture);
    }

    private static String toHHMM(String s) {
        if (s == null || s.length() < 5) return "";
        return s.substring(0, 5); // "HH:mm"
    }
}
