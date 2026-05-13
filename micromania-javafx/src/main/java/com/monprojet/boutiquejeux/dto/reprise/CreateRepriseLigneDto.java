package com.monprojet.boutiquejeux.dto.reprise;

import java.math.BigDecimal;

/** Miroir de CreateRepriseeLigneRequest côté API. */
public class CreateRepriseLigneDto {
    public Long       idVariant;            // variant du produit (null si description libre)
    public String     descriptionLibre;     // description si produit non catalogué
    public Integer    quantite = 1;
    public String     etatGeneral;          // NEUF_SCELLE, TRES_BON_ETAT, BON_ETAT, ETAT_MOYEN, MAUVAIS_ETAT
    public BigDecimal prixEstimeUnitaire;
    public String     commentaires;
}
