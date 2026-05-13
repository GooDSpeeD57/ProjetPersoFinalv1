package com.monprojet.boutiquejeux.dto.garantie;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** Miroir de GarantieResponse côté API. */
@JsonIgnoreProperties(ignoreUnknown = true)
public class GarantieDto {
    public Long    id;
    public Long    idVenteUnite;
    @JsonAlias("codeTypeGarantie")
    public String  typeCode;
    @JsonAlias("descTypeGarantie")
    public String  typeDescription;
    public String  nomProduit;
    /** Durée totale en mois (légale + extension si présente). */
    public Integer dureeMois;
    public String  dateDebut;
    public String  dateFin;
    public String  numeroSerie;
    /** "LEGALE" = garantie de conformité / "EXTENSION" = extension payante souscrite. */
    public String  typeItem;
}
