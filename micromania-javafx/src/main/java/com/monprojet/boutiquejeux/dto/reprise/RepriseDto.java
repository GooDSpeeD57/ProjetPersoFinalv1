package com.monprojet.boutiquejeux.dto.reprise;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.math.BigDecimal;

/** Miroir de RepriseResponse côté API. */
@JsonIgnoreProperties(ignoreUnknown = true)
public class RepriseDto {
    public Long       id;
    public String     referenceReprise;
    public String     statut;
    public String     modeCompensation;
    public String     client;
    public BigDecimal montantTotalEstime;
    public BigDecimal montantTotalValide;
    public String     commentaire;
    public String     dateCreation;
}
