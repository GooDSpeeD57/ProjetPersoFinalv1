package fr.micromania.mapper;

import fr.micromania.dto.catalog.*;
import fr.micromania.dto.referentiel.CodeDescriptionDto;
import fr.micromania.dto.referentiel.EditionDto;
import fr.micromania.dto.referentiel.PlatformeDto;
import fr.micromania.dto.referentiel.TauxTvaDto;
import fr.micromania.entity.catalog.*;
import fr.micromania.entity.referentiel.EditionProduit;
import fr.micromania.entity.referentiel.Plateforme;
import fr.micromania.entity.referentiel.TauxTva;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.math.BigDecimal;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.WARN)
public interface CatalogMapper {

    @Mapping(target = "categorie",   source = "categorie")
    @Mapping(target = "variants",    source = "variants")
    @Mapping(target = "images",      ignore = true)
    @Mapping(target = "screenshots", ignore = true)
    @Mapping(target = "videos",      ignore = true)
    @Mapping(target = "noteMoyenne", ignore = true)
    @Mapping(target = "nbAvis",      ignore = true)
    @Mapping(target = "avis",        ignore = true)
    ProduitResponse toProduitResponse(Produit produit);

    @Mapping(target = "produitId", source = "produit.id")
    ScreenshotDto toScreenshotDto(ProduitScreenshot screenshot);
    List<ScreenshotDto> toScreenshotDtoList(List<ProduitScreenshot> screenshots);

    @Mapping(target = "categorie",        source = "categorie.nom")
    @Mapping(target = "typeCategorie",    ignore = true)
    @Mapping(target = "plateforme",       ignore = true)
    @Mapping(target = "imageUrl",         ignore = true)
    @Mapping(target = "imageAlt",         ignore = true)
    @Mapping(target = "prixNeuf",         ignore = true)
    @Mapping(target = "prixOccasion",     ignore = true)
    @Mapping(target = "prixReprise",      ignore = true)
    @Mapping(target = "prixLocation",     ignore = true)
    @Mapping(target = "disponible",       ignore = true)
    @Mapping(target = "noteMoyenne",      ignore = true)
    @Mapping(target = "nbAvis",           ignore = true)
    @Mapping(target = "variantIdNeuf",    ignore = true)
    @Mapping(target = "variantIdOccasion",ignore = true)
    @Mapping(target = "variantIdLocation",ignore = true)
    ProduitSummary toProduitSummary(Produit produit);

    List<ProduitSummary> toProduitSummaryList(List<Produit> produits);

    @Mapping(target = "typeCategorie", source = "typeCategorie.code")
    CategorieResponse toCategorieResponse(Categorie categorie);

    @Mapping(target = "description", source = "description")
    CodeDescriptionDto toCategorieDto(Categorie categorie);

    @Mapping(target = "plateforme",    source = "plateforme")
    @Mapping(target = "formatProduit", source = "formatProduit.code")
    @Mapping(target = "statutProduit", source = "statutProduit.code")
    @Mapping(target = "edition",       source = "edition")
    @Mapping(target = "tauxTva",       source = "tauxTva")
    @Mapping(target = "prixNeuf",      ignore = true)
    @Mapping(target = "prixOccasion",  ignore = true)
    @Mapping(target = "prixReprise",   ignore = true)
    @Mapping(target = "prixLocation",  ignore = true)
    @Mapping(target = "images",        source = "images")
    ProduitVariantResponse toVariantResponse(ProduitVariant variant);

    List<ProduitVariantResponse> toVariantResponseList(List<ProduitVariant> variants);

    /** MapStruct utilise cette méthode pour EditionProduit → EditionDto */
    default EditionDto toEditionDto(EditionProduit e) {
        if (e == null) return null;
        return new EditionDto(e.getId(), e.getCode(), e.getLibelle());
    }

    @Mapping(target = "idVariant", source = "variant.id")
    PrixResponse toPrixResponse(ProduitPrix prix);

    @Mapping(target = "auteur", source = "client.pseudo")
    AvisProduitPublicResponse toAvisProduitPublicResponse(AvisProduit avisProduit);

