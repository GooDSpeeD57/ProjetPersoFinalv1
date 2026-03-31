package fr.micromania.mapper;

import fr.micromania.dto.commande.*;
import fr.micromania.entity.commande.Commande;
import fr.micromania.entity.commande.LigneCommande;
import org.mapstruct.*;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.WARN)
public interface CommandeMapper {

    @Mapping(target = "statut",       source = "statutCommande.code")
    @Mapping(target = "canalVente",   source = "canalVente.code")
    @Mapping(target = "modeLivraison",source = "modeLivraison.code")
    @Mapping(target = "modePaiement", source = "modePaiement.code")
    @Mapping(target = "lignes",       source = "lignes")
    CommandeResponse toResponse(Commande commande);

    @Mapping(target = "statut",       source = "statutCommande.code")
    @Mapping(target = "modeLivraison",source = "modeLivraison.code")
    @Mapping(target = "nbArticles",   expression = "java(commande.getLignes().stream().mapToInt(fr.micromania.entity.commande.LigneCommande::getQuantite).sum())")
    CommandeSummary toSummary(Commande commande);

    List<CommandeSummary> toSummaryList(List<Commande> commandes);

    @Mapping(target = "idVariant",     source = "variant.id")
    @Mapping(target = "nomCommercial", source = "variant.nomCommercial")
    @Mapping(target = "sku",           source = "variant.sku")
    LigneCommandeResponse toLigneCommandeResponse(LigneCommande ligne);

    List<LigneCommandeResponse> toLigneCommandeResponseList(List<LigneCommande> lignes);
}
