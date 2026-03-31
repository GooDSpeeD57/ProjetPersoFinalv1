package fr.micromania.mapper;

import fr.micromania.dto.tcg.*;
import fr.micromania.entity.tcg.TcgCarteInventaire;
import org.mapstruct.*;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.WARN)
public interface TcgMapper {

    @Mapping(target = "jeu",           source = "tcgCarteReference.tcgExtension.tcgJeu.nom")
    @Mapping(target = "extension",     source = "tcgCarteReference.tcgExtension.nom")
    @Mapping(target = "codeExtension", source = "tcgCarteReference.tcgExtension.code")
    @Mapping(target = "nomCarte",      source = "tcgCarteReference.nomCarte")
    @Mapping(target = "numeroCarte",   source = "tcgCarteReference.numeroCarte")
    @Mapping(target = "rarete",        source = "tcgCarteReference.rarete")
    @Mapping(target = "etatCarte",     source = "etatCarteTcg.libelle")
    @Mapping(target = "magasin",       source = "magasin.nom")
    @Mapping(target = "idInventaire",  source = "id")
    TcgCarteResponse toResponse(TcgCarteInventaire inventaire);

    @Mapping(target = "nomCarte",      source = "tcgCarteReference.nomCarte")
    @Mapping(target = "extension",     source = "tcgCarteReference.tcgExtension.nom")
    @Mapping(target = "rarete",        source = "tcgCarteReference.rarete")
    @Mapping(target = "etatCarte",     source = "etatCarteTcg.libelle")
    @Mapping(target = "magasin",       source = "magasin.nom")
    @Mapping(target = "idInventaire",  source = "id")
    TcgCarteSummary toSummary(TcgCarteInventaire inventaire);

    List<TcgCarteSummary> toSummaryList(List<TcgCarteInventaire> inventaires);
}
