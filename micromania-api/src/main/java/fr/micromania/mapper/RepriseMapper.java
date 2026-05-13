package fr.micromania.mapper;

import fr.micromania.dto.reprise.*;
import fr.micromania.entity.Reprise;
import fr.micromania.entity.RepriseeLigne;
import org.mapstruct.*;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.WARN)
public interface RepriseMapper {

    @Mapping(target = "statut",            source = "statutReprise.code")
    @Mapping(target = "modeCompensation",  source = "modeCompensationReprise.code")
    @Mapping(target = "client",            expression = "java(reprise.getClient() != null ? reprise.getClient().getPseudo() : \"Anonyme\")")
    @Mapping(target = "employe",           expression = "java(reprise.getEmploye().getPrenom() + ' ' + reprise.getEmploye().getNom())")
    @Mapping(target = "magasin",           source = "magasin.nom")
    @Mapping(target = "lignes",            source = "lignes")
    @Mapping(target = "montantTotalAvoir", ignore = true)  // calculé dans le service
    RepriseResponse toResponse(Reprise reprise);

    List<RepriseResponse> toResponseList(List<Reprise> reprises);

    @Mapping(target = "idVariant",     source = "variant.id")
    @Mapping(target = "nomCommercial", expression = "java(ligne.getVariant() != null ? ligne.getVariant().getNomCommercial() : ligne.getDescriptionLibre())")
    RepriseLigneResponse toLigneResponse(RepriseeLigne ligne);
}
