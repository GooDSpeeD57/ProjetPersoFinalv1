package fr.micromania.service;

import fr.micromania.dto.client.BonAchatResponse;
import fr.micromania.dto.client.FideliteDetailResponse;
import fr.micromania.dto.client.HistoriquePointsResponse;
import fr.micromania.entity.commande.Facture;

import java.util.List;

public interface FideliteService {

    FideliteDetailResponse getDetail(Long idClient);

    List<BonAchatResponse> getBonsAchat(Long idClient);

    List<HistoriquePointsResponse> getHistorique(Long idClient);

    void traiterFideliteApresFacture(Facture facture);
}
