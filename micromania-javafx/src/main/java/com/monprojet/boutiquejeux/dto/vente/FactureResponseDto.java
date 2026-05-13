package com.monprojet.boutiquejeux.dto.vente;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.math.BigDecimal;
@JsonIgnoreProperties(ignoreUnknown = true)
public class FactureResponseDto {
    public Long id;
    public String referenceFacture;
    public String statutFacture;
    public BigDecimal montantFinal;
    public String modePaiement;
    public String nomClientAffiche;
}
