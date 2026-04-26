package fr.micromania.mapper;

import fr.micromania.dto.panier.*;
import fr.micromania.entity.commande.LignePanier;
import fr.micromania.entity.commande.Panier;
import org.mapstruct.*;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.WARN)
public interface PanierMapper {

    @Mapping(target = "statutPanier",          source = "statutPanier.code")
    @Mapping(target = "canalVente",            source = "canalVente.code")
    @Mapping(target = "lignes",                source = "lignes")
    @Mapping(target = "sousTotal",             ignore = true)
    @Mapping(target = "remise",                ignore = true)
    @Mapping(target = "total",                 ignore = true)
    PanierResponse toPanierResponse(Panier panier);

    @Mapping(target = "idVariant",       source = "variant.id")
    @Mapping(target = "nomCommercial",   source = "variant.nomCommercial")
    @Mapping(target = "sku",             source = "variant.sku")
    @Mapping(target = "imageUrl",        ignore = true)
    @Mapping(target = "montantLigne",    expression = "java(ligne.getPrixUnitaire().multiply(java.math.BigDecimal.valueOf(ligne.getQuantite())))")
    @Mapping(target = "typeGarantieId",  expression = "java(ligne.getTypeGarantie() != null ? ligne.getTypeGarantie().getId() : null)")
    @Mapping(target = "garantieLabel",   expression = "java(ligne.getTypeGarantie() != null ? ligne.getTypeGarantie().getDescription() : null)")
    @Mapping(target = "garantiePrix",    expression = "java(ligne.getTypeGarantie() != null ? ligne.getTypeGarantie().getPrixExtension() : null)")
    LignePanierResponse toLignePanierResponse(LignePanier ligne);

    List<LignePanierResponse> toLignePanierResponseList(List<LignePanier> lignes);
}
