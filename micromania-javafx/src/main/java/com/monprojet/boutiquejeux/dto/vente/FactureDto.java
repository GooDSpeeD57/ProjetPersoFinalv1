package com.monprojet.boutiquejeux.dto.vente;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.math.BigDecimal;

/**
 * Réponse de POST /factures/vente-magasin (FactureResponse côté API).
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class FactureDto {

    public Long       id;
    public String     referenceFacture;
    public String     statutFacture;
    public String     contexteVente;
    public String     modePaiement;
    public String     magasin;
    public BigDecimal montantTotal;
    public BigDecimal montantFinal;
    public String     dateFacture;    // ISO "YYYY-MM-DDTHH:MM:SS"
}
