package fr.micromania.dto.catalog;

import java.time.LocalDateTime;

public record AvisProduitPublicResponse(
    Long id,
    String auteur,
    byte note,
    String commentaire,
    LocalDateTime dateCreation
) {}
