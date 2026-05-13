package fr.micromania.service;

import fr.micromania.dto.entrepot.EntrepotResponse;

import java.util.List;

/**
 * Consultation des entrepôts.
 */
public interface EntrepotService {

    List<EntrepotResponse> lister();

    EntrepotResponse getById(Long id);
}
