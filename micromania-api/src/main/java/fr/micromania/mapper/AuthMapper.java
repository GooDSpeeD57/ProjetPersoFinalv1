package fr.micromania.mapper;

import fr.micromania.dto.auth.RegisterRequest;
import fr.micromania.dto.client.CreateClientRequest;
import fr.micromania.entity.Client;
import org.mapstruct.*;

/**
 * Mapper dédié à l'authentification.
 * La conversion RegisterRequest → Client est partielle :
 * le mot de passe doit être encodé (BCrypt) AVANT d'appeler ce mapper,
 * puis injecté via @AfterMapping ou directement dans le service.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.WARN)
public interface AuthMapper {

    @Mapping(target = "id",                          ignore = true)
    @Mapping(target = "avatar",                      ignore = true)
    @Mapping(target = "numeroCarteFidelite",         ignore = true)
    @Mapping(target = "typeFidelite",                ignore = true)
    @Mapping(target = "actif",                       constant = "true")
    @Mapping(target = "deleted",                     constant = "false")
    @Mapping(target = "emailVerifie",                constant = "false")
    @Mapping(target = "compteActive",                constant = "false")
    @Mapping(target = "creeParEmploye",              constant = "false")
    @Mapping(target = "doitDefinirMotDePasse",       constant = "false")
    @Mapping(target = "demandeSuppression",          constant = "false")
    @Mapping(target = "rgpdConsentDate",             ignore = true)
    @Mapping(target = "rgpdConsentIp",               ignore = true)
    @Mapping(target = "dateVerificationEmail",       ignore = true)
    @Mapping(target = "tokenVerificationEmail",      ignore = true)
    @Mapping(target = "tokenVerificationExpireLe",   ignore = true)
    @Mapping(target = "employeCreateur",             ignore = true)
    @Mapping(target = "dateSuppression",             ignore = true)
    @Mapping(target = "dateCreation",                ignore = true)
    @Mapping(target = "dateModification",            ignore = true)
    @Mapping(target = "telephoneVerifie",                      ignore = true)
    @Mapping(target = "dateVerificationTelephone",             ignore = true)
    @Mapping(target = "tokenVerificationTelephone",            ignore = true)
    @Mapping(target = "tokenVerificationTelephoneExpireLe",    ignore = true)
    @Mapping(target = "dateDerniereConnexion",                 ignore = true)
    Client registerRequestToClient(RegisterRequest request);

    /** Même logique pour la création côté employé */
    @Mapping(target = "id",                          ignore = true)
    @Mapping(target = "avatar",                      ignore = true)
    @Mapping(target = "numeroCarteFidelite",           ignore = true)
    @Mapping(target = "typeFidelite",                ignore = true)
    @Mapping(target = "actif",                       constant = "true")
    @Mapping(target = "deleted",                     constant = "false")
    @Mapping(target = "emailVerifie",                constant = "false")
    @Mapping(target = "compteActive",                constant = "false")
    @Mapping(target = "creeParEmploye",              constant = "true")
    @Mapping(target = "doitDefinirMotDePasse",       constant = "true")
    @Mapping(target = "demandeSuppression",          constant = "false")
    @Mapping(target = "rgpdConsent",                 ignore = true)
    @Mapping(target = "rgpdConsentDate",             ignore = true)
    @Mapping(target = "rgpdConsentIp",               ignore = true)
    @Mapping(target = "dateVerificationEmail",       ignore = true)
    @Mapping(target = "tokenVerificationEmail",      ignore = true)
    @Mapping(target = "tokenVerificationExpireLe",   ignore = true)
    @Mapping(target = "motDePasse",                  ignore = true)
    @Mapping(target = "employeCreateur",             ignore = true)
    @Mapping(target = "dateSuppression",             ignore = true)
    @Mapping(target = "dateCreation",                ignore = true)
    @Mapping(target = "dateModification",            ignore = true)
    @Mapping(target = "telephoneVerifie",                      ignore = true)
    @Mapping(target = "dateVerificationTelephone",             ignore = true)
    @Mapping(target = "tokenVerificationTelephone",            ignore = true)
    @Mapping(target = "tokenVerificationTelephoneExpireLe",    ignore = true)
    @Mapping(target = "dateDerniereConnexion",                 ignore = true)
    Client createClientRequestToClient(CreateClientRequest request);
}
