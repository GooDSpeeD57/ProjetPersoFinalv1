package com.monprojet.boutiquejeux.dto.planning;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.LocalDate;

/** Miroir de PlanningEntryResponse côté API. */
@JsonIgnoreProperties(ignoreUnknown = true)
public class PlanningEntryDto {

    public Long   id;
    public Long   employeId;
    public String nomEmploye;
    public String role;
    public String dateTravail;   // "YYYY-MM-DD"
    public String heureDebut;    // "HH:mm:ss"
    public String heureFin;      // "HH:mm:ss"
    public String statut;
    public String noteInterne;

    // ── Getters pour PropertyValueFactory ────────────────────────

    public Long   getId()          { return id; }
    public String getNomEmploye()  { return nomEmploye != null ? nomEmploye : ""; }
    public String getRole()        { return role        != null ? role        : ""; }
    public String getNote()        { return noteInterne != null ? noteInterne : ""; }

    /** "YYYY-MM-DD" → "DD/MM" pour l'affichage */
    public String getDateAffichee() {
        if (dateTravail == null || dateTravail.length() < 10) return "";
        return dateTravail.substring(8, 10) + "/" + dateTravail.substring(5, 7);
    }

    /** Retourne le libellé court du jour (Lun, Mar…) */
    public String getJour() {
        if (dateTravail == null) return "";
        try {
            return switch (LocalDate.parse(dateTravail).getDayOfWeek()) {
                case MONDAY    -> "Lun";
                case TUESDAY   -> "Mar";
                case WEDNESDAY -> "Mer";
                case THURSDAY  -> "Jeu";
                case FRIDAY    -> "Ven";
                case SATURDAY  -> "Sam";
                case SUNDAY    -> "Dim";
            };
        } catch (Exception e) { return ""; }
    }

    /** "HH:mm:ss" ou "HH:mm" → "HH:mm" */
    public String getDebutHHMM() { return toHHMM(heureDebut); }
    public String getFinHHMM()   { return toHHMM(heureFin);   }

    private static String toHHMM(String s) {
        if (s == null || s.length() < 5) return "";
        return s.substring(0, 5);
    }
}
