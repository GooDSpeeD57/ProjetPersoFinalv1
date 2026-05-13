package fr.micromania.dto.magasin;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

import java.time.LocalTime;

/**
 * Corps de requête pour créer ou mettre à jour un horaire.
 * jourSemaine : 1 = Lundi … 7 = Dimanche (ISO-8601)
 * heureOuverture / heureFermeture peuvent être null si ferme = true.
 */
public record HoraireMagasinRequest(
        @Min(1) @Max(7) int jourSemaine,
        LocalTime heureOuverture,
        LocalTime heureFermeture,
        boolean ferme
) {}
