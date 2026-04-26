package com.monprojet.boutiquejeux.dto.api.stock;

public record ApiStockEntrepotCheckout(
        Long idVariant,
        String nomCommercial,
        String sku,
        int quantiteTotaleDisponible
) {}
