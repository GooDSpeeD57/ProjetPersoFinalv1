package fr.micromania.service.impl;

import fr.micromania.dto.auth.RegisterRequest;
import fr.micromania.dto.client.ClientResponse;
import fr.micromania.dto.client.ClientSummary;
import fr.micromania.dto.client.CreateClientRequest;
import fr.micromania.dto.client.PointsFideliteResponse;
import fr.micromania.dto.client.UpdateClientRequest;
import fr.micromania.entity.AbonnementClient;
import fr.micromania.entity.Avatar;
import fr.micromania.entity.Client;
import fr.micromania.entity.PointsFidelite;
import fr.micromania.entity.referentiel.TypeFidelite;
import fr.micromania.mapper.AuthMapper;
import fr.micromania.mapper.ClientMapper;
import fr.micromania.repository.AbonnementClientRepository;
import fr.micromania.repository.AvatarRepository;
import fr.micromania.repository.ClientRepository;
import fr.micromania.repository.PointsFideliteRepository;
import fr.micromania.repository.StatutAbonnementRepository;
import fr.micromania.repository.TypeFideliteRepository;
import fr.micromania.service.ClientService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

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
    private final AvatarRepository avatarRepository;
    private final TypeFideliteRepository typeFideliteRepository;
    private final AbonnementClientRepository abonnementClientRepository;
    private final StatutAbonnementRepository statutAbonnementRepository;

    @Override
    @Transactional
    public ClientResponse creerDepuisInscription(RegisterRequest request) {
        validerUniciteEmail(request.email());
        validerUnicitePseudo(request.pseudo());
        validerUniciteTelephone(request.telephone());

        Avatar avatarDefaut = resoudreAvatar(request.idAvatar(), "l'inscription");

        TypeFidelite typeNormal = typeFideliteRepository.findByCode("NORMAL")
            .orElseThrow(() -> new IllegalStateException("Type fidélité NORMAL introuvable"));

        Client client = authMapper.registerRequestToClient(request);
        client.setAvatar(avatarDefaut);
        client.setTypeFidelite(typeNormal);
        client.setNumeroCarteFidelite(genererNumeroCarteFideliteUnique());
        client.setMotDePasse(passwordEncoder.encode(request.motDePasse()));
        client.setRgpdConsentDate(Boolean.TRUE.equals(request.rgpdConsent()) ? LocalDateTime.now() : null);

        // Active immédiatement le compte pour que le register + auto-login fonctionne.
        client.setEmailVerifie(true);
        client.setDateVerificationEmail(LocalDateTime.now());
        client.setCompteActive(true);
        client.setDoitDefinirMotDePasse(false);
        client.setTokenVerificationEmail(null);
        client.setTokenVerificationExpireLe(null);

        client = clientRepository.save(client);

        log.info("Nouveau client créé : pseudo={}, email={}, carte={}",
                client.getPseudo(), client.getEmail(), client.getNumeroCarteFidelite());

        PointsFidelite points = pointsRepository.findByClientId(client.getId()).orElse(null);
        return enrichirClientResponse(client, points);
    }

    @Override
    @Transactional
    public ClientResponse creerParEmploye(CreateClientRequest request, Long idEmployeCreateur) {
        validerUniciteEmail(request.email());
        validerUnicitePseudo(request.pseudo());
        validerUniciteTelephone(request.telephone());

        Avatar avatarDefaut = resoudreAvatar(request.idAvatar(), "la création client");

        TypeFidelite typeNormal = typeFideliteRepository.findByCode("NORMAL")
            .orElseThrow(() -> new IllegalStateException("Type fidélité NORMAL introuvable"));

        Client client = authMapper.createClientRequestToClient(request);
        client.setAvatar(avatarDefaut);
        client.setTypeFidelite(typeNormal);
        client.setNumeroCarteFidelite(genererNumeroCarteFideliteUnique());
        client.setMotDePasse(passwordEncoder.encode(UUID.randomUUID().toString()));
        client.setTokenVerificationEmail(UUID.randomUUID().toString());
        client.setTokenVerificationExpireLe(LocalDateTime.now().plusDays(7));

        client = clientRepository.save(client);

        log.info("Client créé par employé {} : email={}, carte={}",
                idEmployeCreateur, client.getEmail(), client.getNumeroCarteFidelite());

        return enrichirClientResponse(client, null);
    }

    @Override
    public ClientResponse getById(Long id) {
        Client client = clientRepository.findByIdAndDeletedFalse(id)
            .orElseThrow(() -> new EntityNotFoundException("Client introuvable : " + id));
        client = synchroniserStatutUltimate(client);
        PointsFidelite points = pointsRepository.findByClientId(id).orElse(null);
        return enrichirClientResponse(client, points);
    }

    @Override
    public ClientResponse getByPseudo(String pseudo) {
        Client client = clientRepository.findByPseudoAndDeletedFalse(pseudo)
            .orElseThrow(() -> new EntityNotFoundException("Client introuvable : " + pseudo));
        PointsFidelite points = pointsRepository.findByClientId(client.getId()).orElse(null);
        return enrichirClientResponse(client, points);
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
        if (request.telephone() != null && !request.telephone().equals(client.getTelephone())) {
            validerUniciteTelephone(request.telephone());
            client.setTelephone(request.telephone());
        }
        if (request.nom() != null) client.setNom(request.nom());
        if (request.prenom() != null) client.setPrenom(request.prenom());
        if (request.dateNaissance() != null) client.setDateNaissance(request.dateNaissance());
        if (request.idAvatar() != null) {
            Avatar avatar = avatarRepository.findByIdAndActifTrue(request.idAvatar())
                    .orElseThrow(() -> new EntityNotFoundException("Avatar introuvable ou inactif : " + request.idAvatar()));
            client.setAvatar(avatar);
        }

        client = clientRepository.save(client);
        PointsFidelite points = pointsRepository.findByClientId(id).orElse(null);
        return enrichirClientResponse(client, points);
    }

    @Override
    @Transactional
    public void verifierEmail(String token) {
        Client client = clientRepository.findByTokenVerificationEmail(token)
            .orElseThrow(() -> new EntityNotFoundException("Token invalide ou expiré"));

        if (client.getTokenVerificationExpireLe() != null
                && client.getTokenVerificationExpireLe().isBefore(LocalDateTime.now())) {
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
        client.setDateSuppression(LocalDateTime.now().plusDays(30));
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
    @Transactional
    public ClientResponse souscrireUltimate(Long idClient) {
        Client client = clientRepository.findByIdAndDeletedFalse(idClient)
            .orElseThrow(() -> new EntityNotFoundException("Client introuvable : " + idClient));

        client = synchroniserStatutUltimate(client);

        abonnementClientRepository.findTopByClientIdAndStatutAbonnementCodeAndDateFinGreaterThanEqualOrderByDateFinDesc(
                idClient, "ACTIF", LocalDate.now())
            .ifPresent(a -> {
                throw new IllegalStateException("Vous avez déjà un abonnement ULTIMATE actif jusqu'au " + a.getDateFin());
            });

        TypeFidelite typeUltimate = typeFideliteRepository.findByCode("ULTIMATE")
            .orElseThrow(() -> new IllegalStateException("Type fidélité ULTIMATE introuvable"));

        var statutActif = statutAbonnementRepository.findByCode("ACTIF")
            .orElseThrow(() -> new IllegalStateException("Statut abonnement ACTIF introuvable"));

        if (typeUltimate.getPrixAbonnement() == null) {
            throw new IllegalStateException("Le prix d'abonnement ULTIMATE n'est pas configuré");
        }

        LocalDate dateDebut = LocalDate.now();
        LocalDate dateFin = dateDebut.plusYears(1).minusDays(1);

        AbonnementClient abonnement = AbonnementClient.builder()
            .client(client)
            .statutAbonnement(statutActif)
            .dateDebut(dateDebut)
            .dateFin(dateFin)
            .montantPaye(typeUltimate.getPrixAbonnement())
            .datePaiement(LocalDateTime.now())
            .renouvellementAuto(false)
            .build();

        abonnementClientRepository.save(abonnement);
        client.setTypeFidelite(typeUltimate);
        client = clientRepository.save(client);

        PointsFidelite points = pointsRepository.findByClientId(idClient).orElse(null);
        return enrichirClientResponse(client, points);
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


    private ClientResponse enrichirClientResponse(Client client, PointsFidelite points) {
        ClientResponse base = clientMapper.toResponse(client, points);
        AbonnementClient dernierAbonnement = abonnementClientRepository.findTopByClientIdOrderByDateFinDesc(client.getId())
            .orElse(null);
        AbonnementClient abonnementActif = abonnementClientRepository
            .findTopByClientIdAndStatutAbonnementCodeAndDateFinGreaterThanEqualOrderByDateFinDesc(
                client.getId(), "ACTIF", LocalDate.now())
            .orElse(null);

        return new ClientResponse(
            base.id(),
            base.pseudo(),
            base.nom(),
            base.prenom(),
            base.dateNaissance(),
            base.email(),
            base.telephone(),
            base.typeFidelite(),
            base.numeroCarteFidelite(),
            base.soldePoints(),
            base.avatar(),
            base.emailVerifie(),
            base.telephoneVerifie(),
            base.compteActive(),
            base.dateDerniereConnexion(),
            base.dateCreation(),
            dernierAbonnement != null ? dernierAbonnement.getDateDebut() : null,
            dernierAbonnement != null ? dernierAbonnement.getDateFin() : null,
            abonnementActif != null,
            typeFideliteRepository.findByCode("ULTIMATE").map(TypeFidelite::getPrixAbonnement).orElse(null)
        );
    }

    private Client synchroniserStatutUltimate(Client client) {
        AbonnementClient abonnementActif = abonnementClientRepository
            .findTopByClientIdAndStatutAbonnementCodeAndDateFinGreaterThanEqualOrderByDateFinDesc(
                client.getId(), "ACTIF", LocalDate.now())
            .orElse(null);

        if (abonnementActif != null) {
            if (!"ULTIMATE".equals(client.getTypeFidelite().getCode())) {
                TypeFidelite typeUltimate = typeFideliteRepository.findByCode("ULTIMATE")
                    .orElseThrow(() -> new IllegalStateException("Type fidélité ULTIMATE introuvable"));
                client.setTypeFidelite(typeUltimate);
                return clientRepository.save(client);
            }
            return client;
        }

        var statutExpire = statutAbonnementRepository.findByCode("EXPIRE").orElse(null);
        if (statutExpire != null) {
            abonnementClientRepository.findByClientIdAndStatutAbonnementCodeAndDateFinBefore(client.getId(), "ACTIF", LocalDate.now())
                .forEach(abonnement -> abonnement.setStatutAbonnement(statutExpire));
        }

        if ("ULTIMATE".equals(client.getTypeFidelite().getCode())) {
            PointsFidelite points = pointsRepository.findByClientId(client.getId()).orElse(null);
            TypeFidelite typePremium = typeFideliteRepository.findByCode("PREMIUM")
                .orElseThrow(() -> new IllegalStateException("Type fidélité PREMIUM introuvable"));
            TypeFidelite typeNormal = typeFideliteRepository.findByCode("NORMAL")
                .orElseThrow(() -> new IllegalStateException("Type fidélité NORMAL introuvable"));

            boolean premiumEligible = points != null
                && points.getTotalAchatsAnnuel() != null
                && typePremium.getSeuilUpgradeEuro() != null
                && points.getTotalAchatsAnnuel().compareTo(typePremium.getSeuilUpgradeEuro()) >= 0;

            client.setTypeFidelite(premiumEligible ? typePremium : typeNormal);
            return clientRepository.save(client);
        }

        return client;
    }

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

    private void validerUniciteTelephone(String telephone) {
        if (clientRepository.existsByTelephoneAndDeletedFalse(telephone)) {
            throw new IllegalArgumentException("Téléphone déjà utilisé : " + telephone);
        }
    }

    private String genererNumeroCarteFideliteUnique() {
        for (int tentative = 0; tentative < 20; tentative++) {
            String numero = genererNumeroCarteFidelite();
            if (!clientRepository.existsByNumeroCarteFidelite(numero)) {
                return numero;
            }
        }
        throw new IllegalStateException("Impossible de générer un numéro de carte fidélité unique");
    }

    private String genererNumeroCarteFidelite() {
        int annee = Year.now().getValue();
        long blocAleatoire = ThreadLocalRandom.current().nextLong(100_000_000L, 1_000_000_000L);
        return "MM-" + annee + "-" + blocAleatoire;
    }

    private Avatar resoudreAvatar(Long idAvatar, String contexte) {
        if (idAvatar != null) {
            return avatarRepository.findByIdAndActifTrue(idAvatar)
                    .orElseThrow(() -> new EntityNotFoundException("Avatar introuvable ou inactif : " + idAvatar));
        }

        return avatarRepository.findFirstByActifTrueOrderByIdAsc()
                .orElseThrow(() -> new IllegalStateException("Aucun avatar actif disponible pour " + contexte));
    }
}
