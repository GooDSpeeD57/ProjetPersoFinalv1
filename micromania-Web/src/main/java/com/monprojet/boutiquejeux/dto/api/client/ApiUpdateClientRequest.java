package com.monprojet.boutiquejeux.dto.api.client;

public record ApiUpdateClientRequest(
        String pseudo,
        String nom,
        String prenom,
        String email,
        String telephone,
        Long idAvatar
) {}
