package com.monprojet.boutiquejeux.dto.stock;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** Correspond à StockMagasinResponse côté API. */
@JsonIgnoreProperties(ignoreUnknown = true)
public class StockMagasinDto {
    public Long   idStockMagasin;
    public Long   idVariant;
    public String nomCommercial;
    public String sku;
    public Long   idMagasin;
    public String nomMagasin;
    public int    quantiteNeuf;
    public int    quantiteOccasion;
    public int    quantiteReprise;
    public int    quantiteReservee;
    public int    quantiteDisponible;
}
