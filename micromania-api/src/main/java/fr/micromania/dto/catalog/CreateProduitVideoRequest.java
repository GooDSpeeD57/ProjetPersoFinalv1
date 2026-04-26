package fr.micromania.dto.catalog;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateProduitVideoRequest(
        @NotBlank String url,
        @NotBlank @Size(max = 255) String titre,
        int ordreAffichage,
        @Size(max = 10) String langue,
        String sousTitresUrl,
        String audioDescUrl,
        String transcription
) {
    public CreateProduitVideoRequest {
        if (langue == null || langue.isBlank()) {
            langue = "fr";
        }
    }
}
