package fr.micromania.dto.magasin;

import java.time.LocalDateTime;

public record MagasinAdminResponse(
        Long          id,
        String        nom,
        String        telephone,
        String        email,
        boolean       actif,
        LocalDateTime dateCreation,
        LocalDateTime dateModification
) {}
