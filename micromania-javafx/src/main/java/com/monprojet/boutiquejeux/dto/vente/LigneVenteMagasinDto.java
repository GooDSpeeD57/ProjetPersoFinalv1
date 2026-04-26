package com.monprojet.boutiquejeux.dto.vente;
import java.math.BigDecimal;
public class LigneVenteMagasinDto {
    public Long       idVariant;
    public Integer    quantite;
    public BigDecimal prixUnitaire;
    public LigneVenteMagasinDto(Long idVariant, Integer quantite, BigDecimal prixUnitaire) {
        this.idVariant = idVariant;
        this.quantite = quantite;
        this.prixUnitaire = prixUnitaire;
    }
}
