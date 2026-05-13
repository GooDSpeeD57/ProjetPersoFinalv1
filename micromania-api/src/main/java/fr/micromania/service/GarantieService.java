package fr.micromania.service;

import fr.micromania.dto.garantie.ExtensionGarantieRequest;
import fr.micromania.dto.garantie.ExtensionGarantieResponse;
import fr.micromania.dto.garantie.GarantieResponse;

import java.util.List;

/**
 * Gestion des garanties : consultation et ajout d'extensions.
 */
public interface GarantieService {

    List<GarantieResponse> getByClientId(Long clientId);

    GarantieResponse getByVenteUniteId(Long idVenteUnite);

    GarantieResponse getById(Long id);

    ExtensionGarantieResponse ajouterExtension(Long idGarantie, ExtensionGarantieRequest request);
}
