package com.monprojet.boutiquejeux.dto.reprise;

import java.util.List;

/** Miroir de CreateRepriseRequest côté API. */
public class CreateRepriseDto {
    public Long                    idClient;           // null si client anonyme
    public Long                    idMagasin;
    public Long                    idModeCompensation;
    public List<CreateRepriseLigneDto> lignes;
    public String                  commentaire;
}
