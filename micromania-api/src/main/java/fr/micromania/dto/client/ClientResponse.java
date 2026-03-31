package fr.micromania.dto.client;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record ClientResponse(
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
        AvatarDto avatar,
        boolean emailVerifie,
        boolean telephoneVerifie,
        boolean compteActive,
        LocalDateTime dateDerniereConnexion,
        LocalDateTime dateCreation
) {}
