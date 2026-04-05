package fr.micromania.service;

import fr.micromania.dto.auth.*;
import fr.micromania.entity.Client;
import fr.micromania.entity.Employe;
import fr.micromania.entity.referentiel.Role;
import fr.micromania.repository.*;
import fr.micromania.service.impl.AuthServiceImpl;
import fr.micromania.service.impl.AuthServiceImpl.BadCredentialsException;
import fr.micromania.util.TestFixtures;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService — tests unitaires")
class AuthServiceTest {

    @Mock ClientRepository              clientRepository;
    @Mock EmployeRepository             employeRepository;
    @Mock ResetPasswordTokenRepository  resetTokenRepository;
    @Mock PasswordEncoder               passwordEncoder;
    @Mock RememberMeTokenRepository     rememberMeTokenRepository;

    @InjectMocks AuthServiceImpl authService;

    Client  client;
    Employe employe;

    @BeforeEach
    void setUp() {

        ReflectionTestUtils.setField(authService, "jwtSecret",
            "MicromaniaSuperSecretKeyPourJWTMinimum256BitsRequis!");
        ReflectionTestUtils.setField(authService, "jwtExpirationMs", 86400000L);
        ReflectionTestUtils.setField(authService, "refreshExpirationMs", 604800000L);

        client = TestFixtures.clientActif();
        client.setMotDePasse("$2a$12$hashedpassword");

        Role role = new Role();
        role.setId(1L); role.setCode("VENDEUR"); role.setLibelle("Vendeur");

        employe = new Employe();
        employe.setId(10L);
        employe.setEmail("vendeur@micromania.fr");
        employe.setMotDePasse("$2a$12$hashedemploye");
        employe.setNom("Martin"); employe.setPrenom("Lucas");
        employe.setRole(role);
        employe.setActif(true); employe.setDeleted(false);
    }

    @Test
    @DisplayName("loginClient — succès avec identifiants valides")
    void loginClient_ok() {
        when(clientRepository.findByEmailAndDeletedFalse("alice@test.fr"))
            .thenReturn(Optional.of(client));
        when(passwordEncoder.matches("Password1!", client.getMotDePasse())).thenReturn(true);

        AuthResponse result = authService.loginClient(
            new LoginRequest("alice@test.fr", "Password1!", false), "127.0.0.1", "TestAgent");

        assertThat(result.accessToken()).isNotBlank();
        assertThat(result.email()).isEqualTo("alice@test.fr");
        assertThat(result.typeFidelite()).isEqualTo("NORMAL");
    }

    @Test
    @DisplayName("loginClient — mauvais mot de passe lève BadCredentialsException")
    void loginClient_mauvaisMdp() {
        when(clientRepository.findByEmailAndDeletedFalse("alice@test.fr"))
            .thenReturn(Optional.of(client));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        assertThatThrownBy(() -> authService.loginClient(
            new LoginRequest("alice@test.fr", "mauvais", false), "127.0.0.1", "TestAgent"))
            .isInstanceOf(BadCredentialsException.class);
    }

    @Test
    @DisplayName("loginClient — compte non activé lève IllegalStateException")
    void loginClient_compteNonActive() {
        client.setCompteActive(false);
        when(clientRepository.findByEmailAndDeletedFalse("alice@test.fr"))
            .thenReturn(Optional.of(client));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);

        assertThatThrownBy(() -> authService.loginClient(
            new LoginRequest("alice@test.fr", "Password1!", false), "127.0.0.1", "TestAgent"))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("activé");
    }

    @Test
    @DisplayName("loginClient — email non vérifié lève IllegalStateException")
    void loginClient_emailNonVerifie() {
        client.setEmailVerifie(false);
        when(clientRepository.findByEmailAndDeletedFalse("alice@test.fr"))
            .thenReturn(Optional.of(client));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);

        assertThatThrownBy(() -> authService.loginClient(
            new LoginRequest("alice@test.fr", "Password1!", false), "127.0.0.1", "TestAgent"))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("vérifié");
    }

    @Test
    @DisplayName("loginClient — email inexistant lève BadCredentialsException")
    void loginClient_emailInexistant() {
        when(clientRepository.findByEmailAndDeletedFalse("inexistant@test.fr"))
            .thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.loginClient(
            new LoginRequest("inexistant@test.fr", "Password1!", false), "127.0.0.1", "TestAgent"))
            .isInstanceOf(BadCredentialsException.class);
    }

    @Test
    @DisplayName("loginEmploye — succès avec rôle dans le token")
    void loginEmploye_ok() {
        when(employeRepository.findByEmailAndDeletedFalse("vendeur@micromania.fr"))
            .thenReturn(Optional.of(employe));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);

        AuthResponse result = authService.loginEmploye(
            new LoginRequest("vendeur@micromania.fr", "Admin1!?", false), "127.0.0.1", "TestAgent");

        assertThat(result.accessToken()).isNotBlank();
        assertThat(result.typeFidelite()).isEqualTo("VENDEUR");
    }

    @Test
    @DisplayName("demanderResetPassword — génère un token même si email existe")
    void demanderResetPassword_emailExistant() {
        when(clientRepository.findByEmailAndDeletedFalse("alice@test.fr"))
            .thenReturn(Optional.of(client));
        when(resetTokenRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        assertThatCode(() -> authService.demanderResetPassword("alice@test.fr"))
            .doesNotThrowAnyException();

        verify(resetTokenRepository).save(any());
    }

    @Test
    @DisplayName("demanderResetPassword — silencieux si email inexistant (sécurité)")
    void demanderResetPassword_emailInexistant() {
        when(clientRepository.findByEmailAndDeletedFalse("inconnu@test.fr"))
            .thenReturn(Optional.empty());

        assertThatCode(() -> authService.demanderResetPassword("inconnu@test.fr"))
            .doesNotThrowAnyException();

        verify(resetTokenRepository, never()).save(any());
    }

    @Test
    @DisplayName("changerMotDePasse — encode et sauvegarde le nouveau mot de passe")
    void changerMotDePasse_ok() {
        when(clientRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(client));
        when(passwordEncoder.matches("ancienMdp", client.getMotDePasse())).thenReturn(true);
        when(passwordEncoder.encode("NouveauMdp1!")).thenReturn("$2a$12$newHash");
        when(clientRepository.save(any())).thenReturn(client);

        authService.changerMotDePasse(1L,
            new ChangePasswordRequest("ancienMdp", "NouveauMdp1!"));

        assertThat(client.getMotDePasse()).isEqualTo("$2a$12$newHash");
        verify(clientRepository).save(client);
    }

    @Test
    @DisplayName("changerMotDePasse — ancien mot de passe incorrect lève exception")
    void changerMotDePasse_ancienMdpIncorrect() {
        when(clientRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(client));
        when(passwordEncoder.matches("mauvais", client.getMotDePasse())).thenReturn(false);

        assertThatThrownBy(() -> authService.changerMotDePasse(1L,
            new ChangePasswordRequest("mauvais", "NouveauMdp1!")))
            .isInstanceOf(BadCredentialsException.class);
    }
}
