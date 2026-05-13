package fr.micromania.dto.favori;

import java.time.LocalDateTime;

public record FavoriProduitResponse(
        Long idProduit,
        String nomProduit,
        String slug,
        LocalDateTime dateAjout
) {}
