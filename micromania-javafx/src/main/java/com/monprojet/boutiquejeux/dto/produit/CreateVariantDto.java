package com.monprojet.boutiquejeux.dto.produit;

/** Correspond à CreateVariantRequest côté API (POST /variants). */
public class CreateVariantDto {
    public Long   idProduit;
    public Long   idPlateforme;
    public Long   idFormatProduit;  // obligatoire — format physique / démat / collector…
    public Long   idStatutProduit;  // obligatoire — neuf / occasion / reconditionné…
    public String nomCommercial;
    public String sku;              // auto-généré côté JavaFX
    public Long   idEdition;        // référence vers edition_produit (nullable)
    public Double prixNeuf;
    public Double prixOccasion;
    public Double prixReprise;
    public Double prixLocation;

    // Champs techniques avec valeurs par défaut
    public String  langueVente    = "fr";
    public boolean scelle         = false;
    public boolean estDemat       = false;
    public boolean estTcgUnitaire = false;
    public boolean estReprise     = false;

    public CreateVariantDto() {}
}
