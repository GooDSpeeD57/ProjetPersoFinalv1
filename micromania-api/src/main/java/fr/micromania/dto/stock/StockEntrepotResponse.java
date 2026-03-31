package fr.micromania.dto.stock;

public record StockEntrepotResponse(
    Long idStockEntrepot,
    Long idVariant,
    String nomCommercial,
    String sku,
    Long idEntrepot,
    String nomEntrepot,
    int quantiteNeuf,
    int quantiteReservee,
    int quantiteDisponible
) {}
