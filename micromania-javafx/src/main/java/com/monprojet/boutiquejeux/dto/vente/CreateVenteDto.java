package com.monprojet.boutiquejeux.dto.vente;

import java.util.List;

/** Correspond exactement à CreateFactureVenteRequest côté API. */
public class CreateVenteDto {
    public Long              idClient;        // null → vente anonyme
    public Long              idMagasin;
    public Long              idModePaiement;
    public Long              idContexteVente;
    public List<Long>        idsBonAchat;     // plusieurs bons possible
    public List<LigneVenteDto> lignes;

    public CreateVenteDto(Long idClient, Long idMagasin, Long idModePaiement,
                          Long idContexteVente, List<Long> idsBonAchat,
                          List<LigneVenteDto> lignes) {
        this.idClient        = idClient;
        this.idMagasin       = idMagasin;
        this.idModePaiement  = idModePaiement;
        this.idContexteVente = idContexteVente;
        this.idsBonAchat     = (idsBonAchat == null || idsBonAchat.isEmpty()) ? null : idsBonAchat;
        this.lignes          = lignes;
    }
}
