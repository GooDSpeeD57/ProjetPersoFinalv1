package com.monprojet.boutiquejeux.dto.commande;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = true)
public class LigneCommandeDto {
    public Long       id;
    public String     nomCommercial;
    public String     sku;
    public int        quantite;
    public BigDecimal prixUnitaire;
    public BigDecimal montantLigne;

    // Getters requis par PropertyValueFactory (JavaFX TableView)
    public Long       getId()            { return id; }
    public String     getNomCommercial() { return nomCommercial; }
    public String     getSku()           { return sku; }
    public int        getQuantite()      { return quantite; }
    public BigDecimal getPrixUnitaire()  { return prixUnitaire; }
    public BigDecimal getMontantLigne()  { return montantLigne; }
}
