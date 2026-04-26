package fr.micromania.service;

import fr.micromania.dto.client.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.time.LocalDate;

public interface ClientService {

    /** Identification sécurisée par identité (nom + prénom + date de naissance) */
    ClientResponse identifierParIdentite(String nom, String prenom, LocalDate dateNaissance);

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

    /** Simulation : marque l'e-mail comme vérifié sans token */
    void simulerVerificationEmail(Long idClient);

    /** Simulation : marque le téléphone comme vérifié */
    void simulerVerificationTelephone(Long idClient);

    /** Définit le mot de passe (flux création par employé) */
    void definirMotDePasse(Long idClient, String nouveauMotDePasse);

    void demanderSuppression(Long id);

    void softDelete(Long id);

    PointsFideliteResponse getPointsFidelite(Long idClient);

    ClientResponse souscrireUltimate(Long idClient);

    ClientResponse setMagasinFavori(Long idClient, Long idMagasin);

    ClientResponse removeMagasinFavori(Long idClient);
}
