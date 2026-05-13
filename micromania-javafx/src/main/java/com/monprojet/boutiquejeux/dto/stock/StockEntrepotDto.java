package com.monprojet.boutiquejeux.dto.stock;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** Correspond à StockEntrepotResponse côté API. */
@JsonIgnoreProperties(ignoreUnknown = true)
public class StockEntrepotDto {
    public Long   idStockEntrepot;
    public Long   idVariant;
    public String nomCommercial;
    public String sku;
    public Long   idEntrepot;
    public String nomEntrepot;
    public int    quantiteNeuf;
    public int    quantiteReservee;
    public int    quantiteDisponible;
}
