package fr.micromania.dto.entrepot;

/**
 * Réponse lecture d'un entrepôt.
 */
public record EntrepotResponse(
        Long    id,
        String  nom,
        String  code,
        String  telephone,
        String  email,
        String  responsable,
        boolean actif
) {}
