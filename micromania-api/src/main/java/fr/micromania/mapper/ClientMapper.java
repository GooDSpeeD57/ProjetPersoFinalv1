package fr.micromania.mapper;

import fr.micromania.dto.client.*;
import fr.micromania.entity.Adresse;
import fr.micromania.entity.Avatar;
import fr.micromania.entity.Client;
import fr.micromania.entity.PointsFidelite;
import org.mapstruct.*;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.WARN)
public interface ClientMapper {

    @Mapping(target = "typeFidelite",       source = "typeFidelite.code")
    @Mapping(target = "numeroCarteFidelite", source = "numeroCarteFidelite")
    @Mapping(target = "soldePoints",        ignore = true)
    @Mapping(target = "avatar",             source = "avatar")
    @Mapping(target = "telephoneVerifie",   source = "telephoneVerifie")
    @Mapping(target = "dateDerniereConnexion", source = "dateDerniereConnexion")
    ClientResponse toResponse(Client client);

    default ClientResponse toResponse(Client client, PointsFidelite points) {
        ClientResponse base = toResponse(client);
        return new ClientResponse(
                base.id(), base.pseudo(), base.nom(), base.prenom(),
                base.dateNaissance(), base.email(), base.telephone(),
                base.typeFidelite(), base.numeroCarteFidelite(),
                points != null ? points.getSoldePoints() : 0,
                base.avatar(), base.emailVerifie(), base.telephoneVerifie(),
                base.compteActive(), base.dateDerniereConnexion(),
                base.dateCreation()
        );
    }

    /** Entity → Summary (liste, recherche) */
    @Mapping(target = "typeFidelite", source = "typeFidelite.code")
    ClientSummary toSummary(Client client);

    List<ClientSummary> toSummaryList(List<Client> clients);

    /** Avatar */
    AvatarDto toAvatarDto(Avatar avatar);

    /** Adresse */
    @Mapping(target = "typeAdresse", source = "typeAdresse.code")
    AdresseResponse toAdresseResponse(Adresse adresse);

    List<AdresseResponse> toAdresseResponseList(List<Adresse> adresses);
}
