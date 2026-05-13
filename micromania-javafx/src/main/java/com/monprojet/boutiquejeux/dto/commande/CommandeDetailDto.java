package com.monprojet.boutiquejeux.dto.commande;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.math.BigDecimal;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CommandeDetailDto {
    public Long                  id;
    public String                referenceCommande;
    public String                statut;
    public String                canalVente;
    public String                modeLivraison;
    public String                modePaiement;
    public BigDecimal            sousTotal;
    public BigDecimal            montantRemise;
    public BigDecimal            fraisLivraison;
    public BigDecimal            montantTotal;
    public String                commentaireClient;
    public String                dateCommande;
    public String                datePaiement;
    public String                dateExpedition;
    public String                dateLivraisonPrevue;
    public String                dateLivraisonReelle;
    public List<LigneCommandeDto> lignes;
}
