package fr.micromania.dto.catalog;

public record ProduitImageDto(
    Long id,
    String url,
    String alt,
    boolean principale,
    int ordreAffichage
) {}
