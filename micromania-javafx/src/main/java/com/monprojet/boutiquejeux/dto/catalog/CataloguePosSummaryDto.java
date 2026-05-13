package com.monprojet.boutiquejeux.dto.catalog;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.math.BigDecimal;
@JsonIgnoreProperties(ignoreUnknown = true)
public class CataloguePosSummaryDto {
    public Long idProduit;
    public Long idVariant;
    public String nom;
    public String sku;
    public String plateforme;
    public String edition;
    public String format;
    public BigDecimal prixMagasin;
    public int stockNeuf;
    public int stockOccasion;
    public boolean disponible;
    public String getAffichageStock(boolean occasion) {
        return occasion ? (stockOccasion > 0 ? stockOccasion + " dispo" : "Rupture")
                        : (stockNeuf    > 0 ? stockNeuf    + " dispo" : "Rupture");
    }
}
