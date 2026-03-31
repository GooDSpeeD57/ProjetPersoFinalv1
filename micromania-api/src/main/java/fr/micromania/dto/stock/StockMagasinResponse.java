package fr.micromania.dto.stock;

public record StockMagasinResponse(
    Long idStockMagasin,
    Long idVariant,
    String nomCommercial,
    String sku,
    Long idMagasin,
    String nomMagasin,
    int quantiteNeuf,
    int quantiteOccasion,
    int quantiteReprise,
    int quantiteReservee,
    int quantiteDisponible
) {}
