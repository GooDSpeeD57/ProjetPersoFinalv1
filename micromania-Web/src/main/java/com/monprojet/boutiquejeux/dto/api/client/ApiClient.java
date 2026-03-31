package com.monprojet.boutiquejeux.dto.api.client;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record ApiClient(
        Long id,
        String pseudo,
        String nom,
        String prenom,
        LocalDate dateNaissance,
        String email,
        String telephone,
        String typeFidelite,
        String numeroCarteFidelite,
        Integer soldePoints,
        ApiAvatar avatar,
        boolean emailVerifie,
        boolean telephoneVerifie,
        boolean compteActive,
        LocalDateTime dateDerniereConnexion,
        LocalDateTime dateCreation
) {}
