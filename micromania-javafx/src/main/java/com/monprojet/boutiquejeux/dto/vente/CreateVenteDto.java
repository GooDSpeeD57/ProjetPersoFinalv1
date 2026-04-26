package com.monprojet.boutiquejeux.dto.vente;

import java.util.List;

/** Correspond exactement à CreateFactureVenteRequest côté API. */
public class CreateVenteDto {
    public Long              idClient;
    public Long              idMagasin;
    public Long              idModePaiement;
    public Long              idContexteVente;
    public Long              idBonAchat;       // null si aucun bon sélectionné
    public List<LigneVenteDto> lignes;

    public CreateVenteDto(Long idClient, Long idMagasin, Long idModePaiement,
                          Long idContexteVente, Long idBonAchat,
                          List<LigneVenteDto> lignes) {
        this.idClient        = idClient;
        this.idMagasin       = idMagasin;
        this.idModePaiement  = idModePaiement;
        this.idContexteVente = idContexteVente;
        this.idBonAchat      = idBonAchat;
        this.lignes          = lignes;
    }
}
