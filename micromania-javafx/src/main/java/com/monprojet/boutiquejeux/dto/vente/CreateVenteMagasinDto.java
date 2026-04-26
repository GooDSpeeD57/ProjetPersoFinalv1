package com.monprojet.boutiquejeux.dto.vente;
import java.util.List;
/** Miroir exact de CreateFactureVenteRequest (Spring Boot API) */
public class CreateVenteMagasinDto {
    public Long   idClient;
    public String nomClient;
    public String emailClient;
    public Long   idMagasin;
    public Long   idEmploye;      // optionnel, peut être null
    public Long   idModePaiement;
    public Long   idContexteVente;
    public List<LigneVenteMagasinDto> lignes;
    public String codePromo;
    public Long   idBonAchat;
}
