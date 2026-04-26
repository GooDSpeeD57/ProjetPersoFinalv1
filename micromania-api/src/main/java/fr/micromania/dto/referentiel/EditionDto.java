package fr.micromania.dto.referentiel;

/** DTO édition produit — exposé dans ProduitVariantResponse et GET /referentiel/editions */
public record EditionDto(Long id, String code, String libelle) {}
