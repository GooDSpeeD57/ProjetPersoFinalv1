package fr.micromania.mapper;

import fr.micromania.dto.precommande.*;
import fr.micromania.entity.commande.Precommande;
import fr.micromania.entity.commande.PrecommandeLigne;
import org.mapstruct.*;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.WARN)
public interface PrecommandeMapper {

    @Mapping(target = "statut",       source = "statutPrecommande.code")
    @Mapping(target = "canalVente",   source = "canalVente.code")
    @Mapping(target = "modePaiement", source = "modePaiement.code")
    @Mapping(target = "clientPseudo", source = "client.pseudo")
    @Mapping(target = "lignes",       source = "lignes")
    PrecommandeResponse toResponse(Precommande precommande);

    List<PrecommandeResponse> toResponseList(List<Precommande> precommandes);

    @Mapping(target = "idVariant",     source = "variant.id")
    @Mapping(target = "nomCommercial", source = "variant.nomCommercial")
    PrecommandeLigneResponse toLigneResponse(PrecommandeLigne ligne);
}
