package fr.micromania.dto.client;

public record AdresseResponse(
    Long id,
    String typeAdresse,
    String rue,
    String complement,
    String ville,
    String codePostal,
    String pays,
    boolean estDefaut
) {}
