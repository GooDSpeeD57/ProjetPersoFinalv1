package com.monprojet.boutiquejeux.dto.vente;

import java.math.BigDecimal;

/** Correspond exactement à LigneFactureRequest côté API. */
public class LigneVenteDto {
    public Long       idVariant;
    public Integer    quantite;
    public BigDecimal prixUnitaire;
    public Long       idTypeGarantie;  // null si pas de garantie

    public LigneVenteDto(Long idVariant, Integer quantite, BigDecimal prixUnitaire, Long idTypeGarantie) {
        this.idVariant      = idVariant;
        this.quantite       = quantite;
        this.prixUnitaire   = prixUnitaire;
        this.idTypeGarantie = idTypeGarantie;
    }
}
