package fr.micromania.service;

import fr.micromania.dto.sav.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SavService {

    DossierSavResponse ouvrir(CreateDossierSavRequest request);

    DossierSavResponse getById(Long id);

    DossierSavResponse getByReference(String reference);

    Page<DossierSavResponse> filter(String statut, Long idEmploye, Pageable pageable);

    DossierSavResponse update(Long id, UpdateDossierSavRequest request);

    DossierSavResponse cloturer(Long id, String solution);
}
