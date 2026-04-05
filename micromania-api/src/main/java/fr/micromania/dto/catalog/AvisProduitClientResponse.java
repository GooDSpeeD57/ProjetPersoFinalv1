package fr.micromania.dto.catalog;

import java.time.LocalDateTime;

public record AvisProduitClientResponse(
    Long id,
    Long idProduit,
    byte note,
    String commentaire,
    String statut,
    String motifModeration,
    LocalDateTime dateCreation,
    LocalDateTime dateModification,
    LocalDateTime dateModeration
) {}
