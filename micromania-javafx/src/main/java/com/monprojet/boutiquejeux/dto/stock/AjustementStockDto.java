package com.monprojet.boutiquejeux.dto.stock;

/** Correspond à AjustementStockRequest côté API. */
public class AjustementStockDto {
    public Long   idVariant;
    public Long   idMagasin;    // null pour un dépôt
    public Long   idEntrepot;   // null pour un magasin
    public String sourceStock;  // "NEUF", "OCCASION" ou "REPRISE"
    public int    delta;        // positif = réception, négatif = sortie
    public String commentaire;

    public AjustementStockDto(Long idVariant, Long idMagasin, Long idEntrepot,
                               String sourceStock, int delta, String commentaire) {
        this.idVariant   = idVariant;
        this.idMagasin   = idMagasin;
        this.idEntrepot  = idEntrepot;
        this.sourceStock = sourceStock;
        this.delta       = delta;
        this.commentaire = commentaire;
    }
}
