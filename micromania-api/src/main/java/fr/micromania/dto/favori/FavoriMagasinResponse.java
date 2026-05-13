package fr.micromania.dto.favori;

import java.time.LocalDateTime;

public record FavoriMagasinResponse(
        Long idMagasin,
        String nomMagasin,
        LocalDateTime dateAjout,
        boolean principal
) {}
