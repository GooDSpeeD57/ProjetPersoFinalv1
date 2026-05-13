package com.monprojet.boutiquejeux.dto.api.magasin;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Miroir de HoraireMagasinDto côté API.
 * Les heures sont reçues en String "HH:mm:ss" (ISO, write-dates-as-timestamps=false côté API).
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ApiHoraireMagasin(
        int    jourSemaine,
        String libelleJour,
        String heureOuverture,   // "HH:mm:ss" ou null
        String heureFermeture,   // "HH:mm:ss" ou null
        boolean ferme
) {
    /** Renvoie "HH:mm" pour l'affichage dans le template Thymeleaf. */
    public String ouvertureHHMM() { return toHHMM(heureOuverture); }
    public String fermetureHHMM() { return toHHMM(heureFermeture); }

    /** Renvoie "HH:mm – HH:mm" ou "Fermé" selon le jour. */
    public String affichage() {
        if (ferme) return "Fermé";
        String o = toHHMM(heureOuverture);
        String f = toHHMM(heureFermeture);
        if (o.isEmpty() && f.isEmpty()) return "";
        return o + " – " + f;
    }

    private static String toHHMM(String s) {
        if (s == null || s.length() < 5) return "";
        return s.substring(0, 5); // "HH:mm"
    }
}
