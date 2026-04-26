package fr.micromania.dto.catalog;

import jakarta.validation.constraints.Size;

/**
 * Corps du PATCH /variants/{id}/images/{imageId}.
 * Tous les champs sont optionnels : seuls les non-null sont appliqués.
 */
public record UpdateProduitImageRequest(
    @Size(max = 255) String  url,
    @Size(max = 255) String  alt,
    Boolean principale
) {}
