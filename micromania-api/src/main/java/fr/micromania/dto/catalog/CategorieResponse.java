package fr.micromania.dto.catalog;

public record CategorieResponse(
    Long id,
    String nom,
    String description,
    String typeCategorie,
    boolean actif
) {}
