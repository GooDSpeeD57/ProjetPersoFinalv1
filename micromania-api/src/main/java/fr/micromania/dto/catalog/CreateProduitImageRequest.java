package fr.micromania.dto.catalog;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateProduitImageRequest(
    @NotBlank @Size(max = 255) String url,
    @Size(max = 255)           String alt,
    boolean principale
) {}
