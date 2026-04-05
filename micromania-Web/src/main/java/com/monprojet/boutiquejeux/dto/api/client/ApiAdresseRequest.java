package com.monprojet.boutiquejeux.dto.api.client;

public record ApiAdresseRequest(
        Long idTypeAdresse,
        String codeTypeAdresse,
        String rue,
        String complement,
        String ville,
        String codePostal,
        String pays,
        boolean estDefaut
) {}
