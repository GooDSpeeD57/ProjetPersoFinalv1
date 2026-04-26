package com.monprojet.boutiquejeux.dto.produit;

/** Correspond à CreateProduitImageRequest côté API (POST /produits/{id}/images). */
public class CreateProduitImageDto {
    public String  url;
    public String  alt;
    public boolean principale;

    public CreateProduitImageDto() {}

    public CreateProduitImageDto(String url, String alt, boolean principale) {
        this.url        = url;
        this.alt        = alt;
        this.principale = principale;
    }
}
