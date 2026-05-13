package com.monprojet.boutiquejeux.dto.vente;

import java.math.BigDecimal;

/** Correspond exactement à LigneFactureRequest côté API. */
public class LigneVenteDto {
    public Long       idVariant;
    public Integer    quantite;
    public BigDecimal prixUnitaire;
    public Long       idTypeGarantie;  // null si pas de garantie
    public String     numeroSerie;     // null sauf pour consoles/accessoires sérialisés

    public LigneVenteDto(Long idVariant, Integer quantite, BigDecimal prixUnitaire,
                         Long idTypeGarantie, String numeroSerie) {
        this.idVariant      = idVariant;
        this.quantite       = quantite;
        this.prixUnitaire   = prixUnitaire;
        this.idTypeGarantie = idTypeGarantie;
        this.numeroSerie    = numeroSerie;
    }
}
