package fr.micromania.service;

import fr.micromania.dto.reprise.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RepriseService {

    RepriseResponse creer(CreateRepriseRequest request);

    RepriseResponse getById(Long id);

    RepriseResponse getByReference(String reference);

    Page<RepriseResponse> filter(Long idMagasin, String statut, Long idClient, Pageable pageable);

    RepriseResponse validerLigne(Long idReprise, ValiderRepriseLigneRequest request);

    RepriseResponse valider(Long idReprise);

    RepriseResponse refuser(Long idReprise, String motif);
}
