package fr.micromania.dto.stock;

public record StockEntrepotCheckoutResponse(
    Long idVariant,
    String nomCommercial,
    String sku,
    int quantiteTotaleDisponible
) {}
