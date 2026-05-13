package fr.micromania.dto.magasin;

/**
 * Représentation publique des horaires d'un jour pour un magasin.
 * jourSemaine : 1 = Lundi … 7 = Dimanche (ISO-8601)
 * Les heures sont des Strings "HH:mm:ss" (ou null si fermé),
 * évitant tout problème de sérialisation Jackson LocalTime.
 */
public record HoraireMagasinDto(
        int     jourSemaine,
        String  libelleJour,
        String  heureOuverture,   // "HH:mm:ss" ou null
        String  heureFermeture,   // "HH:mm:ss" ou null
        boolean ferme
) {}
