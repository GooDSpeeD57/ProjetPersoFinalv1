package fr.micromania.dto.catalog;

import fr.micromania.dto.referentiel.CodeDescriptionDto;
import java.time.LocalDate;
import java.util.List;

public record ProduitResponse(
    Long id,
    String nom,
    String slug,
    String description,
    String resumeCourt,
    LocalDate dateSortie,
    String editeur,
    String constructeur,
    Integer pegi,
    String marque,
    String niveauAccesMin,
    String langue,
    boolean misEnAvant,
    CodeDescriptionDto categorie,
    List<ProduitVariantResponse> variants,
    List<ProduitImageDto> images,
    Double noteMoyenne,
    long nbAvis,
    List<AvisProduitPublicResponse> avis
) {}
