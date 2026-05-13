package fr.micromania.dto.planning;

/**
 * Une ligne du tableau de planning hebdomadaire (une par employé).
 * Chaque jour contient "HH:mm-HH:mm" ou "REPOS" si pas de créneau.
 */
public record PlanningRowResponse(
    Long   employeId,
    String nomEmploye,
    String role,
    String lundi,
    String mardi,
    String mercredi,
    String jeudi,
    String vendredi,
    String samedi,
    String dimanche
) {}
