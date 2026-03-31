package fr.micromania.dto.client;

public record ClientSummary(
    Long id,
    String pseudo,
    String nom,
    String prenom,
    String email,
    String telephone,
    String typeFidelite,
    boolean compteActive
) {}
