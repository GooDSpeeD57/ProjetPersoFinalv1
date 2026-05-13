package fr.micromania.dto.catalog;

public record ScreenshotDto(
    Long   id,
    Long   produitId,
    String url,
    String alt,
    int    ordreAffichage
) {}
