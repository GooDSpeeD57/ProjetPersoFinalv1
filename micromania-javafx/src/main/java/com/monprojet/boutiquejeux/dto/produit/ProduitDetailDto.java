package com.monprojet.boutiquejeux.dto.produit;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

/** Correspond à ProduitResponse côté API (GET /produits/{id}). */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProduitDetailDto {
    public Long    id;
    public String  nom;
    public String  slug;
    public String  description;
    public String  resumeCourt;
    public String  dateSortie;      // ISO "YYYY-MM-DD" ou null
    public String  editeur;
    public String  constructeur;
    public Integer pegi;
    public String  marque;
    public String  niveauAccesMin;
    public String  langue;
    public boolean misEnAvant;
    public boolean estPreCommande;
    public CategorieRefDto categorie;
    public List<ProduitVariantDto>  variants;
    public List<ProduitImageRefDto> images;
    public List<ScreenshotRefDto>   screenshots;
    public List<VideoRefDto>        videos;

    /** Sous-objet categorie reçu dans ProduitResponse (CodeDescriptionDto). */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class CategorieRefDto {
        public Long   id;
        public String code;
        public String description;
    }

    /** Sous-objet vidéo reçu dans ProduitResponse (ProduitVideoResponse). */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class VideoRefDto {
        public Long   id;
        public Long   produitId;
        public String url;
        public String titre;
        public int    ordreAffichage;
        public String langue;
    }

    /** Sous-objet screenshot reçu dans ProduitResponse (ScreenshotDto). */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ScreenshotRefDto {
        public Long   id;
        public Long   produitId;
        public String url;
        public String alt;
        public int    ordreAffichage;
    }

    /** Sous-objet image reçu dans ProduitResponse (ProduitImageDto). */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ProduitImageRefDto {
        public Long    id;
        public Long    variantId;
        public String  url;
        public String  alt;
        public boolean principale;
        public int     ordreAffichage;
    }
}
