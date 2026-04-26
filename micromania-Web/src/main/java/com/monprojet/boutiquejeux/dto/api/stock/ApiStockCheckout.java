package com.monprojet.boutiquejeux.dto.api.stock;

public record ApiStockCheckout(
        Long idMagasin,
        String nomMagasin,
        int quantiteDisponible
) {}
