package com.monprojet.boutiquejeux.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class CartItem {
    private Long produitId;
    private Long variantId;
    private String nom;
    private BigDecimal prix;
    private int quantite;
    /** Libellé de la garantie optionnelle (null si non souscrite). */
    private String garantieLabel;
    /** Prix de la garantie optionnelle (null si non souscrite). */
    private BigDecimal garantiePrix;
    /** ID du TypeGarantie sélectionné (null si aucune garantie). */
    private Long typeGarantieId;

    /** Prix garantie × quantité (0 si pas de garantie). */
    public BigDecimal getGarantieTotale() {
        return garantiePrix != null ? garantiePrix.multiply(BigDecimal.valueOf(quantite)) : BigDecimal.ZERO;
    }

    /** Constructeur sans garantie (rétro-compatibilité). */
    public CartItem(Long produitId, Long variantId, String nom, BigDecimal prix, int quantite) {
        this(produitId, variantId, nom, prix, quantite, null, null, null);
    }

    /** Constructeur avec garantie sans typeGarantieId (rétro-compatibilité). */
    public CartItem(Long produitId, Long variantId, String nom, BigDecimal prix, int quantite,
                    String garantieLabel, BigDecimal garantiePrix) {
        this(produitId, variantId, nom, prix, quantite, garantieLabel, garantiePrix, null);
    }
}