package fr.micromania.mapper;

import fr.micromania.dto.facture.*;
import fr.micromania.entity.commande.Facture;
import fr.micromania.entity.commande.LigneFacture;
import org.mapstruct.*;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.WARN)
public interface FactureMapper {

    @Mapping(target = "statutFacture",        source = "statutFacture.code")
    @Mapping(target = "contexteVente",        source = "contexteVente.code")
    @Mapping(target = "modePaiement",         source = "modePaiement.code")
    @Mapping(target = "magasin",              source = "magasin.nom")
    @Mapping(target = "nomClientAffiche",     expression = "java(facture.getClient() != null ? facture.getClient().getNom() + ' ' + facture.getClient().getPrenom() : facture.getNomClient())")
    @Mapping(target = "emailClientAffiche",   expression = "java(facture.getClient() != null ? facture.getClient().getEmail() : facture.getEmailClient())")
    @Mapping(target = "lignes",               source = "lignes")
    FactureResponse toResponse(Facture facture);

    @Mapping(target = "statutFacture", source = "statutFacture.code")
    @Mapping(target = "magasin",       source = "magasin.nom")
    FactureSummary toSummary(Facture facture);

    List<FactureSummary> toSummaryList(List<Facture> factures);

    @Mapping(target = "idVariant",        source = "variant.id")
    @Mapping(target = "nomCommercial",    source = "variant.nomCommercial")
    @Mapping(target = "sku",              source = "variant.sku")
    LigneFactureResponse toLigneFactureResponse(LigneFacture ligne);

    List<LigneFactureResponse> toLigneFactureResponseList(List<LigneFacture> lignes);
}
