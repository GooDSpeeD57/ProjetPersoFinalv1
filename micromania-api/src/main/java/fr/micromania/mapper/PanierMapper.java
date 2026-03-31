package fr.micromania.mapper;

import fr.micromania.dto.panier.*;
import fr.micromania.entity.commande.LignePanier;
import fr.micromania.entity.commande.Panier;
import org.mapstruct.*;
import java.math.BigDecimal;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.WARN)
public interface PanierMapper {

    @Mapping(target = "statutPanier",          source = "statutPanier.code")
    @Mapping(target = "canalVente",            source = "canalVente.code")
    @Mapping(target = "lignes",                source = "lignes")
    @Mapping(target = "sousTotal",             ignore = true)  // calculé dans le service
    @Mapping(target = "remise",                ignore = true)
    @Mapping(target = "total",                 ignore = true)
    PanierResponse toPanierResponse(Panier panier);

    @Mapping(target = "idVariant",      source = "variant.id")
    @Mapping(target = "nomCommercial",  source = "variant.nomCommercial")
    @Mapping(target = "sku",            source = "variant.sku")
    @Mapping(target = "imageUrl",       ignore = true)   // résolu via le service
    @Mapping(target = "montantLigne",   expression = "java(ligne.getPrixUnitaire().multiply(java.math.BigDecimal.valueOf(ligne.getQuantite())))")
    LignePanierResponse toLignePanierResponse(LignePanier ligne);

    List<LignePanierResponse> toLignePanierResponseList(List<LignePanier> lignes);
}
