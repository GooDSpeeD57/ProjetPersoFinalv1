package fr.micromania.dto.stock;

import java.time.LocalDateTime;

public record MouvementStockResponse(
    Long id,
    Long idVariant,
    String nomCommercial,
    String typeMouvement,
    String sourceStock,
    int quantite,
    String lieu,
    String commentaire,
    LocalDateTime dateMouvement
) {}