    @Mapping(target = "idProduit", source = "produit.id")
    @Mapping(target = "statut", source = "statutAvis.code")
    AvisProduitClientResponse toAvisProduitClientResponse(AvisProduit avisProduit);

    @Mapping(target = "variantId", source = "variant.id")
    ProduitImageDto toImageDto(ProduitImage image);
    List<ProduitImageDto> toImageDtoList(List<ProduitImage> images);

    PlatformeDto toPlatformeDto(Plateforme plateforme);

    /**
     * Prix neuf à afficher sur la fiche produit (catalogue) :
     * prend le prixNeuf du premier prix actif d'un variant NEUF.
     */
    default BigDecimal prixNeuf(List<ProduitVariant> variants) {
        if (variants == null) return null;
        return variants.stream()
                .filter(ProduitVariant::isActif)
                .filter(v -> v.getStatutProduit() != null
                        && "NEUF".equalsIgnoreCase(v.getStatutProduit().getCode()))
                .flatMap(v -> v.getPrix() != null ? v.getPrix().stream() : java.util.stream.Stream.empty())
                .filter(ProduitPrix::isActif)
                .map(ProduitPrix::getPrixNeuf)
                .filter(p -> p != null)
                .min(BigDecimal::compareTo)
                .orElse(null);
    }

    /**
     * Prix occasion à afficher sur la fiche produit (catalogue) :
     * prend le prixOccasion du premier prix actif d'un variant OCCASION.
     */
    default BigDecimal prixOccasion(List<ProduitVariant> variants) {
        if (variants == null) return null;
        return variants.stream()
                .filter(ProduitVariant::isActif)
                .filter(v -> v.getStatutProduit() != null
                        && "OCCASION".equalsIgnoreCase(v.getStatutProduit().getCode()))
                .flatMap(v -> v.getPrix() != null ? v.getPrix().stream() : java.util.stream.Stream.empty())
                .filter(ProduitPrix::isActif)
                .map(ProduitPrix::getPrixOccasion)
                .filter(p -> p != null)
                .min(BigDecimal::compareTo)
                .orElse(null);
    }

    /**
     * Prix de reprise : valeur stockée dans produit_prix.prix_reprise,
     * renseignée manuellement par l'admin via l'interface bo.
     * Retourne le premier prix non null trouvé sur n'importe quel variant actif.
     */
    default BigDecimal prixReprise(List<ProduitVariant> variants) {
        if (variants == null) return null;
        return variants.stream()
                .filter(ProduitVariant::isActif)
                .flatMap(v -> v.getPrix() != null ? v.getPrix().stream() : java.util.stream.Stream.empty())
                .filter(ProduitPrix::isActif)
                .map(ProduitPrix::getPrixReprise)
                .filter(p -> p != null)
                .findFirst()
                .orElse(null);
    }

    /**
     * Prix location : prend le prixLocation du premier variant actif de statut LOCATION.
     */
    default BigDecimal prixLocation(List<ProduitVariant> variants) {
        if (variants == null) return null;
        return variants.stream()
                .filter(ProduitVariant::isActif)
                .filter(v -> v.getStatutProduit() != null
                        && "LOCATION".equalsIgnoreCase(v.getStatutProduit().getCode()))
                .flatMap(v -> v.getPrix() != null ? v.getPrix().stream() : java.util.stream.Stream.empty())
                .filter(ProduitPrix::isActif)
                .map(ProduitPrix::getPrixLocation)
                .filter(p -> p != null)
                .min(BigDecimal::compareTo)
                .orElse(null);
    }

    /** Retourne l'ID du premier variant actif correspondant au statut donné (NEUF / OCCASION / LOCATION). */
    default Long variantIdParStatut(List<ProduitVariant> variants, String statut) {
        if (variants == null) return null;
        return variants.stream()
                .filter(ProduitVariant::isActif)
                .filter(v -> v.getStatutProduit() != null
                        && statut.equalsIgnoreCase(v.getStatutProduit().getCode()))
                .map(ProduitVariant::getId)
                .findFirst()
                .orElse(null);
    }

    @Mapping(target = "taux", source = "taux")
    TauxTvaDto toTauxTvaDto(TauxTva tauxTva);
}
