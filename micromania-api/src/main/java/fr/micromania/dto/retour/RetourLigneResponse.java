package fr.micromania.dto.retour;

public record RetourLigneResponse(
    Long id,
    Long idLigneFacture,
    int quantite,
    String motif
) {}
