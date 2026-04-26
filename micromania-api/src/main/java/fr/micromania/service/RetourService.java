package fr.micromania.service;

import fr.micromania.dto.retour.*;

import java.util.List;

public interface RetourService {

    RetourResponse creer(CreateRetourRequest request);

    RetourResponse getById(Long id);

    List<RetourResponse> getByFacture(Long idFacture);

    RetourResponse updateStatut(Long id, UpdateStatutRetourRequest request);
}
