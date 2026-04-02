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

    @Mapping(target = "categorie", source = "categorie")
    @Mapping(target = "variants",  source = "variants")
    @Mapping(target = "images",    source = "images")
    ProduitResponse toProduitResponse(Produit produit);

    @Mapping(target = "categorie",   source = "categorie.nom")
    @Mapping(target = "imageUrl",    ignore = true)
    @Mapping(target = "imageAlt",    ignore = true)
    @Mapping(target = "prixNeuf", ignore = true)
    @Mapping(target = "prixOccasion", ignore = true)
    @Mapping(target = "disponible",  ignore = true)
    ProduitSummary toProduitSummary(Produit produit);

    List<ProduitSummary> toProduitSummaryList(List<Produit> produits);

    @Mapping(target = "typeCategorie", source = "typeCategorie.code")
    CategorieResponse toCategorieResponse(Categorie categorie);

    @Mapping(target = "description", source = "description")
    CodeDescriptionDto toCategorieDto(Categorie categorie);

    // ── Variant ───────────────────────────────────────────────
    @Mapping(target = "plateforme",    source = "plateforme")
    @Mapping(target = "formatProduit", source = "formatProduit.code")
    @Mapping(target = "statutProduit", source = "statutProduit.code")
    @Mapping(target = "tauxTva",       source = "tauxTva")
    @Mapping(target = "prixWeb",       ignore = true)
    @Mapping(target = "prixMagasin",   ignore = true)
    ProduitVariantResponse toVariantResponse(ProduitVariant variant);

    List<ProduitVariantResponse> toVariantResponseList(List<ProduitVariant> variants);

    @Mapping(target = "idVariant",  source = "variant.id")
    @Mapping(target = "canalVente", source = "canalVente.code")
    PrixResponse toPrixResponse(ProduitPrix prix);

    ProduitImageDto toImageDto(ProduitImage image);
    List<ProduitImageDto> toImageDtoList(List<ProduitImage> images);

    PlatformeDto toPlatformeDto(Plateforme plateforme);

    default BigDecimal prixParStatut(List<ProduitVariant> variants, String statut) {
        if (variants == null) return null;

        return variants.stream()
                .filter(ProduitVariant::isActif)
                .filter(v -> v.getStatutProduit() != null
                        && statut.equalsIgnoreCase(v.getStatutProduit().getCode()))
                .flatMap(v -> v.getPrix() != null ? v.getPrix().stream() : java.util.stream.Stream.empty())
                .filter(ProduitPrix::isActif)
                .filter(p -> p.getCanalVente() != null
                        && "WEB".equalsIgnoreCase(p.getCanalVente().getCode()))
                .map(ProduitPrix::getPrix)
                .min(BigDecimal::compareTo)
                .orElse(null);
    }

    default BigDecimal prixNeuf(List<ProduitVariant> variants) {
        return prixParStatut(variants, "NEUF");
    }

    default BigDecimal prixOccasion(List<ProduitVariant> variants) {
        return prixParStatut(variants, "OCCASION");
    }
    @Mapping(target = "taux", source = "taux")
    TauxTvaDto toTauxTvaDto(TauxTva tauxTva);
}
