package fr.micromania.service;

import fr.micromania.dto.magasin.MagasinProximiteResponse;
import fr.micromania.dto.magasin.MagasinPublicResponse;

import java.util.List;

public interface MagasinService {

    List<MagasinPublicResponse> getMagasinsActifs(String q);

    MagasinPublicResponse getMagasinActifById(Long idMagasin);

    List<MagasinProximiteResponse> getMagasinsProches(Long idClient, Long idAdresse, Integer limit);
}
