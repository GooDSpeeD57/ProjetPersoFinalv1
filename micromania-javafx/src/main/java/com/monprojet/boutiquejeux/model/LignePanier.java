package com.monprojet.boutiquejeux.model;

import javafx.beans.property.*;

public class LignePanier {

    private final LongProperty    variantId           = new SimpleLongProperty();
    private final StringProperty  nomProduit          = new SimpleStringProperty();
    private final StringProperty  plateforme          = new SimpleStringProperty();
    private final StringProperty  type                = new SimpleStringProperty();
    private final IntegerProperty quantite            = new SimpleIntegerProperty();
    private final DoubleProperty  prix                = new SimpleDoubleProperty();
    private final StringProperty  garantieLabel       = new SimpleStringProperty();
    private Long                  typeGarantieId      = null;

    public LignePanier(Long variantId, String nom, String plateforme, String type, int quantite, double prix) {
        this.variantId.set(variantId);
        this.nomProduit.set(nom);
        this.plateforme.set(plateforme != null ? plateforme : "");
        this.type.set(type);
        this.quantite.set(quantite);
        this.prix.set(prix);
    }

    // ── Getters ──────────────────────────────────────────────────
    public Long    getVariantId()       { return variantId.get(); }
    public String  getNomProduit()      { return nomProduit.get(); }
    public String  getPlateforme()      { return plateforme.get(); }
    public String  getType()            { return type.get(); }
    public int     getQuantite()        { return quantite.get(); }
    public double  getPrix()            { return prix.get(); }
    public Long    getTypeGarantieId()  { return typeGarantieId; }
    public String  getGarantieLabel()   { return garantieLabel.get(); }
    public boolean isExtensionGarantie(){ return typeGarantieId != null; }

    public String getPrixAffiche() {
        return String.format("%.2f €", prix.get() * quantite.get());
    }

    // ── Setters ──────────────────────────────────────────────────
    public void setQuantite(int q) { quantite.set(q); }
    public void setGarantie(Long id, String label) {
        this.typeGarantieId = id;
        this.garantieLabel.set(label != null ? label : "");
    }

    // ── Properties (pour TableView binding) ──────────────────────
    public LongProperty    variantIdProperty()    { return variantId; }
    public StringProperty  nomProduitProperty()   { return nomProduit; }
    public StringProperty  plateformeProperty()   { return plateforme; }
    public StringProperty  typeProperty()         { return type; }
    public IntegerProperty quantiteProperty()     { return quantite; }
    public DoubleProperty  prixProperty()         { return prix; }
    public StringProperty  garantieLabelProperty(){ return garantieLabel; }
    /** Rétro-compatibilité TableView boolean — conservé pour le binding FXML existant */
    public BooleanProperty extensionGarantieProperty() {
        BooleanProperty bp = new SimpleBooleanProperty(typeGarantieId != null);
        return bp;
    }
}
