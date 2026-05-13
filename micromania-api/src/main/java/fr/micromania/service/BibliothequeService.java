package fr.micromania.service;

import fr.micromania.dto.bibliotheque.BibliothequeResponse;

import java.util.List;

/**
 * Consultation de la bibliothèque numérique d'un client.
 */
public interface BibliothequeService {

    List<BibliothequeResponse> getByClientId(Long idClient);
}
