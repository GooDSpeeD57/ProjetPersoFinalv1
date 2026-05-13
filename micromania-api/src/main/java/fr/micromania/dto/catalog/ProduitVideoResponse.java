package fr.micromania.dto.catalog;

import java.time.LocalDateTime;

public record ProduitVideoResponse(
        Long id,
        String url,
        String titre,
        int ordreAffichage,
        String langue,
        String sousTitresUrl,
        String audioDescUrl,
        LocalDateTime dateCreation
) {}
