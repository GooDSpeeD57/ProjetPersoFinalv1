package com.monprojet.boutiquejeux.dto.client;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.math.BigDecimal;
@JsonIgnoreProperties(ignoreUnknown = true)
public class BonAchatDto {
    public Long id;
    public String codeBon;
    public BigDecimal valeur;
    public boolean utilise;
    public String dateExpiration;
    public String getNomAffiche() { return codeBon + " — " + valeur + " €"; }
}
