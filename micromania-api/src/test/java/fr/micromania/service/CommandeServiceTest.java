package fr.micromania.service;

import fr.micromania.dto.commande.*;
import fr.micromania.entity.Client;
import fr.micromania.entity.commande.*;
import fr.micromania.entity.referentiel.StatutCommande;
import fr.micromania.mapper.CommandeMapper;
import fr.micromania.repository.*;
import fr.micromania.repository.StockMagasinRepository;
import fr.micromania.repository.StockEntrepotRepository;
import fr.micromania.service.impl.CommandeServiceImpl;
import fr.micromania.util.TestFixtures;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CommandeService — tests unitaires")
class CommandeServiceTest {

    @Mock CommandeRepository       commandeRepository;
    @Mock PanierRepository         panierRepository;
    @Mock ClientRepository         clientRepository;
    @Mock ProduitVariantRepository variantRepository;
    @Mock ProduitPrixRepository    prixRepository;
    @Mock StockMagasinRepository   stockMagasinRepository;
    @Mock StockEntrepotRepository  stockEntrepotRepository;
    @Mock PromotionRepository      promotionRepository;
    @Mock CommandeMapper           commandeMapper;

    @InjectMocks CommandeServiceImpl commandeService;

    Client   client;
    Commande commande;

    @BeforeEach
    void setUp() {
        client   = TestFixtures.clientActif();
        commande = TestFixtures.commande(client);
    }

    // ── getById ────────────────────────────────────────────────
    @Test
    @DisplayName("getById — retourne la commande existante")
    void getById_ok() {
        CommandeResponse response = buildCommandeResponse("CREEE");
        when(commandeRepository.findById(1L)).thenReturn(Optional.of(commande));
        when(commandeMapper.toResponse(commande)).thenReturn(response);

        CommandeResponse result = commandeService.getById(1L);

        assertThat(result.referenceCommande()).isEqualTo("CMD-TEST-001");
    }

    @Test
    @DisplayName("getById — lève EntityNotFoundException si commande introuvable")
    void getById_introuvable() {
        when(commandeRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> commandeService.getById(99L))
            .isInstanceOf(EntityNotFoundException.class);
    }

    // ── updateStatut ──────────────────────────────────────────
    @Test
    @DisplayName("updateStatut — transition CREEE → PAYEE avec horodatage")
    void updateStatut_creeVersPayee() {
        commande.setStatutCommande(TestFixtures.statutCommande("CREEE"));
        when(commandeRepository.findById(1L)).thenReturn(Optional.of(commande));
        when(commandeRepository.save(any())).thenReturn(commande);
        when(commandeMapper.toResponse(any())).thenReturn(buildCommandeResponse("PAYEE"));

        CommandeResponse result = commandeService.updateStatut(1L,
            new UpdateStatutCommandeRequest("PAYEE", null));

        assertThat(commande.getDatePaiement()).isNotNull();
        verify(commandeRepository).save(commande);
    }

    @Test
    @DisplayName("updateStatut — transition EXPEDIEE → CREEE invalide")
    void updateStatut_transitionInvalide() {
        commande.setStatutCommande(TestFixtures.statutCommande("EXPEDIEE"));
        when(commandeRepository.findById(1L)).thenReturn(Optional.of(commande));

        assertThatThrownBy(() -> commandeService.updateStatut(1L,
            new UpdateStatutCommandeRequest("CREEE", null)))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("EXPEDIEE");
    }

    // ── annuler ───────────────────────────────────────────────
    @Test
    @DisplayName("annuler — possible depuis CREEE")
    void annuler_depuisCreee() {
        commande.setStatutCommande(TestFixtures.statutCommande("CREEE"));
        when(commandeRepository.findById(1L)).thenReturn(Optional.of(commande));
        when(commandeRepository.save(any())).thenReturn(commande);

        commandeService.annuler(1L, "Client a changé d'avis");

        assertThat(commande.getStatutCommande().getCode()).isEqualTo("ANNULEE");
    }

    @Test
    @DisplayName("annuler — impossible si commande déjà livrée")
    void annuler_dejsLivree_leveException() {
        commande.setStatutCommande(TestFixtures.statutCommande("LIVREE"));
        when(commandeRepository.findById(1L)).thenReturn(Optional.of(commande));

        assertThatThrownBy(() -> commandeService.annuler(1L, "Tentative"))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("expédiée/livrée");
    }

    // ── getByReference ────────────────────────────────────────
    @Test
    @DisplayName("getByReference — retourne la commande correspondante")
    void getByReference_ok() {
        when(commandeRepository.findByReferenceCommande("CMD-TEST-001"))
            .thenReturn(Optional.of(commande));
        when(commandeMapper.toResponse(commande)).thenReturn(buildCommandeResponse("CREEE"));

        CommandeResponse result = commandeService.getByReference("CMD-TEST-001");

        assertThat(result.referenceCommande()).isEqualTo("CMD-TEST-001");
    }

    // ── Helper ────────────────────────────────────────────────
    private CommandeResponse buildCommandeResponse(String statut) {
        return new CommandeResponse(
            1L, "CMD-TEST-001", statut, "WEB", "DOMICILE", "CB",
            null, new BigDecimal("69.99"), BigDecimal.ZERO,
            new BigDecimal("4.99"), new BigDecimal("74.98"),
            null, LocalDateTime.now(), null, null, null, null, null
        );
    }
}
