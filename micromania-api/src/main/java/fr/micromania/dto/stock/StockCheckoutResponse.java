package fr.micromania.dto.stock;

public record StockCheckoutResponse(
    Long idMagasin,
    String nomMagasin,
    int quantiteDisponible
) {}
