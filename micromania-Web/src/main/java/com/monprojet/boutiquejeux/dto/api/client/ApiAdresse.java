package com.monprojet.boutiquejeux.dto.api.client;

public record ApiAdresse(
        Long id,
        String typeAdresse,
        String rue,
        String complement,
        String ville,
        String codePostal,
        String pays,
        boolean estDefaut
) {}
