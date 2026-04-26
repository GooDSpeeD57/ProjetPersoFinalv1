package fr.micromania.dto.catalog;

public record ProduitImageDto(
    Long id,
    Long variantId,
    String url,
    String alt,
    boolean principale,
    int ordreAffichage
) {}
