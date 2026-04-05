package com.monprojet.boutiquejeux.dto.api.client;

import java.time.LocalDate;

public record ApiUpdateClientRequest(
        String pseudo,
        String nom,
        String prenom,
        LocalDate dateNaissance,
        String email,
        String telephone,
        Long idAvatar
) {}
