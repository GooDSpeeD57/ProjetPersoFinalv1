package com.monprojet.boutiquejeux.dto.garantie;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** Miroir de GarantieResponse côté API. */
@JsonIgnoreProperties(ignoreUnknown = true)
public class GarantieDto {
    public Long    id;
    public Long    idVenteUnite;
    /** API : codeTypeGarantie */
    @JsonAlias("codeTypeGarantie")
    public String  typeCode;
    /** API : descTypeGarantie */
    @JsonAlias("descTypeGarantie")
    public String  typeDescription;
    public Integer dureeMois;
    public String  dateDebut;
    public String  dateFin;
    public boolean estEtendue;
    public String  dateExtension;
}
