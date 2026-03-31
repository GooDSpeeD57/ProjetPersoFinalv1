package fr.micromania.service;

import fr.micromania.dto.precommande.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PrecommandeService {

    PrecommandeResponse creer(CreatePrecommandeRequest request);

    PrecommandeResponse getById(Long id);

    PrecommandeResponse getByReference(String reference);

    Page<PrecommandeResponse> getByClient(Long idClient, Pageable pageable);

    Page<PrecommandeResponse> getByStatut(String statut, Pageable pageable);

    /** Convertit la précommande en commande réelle */
    fr.micromania.dto.commande.CommandeResponse convertirEnCommande(Long idPrecommande);

    void annuler(Long id, String motif);
}
