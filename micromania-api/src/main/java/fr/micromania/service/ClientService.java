package fr.micromania.service;

import fr.micromania.dto.client.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ClientService {

    /** Crée un client depuis l'inscription publique */
    ClientResponse creerDepuisInscription(fr.micromania.dto.auth.RegisterRequest request);

    /** Crée un client depuis le back-office employé */
    ClientResponse creerParEmploye(CreateClientRequest request, Long idEmployeCreateur);

    ClientResponse getById(Long id);

    ClientResponse getByPseudo(String pseudo);

    Page<ClientSummary> search(String query, Pageable pageable);

    ClientResponse update(Long id, UpdateClientRequest request);

    /** Valide le token e-mail et active le compte */
    void verifierEmail(String token);

    /** Définit le mot de passe (flux création par employé) */
    void definirMotDePasse(Long idClient, String nouveauMotDePasse);

    void demanderSuppression(Long id);

    void softDelete(Long id);

    PointsFideliteResponse getPointsFidelite(Long idClient);
}
