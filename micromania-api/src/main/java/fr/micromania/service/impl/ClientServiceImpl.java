package fr.micromania.service.impl;

import fr.micromania.dto.auth.RegisterRequest;
import fr.micromania.dto.client.*;
import fr.micromania.entity.Client;
import fr.micromania.entity.PointsFidelite;
import fr.micromania.entity.referentiel.TypeFidelite;
import fr.micromania.mapper.AuthMapper;
import fr.micromania.mapper.ClientMapper;
import fr.micromania.repository.ClientRepository;
import fr.micromania.repository.PointsFideliteRepository;
import fr.micromania.service.ClientService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ClientServiceImpl implements ClientService {

    private final ClientRepository clientRepository;
    private final PointsFideliteRepository pointsRepository;
    private final ClientMapper clientMapper;
    private final AuthMapper authMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public ClientResponse creerDepuisInscription(RegisterRequest request) {
        validerUniciteEmail(request.email());
        validerUnicitePseudo(request.pseudo());

        Client client = authMapper.registerRequestToClient(request);
        client.setMotDePasse(passwordEncoder.encode(request.motDePasse()));
        client.setRgpdConsentDate(request.rgpdConsent() ? LocalDateTime.now() : null);
        client.setTokenVerificationEmail(UUID.randomUUID().toString());
        client.setTokenVerificationExpireLe(LocalDateTime.now().plusHours(24));
        // typeFidelite NORMAL par défaut — résolu par le service d'appel ou via lookup
        client = clientRepository.save(client);

        log.info("Nouveau client créé : pseudo={}, email={}", client.getPseudo(), client.getEmail());
        PointsFidelite points = pointsRepository.findByClientId(client.getId()).orElse(null);
        return clientMapper.toResponse(client, points);
    }

    @Override
    @Transactional
    public ClientResponse creerParEmploye(CreateClientRequest request, Long idEmployeCreateur) {
        validerUniciteEmail(request.email());
        validerUnicitePseudo(request.pseudo());

        Client client = authMapper.createClientRequestToClient(request);
        client.setMotDePasse(passwordEncoder.encode(UUID.randomUUID().toString())); // temporaire
        client.setTokenVerificationEmail(UUID.randomUUID().toString());
        client.setTokenVerificationExpireLe(LocalDateTime.now().plusDays(7));
        client = clientRepository.save(client);

        log.info("Client créé par employé {} : email={}", idEmployeCreateur, client.getEmail());
        return clientMapper.toResponse(client, null);
    }

    @Override
    public ClientResponse getById(Long id) {
        Client client = clientRepository.findByIdAndDeletedFalse(id)
            .orElseThrow(() -> new EntityNotFoundException("Client introuvable : " + id));
        PointsFidelite points = pointsRepository.findByClientId(id).orElse(null);
        return clientMapper.toResponse(client, points);
    }

    @Override
    public ClientResponse getByPseudo(String pseudo) {
        Client client = clientRepository.findByPseudoAndDeletedFalse(pseudo)
            .orElseThrow(() -> new EntityNotFoundException("Client introuvable : " + pseudo));
        PointsFidelite points = pointsRepository.findByClientId(client.getId()).orElse(null);
        return clientMapper.toResponse(client, points);
    }

    @Override
    public Page<ClientSummary> search(String query, Pageable pageable) {
        return clientRepository.search(query, pageable)
            .map(clientMapper::toSummary);
    }

    @Override
    @Transactional
    public ClientResponse update(Long id, UpdateClientRequest request) {
        Client client = clientRepository.findByIdAndDeletedFalse(id)
            .orElseThrow(() -> new EntityNotFoundException("Client introuvable : " + id));

        if (request.pseudo() != null && !request.pseudo().equals(client.getPseudo())) {
            validerUnicitePseudo(request.pseudo());
            client.setPseudo(request.pseudo());
        }
        if (request.email() != null && !request.email().equals(client.getEmail())) {
            validerUniciteEmail(request.email());
            client.setEmail(request.email());
            client.setEmailVerifie(false);
        }
        if (request.nom()        != null) client.setNom(request.nom());
        if (request.prenom()     != null) client.setPrenom(request.prenom());
        if (request.telephone()  != null) client.setTelephone(request.telephone());

        client = clientRepository.save(client);
        PointsFidelite points = pointsRepository.findByClientId(id).orElse(null);
        return clientMapper.toResponse(client, points);
    }

    @Override
    @Transactional
    public void verifierEmail(String token) {
        Client client = clientRepository.findByTokenVerificationEmail(token)
            .orElseThrow(() -> new EntityNotFoundException("Token invalide ou expiré"));

        if (client.getTokenVerificationExpireLe().isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("Token de vérification expiré");
        }
        client.setEmailVerifie(true);
        client.setDateVerificationEmail(LocalDateTime.now());
        client.setTokenVerificationEmail(null);
        client.setTokenVerificationExpireLe(null);
        client.setCompteActive(true);
        clientRepository.save(client);
    }

    @Override
    @Transactional
    public void definirMotDePasse(Long idClient, String nouveauMotDePasse) {
        Client client = clientRepository.findByIdAndDeletedFalse(idClient)
            .orElseThrow(() -> new EntityNotFoundException("Client introuvable : " + idClient));
        client.setMotDePasse(passwordEncoder.encode(nouveauMotDePasse));
        client.setDoitDefinirMotDePasse(false);
        client.setCompteActive(true);
        clientRepository.save(client);
    }

    @Override
    @Transactional
    public void demanderSuppression(Long id) {
        Client client = clientRepository.findByIdAndDeletedFalse(id)
            .orElseThrow(() -> new EntityNotFoundException("Client introuvable : " + id));
        client.setDemandeSuppression(true);
        client.setDateSuppression(LocalDateTime.now().plusDays(30)); // 30j de rétention RGPD
        clientRepository.save(client);
    }

    @Override
    @Transactional
    public void softDelete(Long id) {
        clientRepository.findByIdAndDeletedFalse(id)
            .orElseThrow(() -> new EntityNotFoundException("Client introuvable : " + id));
        clientRepository.softDelete(id);
    }

    @Override
    public PointsFideliteResponse getPointsFidelite(Long idClient) {
        Client client = clientRepository.findByIdAndDeletedFalse(idClient)
            .orElseThrow(() -> new EntityNotFoundException("Client introuvable : " + idClient));
        PointsFidelite points = pointsRepository.findByClientId(idClient)
            .orElseThrow(() -> new EntityNotFoundException("Compte points introuvable"));
        return new PointsFideliteResponse(
            points.getSoldePoints(),
            points.getTotalAchatsAnnuel(),
            points.getDateDebutPeriode(),
            client.getTypeFidelite().getCode()
        );
    }

    // ── Helpers privés ────────────────────────────────────────
    private void validerUniciteEmail(String email) {
        if (clientRepository.existsByEmailAndDeletedFalse(email)) {
            throw new IllegalArgumentException("Email déjà utilisé : " + email);
        }
    }

    private void validerUnicitePseudo(String pseudo) {
        if (clientRepository.existsByPseudoAndDeletedFalse(pseudo)) {
            throw new IllegalArgumentException("Pseudo déjà utilisé : " + pseudo);
        }
    }
}
