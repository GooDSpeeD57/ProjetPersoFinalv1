package com.monprojet.boutiquejeux.dto.referentiel;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TypeGarantieDto {
    public Long       id;
    public String     code;
    public String     description;
    public Integer    dureeMois;
    public BigDecimal prixExtension;
    public Long       categorieId;

    public String getLibelle() {
        String label = description != null ? description : code;
        if (prixExtension != null) label += String.format("  (+%.2f €)", prixExtension);
        return label;
    }
}
