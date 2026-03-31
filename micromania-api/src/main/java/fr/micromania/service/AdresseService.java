package fr.micromania.service;

import fr.micromania.dto.client.AdresseRequest;
import fr.micromania.dto.client.AdresseResponse;
import java.util.List;

public interface AdresseService {

    List<AdresseResponse> getByClient(Long idClient);

    AdresseResponse ajouter(Long idClient, AdresseRequest request);

    AdresseResponse modifier(Long idAdresse, Long idClient, AdresseRequest request);

    void supprimer(Long idAdresse, Long idClient);

    void setDefaut(Long idAdresse, Long idClient);
}
