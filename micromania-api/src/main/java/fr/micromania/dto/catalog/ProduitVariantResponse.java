package fr.micromania.dto.catalog;

import fr.micromania.dto.referentiel.PlatformeDto;
import fr.micromania.dto.referentiel.TauxTvaDto;
import java.math.BigDecimal;

public record ProduitVariantResponse(
    Long id,
    String sku,
    String ean,
    String nomCommercial,
    PlatformeDto plateforme,
    String formatProduit,
    String statutProduit,
    String edition,
    String couleur,
    String langueVente,
    boolean scelle,
    boolean estDemat,
    boolean estTcgUnitaire,
    boolean estReprise,
    boolean necessiteNumeroSerie,
    TauxTvaDto tauxTva,
    BigDecimal prixWeb,
    BigDecimal prixMagasin,
    boolean actif
) {}
