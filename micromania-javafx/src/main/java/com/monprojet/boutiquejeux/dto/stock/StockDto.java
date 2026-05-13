package com.monprojet.boutiquejeux.dto.stock;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class StockDto {
    public Long id;
    public String nomProduit;
    public String genre;
    public String typeStock;
    public int quantite;
    public double prix;
    public int seuilAlerte;
}
