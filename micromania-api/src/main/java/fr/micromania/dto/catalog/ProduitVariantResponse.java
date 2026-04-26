package fr.micromania.dto.catalog;

import fr.micromania.dto.referentiel.EditionDto;
import fr.micromania.dto.referentiel.PlatformeDto;
import fr.micromania.dto.referentiel.TauxTvaDto;
import java.math.BigDecimal;
import java.util.List;

public record ProduitVariantResponse(
    Long id,
    String sku,
    String ean,
    String nomCommercial,
    PlatformeDto plateforme,
    String formatProduit,
    String statutProduit,
    EditionDto edition,
    String couleur,
    String langueVente,
    boolean scelle,
    boolean estDemat,
    boolean estTcgUnitaire,
    boolean estReprise,
    boolean necessiteNumeroSerie,
    TauxTvaDto tauxTva,
    BigDecimal prixNeuf,
    BigDecimal prixOccasion,
    BigDecimal prixReprise,
    BigDecimal prixLocation,
    boolean actif,
    List<ProduitImageDto> images
) {}
