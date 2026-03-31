package fr.micromania.controller;

import fr.micromania.dto.auth.AuthResponse;
import fr.micromania.dto.auth.LoginRequest;
import fr.micromania.security.JwtAuthFilter;
import fr.micromania.security.SecurityConfig;
import fr.micromania.service.AuthService;
import fr.micromania.service.ClientService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@Import(SecurityConfig.class)
@DisplayName("AuthController — tests slice WebMvc")
class AuthControllerTest {

    @Autowired MockMvc mockMvc;

    @MockitoBean AuthService authService;
    @MockitoBean ClientService clientService;
    @MockitoBean JwtAuthFilter jwtAuthFilter;

    AuthResponse authResponse;

    @BeforeEach
    void setUp() throws Exception {
        authResponse = new AuthResponse(
                "eyJhbGciOiJIUzI1NiJ9.test.sig",
                86400L,
                "alice",
                "alice@test.fr",
                "NORMAL"
        );

        doAnswer(invocation -> {
            ServletRequest request = invocation.getArgument(0);
            ServletResponse response = invocation.getArgument(1);
            FilterChain chain = invocation.getArgument(2);
            chain.doFilter(request, response);
            return null;
        }).when(jwtAuthFilter).doFilter(any(), any(), any());
    }

    @Test
    @DisplayName("POST /api/v1/auth/login/client — 200 avec token JWT")
    void login_ok() throws Exception {
        when(authService.loginClient(any(LoginRequest.class), any(), any()))
                .thenReturn(authResponse);

        mockMvc.perform(post("/api/v1/auth/login/client")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "alice@test.fr",
                                  "motDePasse": "Password1!"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.email").value("alice@test.fr"));
    }

    @Test
    @DisplayName("POST /api/v1/auth/login/client — 400 si email manquant")
    void login_emailManquant() throws Exception {
        mockMvc.perform(post("/api/v1/auth/login/client")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "motDePasse": "Password1!"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.email").exists());
    }

    @Test
    @DisplayName("POST /api/v1/auth/login/client — 400 si email invalide")
    void login_emailInvalide() throws Exception {
        mockMvc.perform(post("/api/v1/auth/login/client")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "pas-un-email",
                                  "motDePasse": "Password1!"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.email").exists());
    }

    @Test
    @DisplayName("POST /api/v1/auth/register — 201 avec token JWT")
    void register_ok() throws Exception {
        when(clientService.creerDepuisInscription(any())).thenReturn(null);

        AuthResponse registerResponse = new AuthResponse(
                "eyJhbGciOiJIUzI1NiJ9.register.sig",
                86400L,
                "newuser",
                "bob@test.fr",
                "NORMAL"
        );
        when(authService.loginClient(any(LoginRequest.class), any(), any()))
                .thenReturn(registerResponse);

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "pseudo": "newuser",
                                  "nom": "Martin",
                                  "prenom": "Bob",
                                  "dateNaissance": "2000-01-01",
                                  "email": "bob@test.fr",
                                  "telephone": "0612345678",
                                  "motDePasse": "Password1!",
                                  "rgpdConsent": true
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.pseudo").value("newuser"))
                .andExpect(jsonPath("$.email").value("bob@test.fr"));
    }

    @Test
    @DisplayName("POST /api/v1/auth/register — 400 si mot de passe trop court")
    void register_mdpTropCourt() throws Exception {
        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "pseudo": "newuser",
                                  "nom": "Martin",
                                  "prenom": "Bob",
                                  "dateNaissance": "2000-01-01",
                                  "email": "bob@test.fr",
                                  "telephone": "0612345678",
                                  "motDePasse": "court",
                                  "rgpdConsent": true
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.motDePasse").exists());
    }
}
