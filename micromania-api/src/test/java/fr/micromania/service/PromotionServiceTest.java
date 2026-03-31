package fr.micromania.service;

import fr.micromania.dto.promotion.*;
import fr.micromania.entity.commande.Promotion;
import fr.micromania.entity.referentiel.TypeReduction;
import fr.micromania.mapper.PromotionMapper;
import fr.micromania.repository.PromotionRepository;
import fr.micromania.service.impl.PromotionServiceImpl;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("PromotionService — tests unitaires")
class PromotionServiceTest {

    @Mock PromotionRepository promotionRepository;
    @Mock PromotionMapper     promotionMapper;

    @InjectMocks PromotionServiceImpl promotionService;

    Promotion promoFixe;
    Promotion promoPourcentage;

    @BeforeEach
    void setUp() {
        TypeReduction fixe = new TypeReduction();
        fixe.setCode("MONTANT_FIXE");

        TypeReduction pct = new TypeReduction();
        pct.setCode("POURCENTAGE");

        promoFixe = new Promotion();
        promoFixe.setId(1L);
        promoFixe.setCodePromo("PROMO10");
        promoFixe.setTypeReduction(fixe);
        promoFixe.setValeur(new BigDecimal("10.00"));
        promoFixe.setDateDebut(LocalDateTime.now().minusDays(1));
        promoFixe.setDateFin(LocalDateTime.now().plusDays(30));
        promoFixe.setActif(true);
        promoFixe.setNbUtilisationsActuel(0);

        promoPourcentage = new Promotion();
        promoPourcentage.setId(2L);
        promoPourcentage.setCodePromo("SUMMER20");
        promoPourcentage.setTypeReduction(pct);
        promoPourcentage.setValeur(new BigDecimal("20.00"));
        promoPourcentage.setDateDebut(LocalDateTime.now().minusDays(1));
        promoPourcentage.setDateFin(LocalDateTime.now().plusDays(30));
        promoPourcentage.setActif(true);
        promoPourcentage.setNbUtilisationsActuel(0);
    }

    // ── valider : montant fixe ─────────────────────────────────
    @Test
    @DisplayName("valider PROMO10 — remise de 10€ sur 100€")
    void valider_montantFixe_ok() {
        when(promotionRepository.findByCodePromoAndActifTrue("PROMO10"))
            .thenReturn(Optional.of(promoFixe));

        ValidatePromoResponse result = promotionService.valider(
            new ValidatePromoRequest("PROMO10", null, new BigDecimal("100.00")));

        assertThat(result.valide()).isTrue();
        assertThat(result.montantRemise()).isEqualByComparingTo("10.00");
    }

    // ── valider : montant fixe plafonné au total ───────────────
    @Test
    @DisplayName("valider PROMO10 — remise plafonnée si panier < 10€")
    void valider_montantFixe_plafonne() {
        when(promotionRepository.findByCodePromoAndActifTrue("PROMO10"))
            .thenReturn(Optional.of(promoFixe));

        ValidatePromoResponse result = promotionService.valider(
            new ValidatePromoRequest("PROMO10", null, new BigDecimal("5.00")));

        assertThat(result.valide()).isTrue();
        assertThat(result.montantRemise()).isEqualByComparingTo("5.00");
    }

    // ── valider : pourcentage ─────────────────────────────────
    @Test
    @DisplayName("valider SUMMER20 — remise de 20% sur 50€ = 10€")
    void valider_pourcentage_ok() {
        when(promotionRepository.findByCodePromoAndActifTrue("SUMMER20"))
            .thenReturn(Optional.of(promoPourcentage));

        ValidatePromoResponse result = promotionService.valider(
            new ValidatePromoRequest("SUMMER20", null, new BigDecimal("50.00")));

        assertThat(result.valide()).isTrue();
        assertThat(result.montantRemise()).isEqualByComparingTo("10.00");
    }

    // ── valider : code inconnu ────────────────────────────────
    @Test
    @DisplayName("valider code inconnu — retourne invalide")
    void valider_codeInconnu() {
        when(promotionRepository.findByCodePromoAndActifTrue("FAKE")).thenReturn(Optional.empty());

        ValidatePromoResponse result = promotionService.valider(
            new ValidatePromoRequest("FAKE", null, new BigDecimal("100.00")));

        assertThat(result.valide()).isFalse();
        assertThat(result.montantRemise()).isEqualByComparingTo("0.00");
    }

    // ── valider : hors période ────────────────────────────────
    @Test
    @DisplayName("valider — code expiré retourne invalide")
    void valider_expire() {
        promoFixe.setDateFin(LocalDateTime.now().minusDays(1));
        when(promotionRepository.findByCodePromoAndActifTrue("PROMO10"))
            .thenReturn(Optional.of(promoFixe));

        ValidatePromoResponse result = promotionService.valider(
            new ValidatePromoRequest("PROMO10", null, new BigDecimal("100.00")));

        assertThat(result.valide()).isFalse();
        assertThat(result.message()).contains("période");
    }

    // ── valider : quota dépassé ───────────────────────────────
    @Test
    @DisplayName("valider — quota max atteint retourne invalide")
    void valider_quotaAtteint() {
        promoFixe.setNbUtilisationsMax(5);
        promoFixe.setNbUtilisationsActuel(5);
        when(promotionRepository.findByCodePromoAndActifTrue("PROMO10"))
            .thenReturn(Optional.of(promoFixe));

        ValidatePromoResponse result = promotionService.valider(
            new ValidatePromoRequest("PROMO10", null, new BigDecimal("100.00")));

        assertThat(result.valide()).isFalse();
        assertThat(result.message()).contains("Quota");
    }

    // ── valider : montant minimum ─────────────────────────────
    @Test
    @DisplayName("valider — montant commande inférieur au minimum requis")
    void valider_montantMinimumNonAtteint() {
        promoFixe.setMontantMinimumCommande(new BigDecimal("50.00"));
        when(promotionRepository.findByCodePromoAndActifTrue("PROMO10"))
            .thenReturn(Optional.of(promoFixe));

        ValidatePromoResponse result = promotionService.valider(
            new ValidatePromoRequest("PROMO10", null, new BigDecimal("30.00")));

        assertThat(result.valide()).isFalse();
        assertThat(result.message()).contains("minimum");
    }

    // ── creer ─────────────────────────────────────────────────
    @Test
    @DisplayName("creer — refuse si code promo déjà actif")
    void creer_codeExistant_leveException() {
        when(promotionRepository.findByCodePromoAndActifTrue("PROMO10"))
            .thenReturn(Optional.of(promoFixe));

        CreatePromotionRequest request = new CreatePromotionRequest(
            "PROMO10", null, 1L, new BigDecimal("10"), LocalDateTime.now(),
            LocalDateTime.now().plusDays(30), null, null, null, false, null, null
        );

        assertThatThrownBy(() -> promotionService.creer(request))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("PROMO10");
    }

    // ── desactiver ────────────────────────────────────────────
    @Test
    @DisplayName("desactiver — passe actif à false")
    void desactiver_ok() {
        when(promotionRepository.findById(1L)).thenReturn(Optional.of(promoFixe));
        when(promotionRepository.save(any())).thenReturn(promoFixe);

        promotionService.desactiver(1L);

        assertThat(promoFixe.isActif()).isFalse();
        verify(promotionRepository).save(promoFixe);
    }
}
