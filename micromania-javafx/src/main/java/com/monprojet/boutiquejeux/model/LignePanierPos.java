package com.monprojet.boutiquejeux.model;
import javafx.beans.property.*;
import java.math.BigDecimal;
public class LignePanierPos {
    private final Long idVariant;
    private final StringProperty nomProduit;
    private final StringProperty plateforme;
    private final StringProperty type;   // NEUF / OCCASION
    private final IntegerProperty quantite;
    private final BigDecimal prixUnitaire;

    public LignePanierPos(Long idVariant, String nom, String plateforme, String type, int qte, BigDecimal prix) {
        this.idVariant   = idVariant;
        this.nomProduit  = new SimpleStringProperty(nom);
        this.plateforme  = new SimpleStringProperty(plateforme != null ? plateforme : "");
        this.type        = new SimpleStringProperty(type);
        this.quantite    = new SimpleIntegerProperty(qte);
        this.prixUnitaire = prix;
    }

    public Long      getIdVariant()   { return idVariant; }
    public String    getNomProduit()  { return nomProduit.get(); }
    public String    getPlateforme()  { return plateforme.get(); }
    public String    getType()        { return type.get(); }
    public int       getQuantite()    { return quantite.get(); }
    public void      setQuantite(int q){ quantite.set(q); }
    public BigDecimal getPrixUnitaire(){ return prixUnitaire; }
    public String    getPrixAffiche() { return prixUnitaire.toPlainString() + " €"; }
    public BigDecimal getSousTotal()  { return prixUnitaire.multiply(BigDecimal.valueOf(quantite.get())); }
}
