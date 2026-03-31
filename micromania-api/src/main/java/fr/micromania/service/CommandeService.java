package fr.micromania.service;

import fr.micromania.dto.commande.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CommandeService {

    CommandeResponse creer(Long idClient, fr.micromania.dto.commande.CreateCommandeRequest request);

    CommandeResponse getById(Long id);

    CommandeResponse getByReference(String reference);

    Page<CommandeSummary> getByClient(Long idClient, Pageable pageable);

    Page<CommandeSummary> filter(Long idClient, String statut, Pageable pageable);

    CommandeResponse updateStatut(Long id, UpdateStatutCommandeRequest request);

    void annuler(Long id, String motif);
}
