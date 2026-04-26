package com.monprojet.boutiquejeux.dto.commande;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CommandeSummaryDto {
    public Long       id;
    public String     referenceCommande;
    public String     statut;
    public String     modeLivraison;
    public BigDecimal montantTotal;
    public int        nbArticles;
    public String     dateCommande;

    // Getters requis par PropertyValueFactory (JavaFX TableView)
    public Long       getId()                 { return id; }
    public String     getReferenceCommande()  { return referenceCommande; }
    public String     getStatut()             { return statut; }
    public String     getModeLivraison()      { return modeLivraison; }
    public BigDecimal getMontantTotal()       { return montantTotal; }
    public int        getNbArticles()         { return nbArticles; }
    public String     getDateCommande()       { return dateCommande; }
}
