package fr.micromania.service;

import fr.micromania.dto.auth.RegisterRequest;
import fr.micromania.dto.client.*;
import fr.micromania.entity.Client;
import fr.micromania.entity.PointsFidelite;
import fr.micromania.mapper.AuthMapper;
import fr.micromania.mapper.ClientMapper;
import fr.micromania.repository.ClientRepository;
import fr.micromania.repository.PointsFideliteRepository;
import fr.micromania.service.impl.ClientServiceImpl;
import fr.micromania.util.TestFixtures;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ClientService — tests unitaires")
class ClientServiceTest {

    @Mock ClientRepository         clientRepository;
    @Mock PointsFideliteRepository pointsRepository;
    @Mock ClientMapper             clientMapper;
    @Mock AuthMapper               authMapper;
    @Mock PasswordEncoder          passwordEncoder;

    @InjectMocks ClientServiceImpl clientService;

    Client       client;
    PointsFidelite points;
    ClientResponse clientResponse;

    @BeforeEach
    void setUp() {
        client = TestFixtures.clientActif();
        points = TestFixtures.points(client);
        clientResponse = new ClientResponse(
                1L, "testuser", "Dupont", "Alice",
                LocalDate.of(1995, 6, 15), "alice@test.fr", "0601020304",
                "NORMAL", null, 150,
                new AvatarDto(1L, "Default", "/images/avatars/default.png", "Avatar"),
                true,   // emailVerifie
                false,  // telephoneVerifie  ← ajouté
                true,   // compteActive
                null,   // dateDerniereConnexion  ← ajouté
                null    // dateCreation
        );
    }

    @Test
    @DisplayName("getById — retourne le client avec ses points")
    void getById_existant_retourneResponse() {
        when(clientRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(client));
        when(pointsRepository.findByClientId(1L)).thenReturn(Optional.of(points));
        when(clientMapper.toResponse(client, points)).thenReturn(clientResponse);

        ClientResponse result = clientService.getById(1L);

        assertThat(result).isNotNull();
        assertThat(result.pseudo()).isEqualTo("testuser");
        assertThat(result.soldePoints()).isEqualTo(150);
        verify(clientRepository).findByIdAndDeletedFalse(1L);
    }

    @Test
    @DisplayName("getById — lève EntityNotFoundException si client inexistant")
    void getById_inexistant_leveException() {
        when(clientRepository.findByIdAndDeletedFalse(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> clientService.getById(99L))
            .isInstanceOf(EntityNotFoundException.class)
            .hasMessageContaining("99");
    }

    @Test
    @DisplayName("creerDepuisInscription — crée le client et encode le mot de passe")
    void creerDepuisInscription_ok() {
        RegisterRequest request = new RegisterRequest(
            "newuser", "Martin", "Bob", LocalDate.of(2000, 1, 1),
            "bob@test.fr", "0612345678", "Password1!", true
        );

        when(clientRepository.existsByEmailAndDeletedFalse("bob@test.fr")).thenReturn(false);
        when(clientRepository.existsByPseudoAndDeletedFalse("newuser")).thenReturn(false);
        when(authMapper.registerRequestToClient(request)).thenReturn(client);
        when(passwordEncoder.encode("Password1!")).thenReturn("$2a$12$encodedpwd");
        when(clientRepository.save(any())).thenReturn(client);
        when(pointsRepository.findByClientId(anyLong())).thenReturn(Optional.of(points));
        when(clientMapper.toResponse(any(Client.class), any())).thenReturn(clientResponse);

        ClientResponse result = clientService.creerDepuisInscription(request);

        assertThat(result).isNotNull();
        verify(passwordEncoder).encode("Password1!");
        verify(clientRepository).save(any(Client.class));
    }

    @Test
    @DisplayName("creerDepuisInscription — refuse si email déjà utilisé")
    void creerDepuisInscription_emailDuplique_leveException() {
        RegisterRequest request = new RegisterRequest(
            "newuser", "Martin", "Bob", LocalDate.of(2000, 1, 1),
            "alice@test.fr", "0612345678", "Password1!", true
        );
        when(clientRepository.existsByEmailAndDeletedFalse("alice@test.fr")).thenReturn(true);

        assertThatThrownBy(() -> clientService.creerDepuisInscription(request))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("alice@test.fr");
    }

    @Test
    @DisplayName("search — délègue au repository et mappe les résultats")
    void search_ok() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Client> page = new PageImpl<>(List.of(client));
        ClientSummary summary = new ClientSummary(
            1L, "testuser", "Dupont", "Alice", "alice@test.fr", "0601020304", "NORMAL", true
        );
        when(clientRepository.search("dupont", pageable)).thenReturn(page);
        when(clientMapper.toSummary(client)).thenReturn(summary);

        Page<ClientSummary> result = clientService.search("dupont", pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).pseudo()).isEqualTo("testuser");
    }

    @Test
    @DisplayName("softDelete — appelle le soft-delete sur le repository")
    void softDelete_ok() {
        when(clientRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(client));

        clientService.softDelete(1L);

        verify(clientRepository).softDelete(1L);
    }

    @Test
    @DisplayName("softDelete — lève exception si client déjà supprimé")
    void softDelete_inexistant_leveException() {
        when(clientRepository.findByIdAndDeletedFalse(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> clientService.softDelete(99L))
            .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    @DisplayName("verifierEmail — active le compte et efface le token")
    void verifierEmail_ok() {
        client.setTokenVerificationEmail("valid-token");
        client.setTokenVerificationExpireLe(java.time.LocalDateTime.now().plusHours(1));
        client.setEmailVerifie(false);
        client.setCompteActive(false);

        when(clientRepository.findByTokenVerificationEmail("valid-token"))
            .thenReturn(Optional.of(client));
        when(clientRepository.save(any())).thenReturn(client);

        clientService.verifierEmail("valid-token");

        assertThat(client.isEmailVerifie()).isTrue();
        assertThat(client.isCompteActive()).isTrue();
        assertThat(client.getTokenVerificationEmail()).isNull();
        verify(clientRepository).save(client);
    }

    @Test
    @DisplayName("verifierEmail — lève exception si token expiré")
    void verifierEmail_tokenExpire_leveException() {
        client.setTokenVerificationEmail("expired-token");
        client.setTokenVerificationExpireLe(java.time.LocalDateTime.now().minusHours(1));

        when(clientRepository.findByTokenVerificationEmail("expired-token"))
            .thenReturn(Optional.of(client));

        assertThatThrownBy(() -> clientService.verifierEmail("expired-token"))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("expiré");
    }
}
