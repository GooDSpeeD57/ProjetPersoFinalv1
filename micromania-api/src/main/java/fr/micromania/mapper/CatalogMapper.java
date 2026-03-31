package fr.micromania.mapper;

import fr.micromania.dto.catalog.*;
import fr.micromania.dto.referentiel.CodeDescriptionDto;
import fr.micromania.dto.referentiel.PlatformeDto;
import fr.micromania.dto.referentiel.TauxTvaDto;
import fr.micromania.entity.catalog.*;
import fr.micromania.entity.referentiel.Plateforme;
import fr.micromania.entity.referentiel.TauxTva;
import fr.micromania.entity.referentiel.TypeCategorie;
import org.mapstruct.*;
import java.math.BigDecimal;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.WARN)
public interface CatalogMapper {

    // ── Produit ──────────────────────────────────────────────
    @Mapping(target = "categorie", source = "categorie")
    @Mapping(target = "variants",  source = "variants")
    @Mapping(target = "images",    source = "images")
    ProduitResponse toProduitResponse(Produit produit);

    @Mapping(target = "categorie",   source = "categorie.nom")
    @Mapping(target = "imageUrl",    ignore = true)   // résolu dans le service (image principale)
    @Mapping(target = "imageAlt",    ignore = true)
    @Mapping(target = "prixMinimal", ignore = true)   // résolu dans le service
    @Mapping(target = "disponible",  ignore = true)   // résolu via stock
    ProduitSummary toProduitSummary(Produit produit);

    List<ProduitSummary> toProduitSummaryList(List<Produit> produits);

    // ── Catégorie ─────────────────────────────────────────────
    @Mapping(target = "typeCategorie", source = "typeCategorie.code")
    CategorieResponse toCategorieResponse(Categorie categorie);

    @Mapping(target = "description", source = "description")
    CodeDescriptionDto toCategorieDto(Categorie categorie);

    // ── Variant ───────────────────────────────────────────────
    @Mapping(target = "plateforme",    source = "plateforme")
    @Mapping(target = "formatProduit", source = "formatProduit.code")
    @Mapping(target = "statutProduit", source = "statutProduit.code")
    @Mapping(target = "tauxTva",       source = "tauxTva")
    @Mapping(target = "prixWeb",       ignore = true)     // résolu dans le service
    @Mapping(target = "prixMagasin",   ignore = true)
    ProduitVariantResponse toVariantResponse(ProduitVariant variant);

    List<ProduitVariantResponse> toVariantResponseList(List<ProduitVariant> variants);

    @Mapping(target = "idVariant",  source = "variant.id")
    @Mapping(target = "canalVente", source = "canalVente.code")
    PrixResponse toPrixResponse(ProduitPrix prix);

    ProduitImageDto toImageDto(ProduitImage image);
    List<ProduitImageDto> toImageDtoList(List<ProduitImage> images);

    PlatformeDto toPlatformeDto(Plateforme plateforme);

    @Mapping(target = "taux", source = "taux")
    TauxTvaDto toTauxTvaDto(TauxTva tauxTva);

    default BigDecimal prixMinimal(List<ProduitVariant> variants) {
        if (variants == null) return null;
        return variants.stream()
            .flatMap(v -> v.getPrix() != null
                ? v.getPrix().stream().filter(p -> p.isActif())
                : java.util.stream.Stream.empty())
            .map(ProduitPrix::getPrix)
            .min(BigDecimal::compareTo)
            .orElse(null);
    }
}
