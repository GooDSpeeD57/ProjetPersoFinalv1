package com.monprojet.boutiquejeux.dto.produit;

/** Correspond à CreateProduitRequest côté API (POST/PUT /produits). */
public class CreateProduitDto {
    public Long    idCategorie;
    public String  nom;
    public String  slug;
    public String  description;
    public String  resumeCourt;
    public String  dateSortie;      // "YYYY-MM-DD" ou null
    public String  editeur;
    public String  constructeur;
    public Integer pegi;
    public String  marque;
    public String  niveauAccesMin;
    public String  langue;          // "fr", "en", "de"… Défaut côté API : "fr"
    public boolean misEnAvant;
    public boolean estPreCommande;

    public CreateProduitDto() {}
}
