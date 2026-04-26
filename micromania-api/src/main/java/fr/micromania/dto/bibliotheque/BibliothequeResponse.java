package fr.micromania.dto.bibliotheque;

import java.time.LocalDateTime;

public record BibliothequeResponse(
        Long id,
        Long idVariant,
        String nomCommercial,
        String sku,
        Long idFacture,
        String cleActivation,
        LocalDateTime dateAttribution
) {}
