package fr.micromania.service.impl;

import fr.micromania.dto.client.BonAchatResponse;
import fr.micromania.dto.client.FideliteDetailResponse;
import fr.micromania.dto.client.HistoriquePointsResponse;
import fr.micromania.entity.Client;
import fr.micromania.entity.PointsFidelite;
import fr.micromania.entity.commande.BonAchat;
import fr.micromania.entity.commande.Facture;
import fr.micromania.entity.commande.HistoriquePoints;
import fr.micromania.entity.commande.LigneFacture;
import fr.micromania.repository.*;
import fr.micromania.service.FideliteService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class FideliteServiceImpl implements FideliteService {

    private static final int SEUIL_BON_10 = 2000;
    private static final BigDecimal VALEUR_BON_10 = new BigDecimal("10.00");
    private static final int SEUIL_BON_20 = 8000;
    private static final BigDecimal VALEUR_BON_20 = new BigDecimal("20.00");
    private static final String OP_ACHAT = "ACHAT";
    private static final String OP_BON_10 = "BON_ACHAT_AUTO_10";
    private static final String OP_BON_20 = "BON_ACHAT_PALIER_20";

    private final ClientRepository clientRepository;
    private final PointsFideliteRepository pointsRepository;
    private final BonAchatRepository bonAchatRepository;
    private final HistoriquePointsRepository historiquePointsRepository;
    private final RatioPointsRepository ratioPointsRepository;
    private final FactureRepository factureRepository;

    @Override
    public FideliteDetailResponse getDetail(Long idClient) {
        Client client = chargerClient(idClient);
        PointsFidelite points = chargerComptePoints(idClient);
        int totalPointsGagnes = totalPointsGagnes(idClient);

        int pointsCycleBon10 = Math.floorMod(points.getSoldePoints(), SEUIL_BON_10);
        int pointsAvantBon10 = pointsCycleBon10 == 0 ? SEUIL_BON_10 : SEUIL_BON_10 - pointsCycleBon10;
        int progressionBon10Percent = calculerPourcentage(pointsCycleBon10, SEUIL_BON_10);

        int pointsCycleBon20 = Math.floorMod(totalPointsGagnes, SEUIL_BON_20);
        int pointsAvantBon20 = pointsCycleBon20 == 0 ? SEUIL_BON_20 : SEUIL_BON_20 - pointsCycleBon20;
        int progressionBon20Percent = calculerPourcentage(pointsCycleBon20, SEUIL_BON_20);

        String codeFidelite = client.getTypeFidelite().getCode();
        BigDecimal pointsParEuroBase = client.getTypeFidelite().getPointsParEuro();
        List<FideliteDetailResponse.RatioDetail> ratios = ratioPointsRepository
            .findAllByTypeFideliteCode(codeFidelite)
            .stream()
            .map(r -> new FideliteDetailResponse.RatioDetail(
                r.getTypeCategorie().getCode(),
                r.getTypeCategorie().getDescription() != null
                    ? r.getTypeCategorie().getDescription()
                    : r.getTypeCategorie().getCode(),
                r.getRatio()))
            .toList();

        return new FideliteDetailResponse(
            points.getSoldePoints(),
            points.getTotalAchatsAnnuel(),
            points.getDateDebutPeriode(),
            codeFidelite,
            pointsParEuroBase,
            ratios,
            SEUIL_BON_10,
            pointsCycleBon10,
            pointsAvantBon10,
            progressionBon10Percent,
            SEUIL_BON_20,
            pointsCycleBon20,
            pointsAvantBon20,
            progressionBon20Percent,
            totalPointsGagnes
        );
    }

    @Override
    public List<BonAchatResponse> getBonsAchat(Long idClient) {
        chargerClient(idClient);
        return bonAchatRepository.findByClientIdOrderByDateCreationDesc(idClient).stream()
            .map(this::toBonResponse)
            .toList();
    }

    @Override
    public List<HistoriquePointsResponse> getHistorique(Long idClient) {
        chargerClient(idClient);
        return historiquePointsRepository.findByClientIdOrderByDateOperationDesc(idClient).stream()
            .map(this::toHistoriqueResponse)
            .toList();
    }

    @Override
    @Transactional
    public void traiterFideliteApresFacture(Facture facture) {
        if (facture.getClient() == null || facture.getClient().getId() == null) {
            return;
        }

        Long idClient = facture.getClient().getId();
        Client client = chargerClient(idClient);
        PointsFidelite points = chargerComptePoints(idClient);

        Facture factureAvecLignes = factureRepository.findByIdAvecLignesEtCategories(facture.getId())
            .orElse(facture);
        int pointsGagnes = calculerPointsFacture(factureAvecLignes, client);
        if (pointsGagnes <= 0) {
            return;
        }

        points.setSoldePoints(points.getSoldePoints() + pointsGagnes);
        BigDecimal totalAchatsActuel = points.getTotalAchatsAnnuel() != null ? points.getTotalAchatsAnnuel() : BigDecimal.ZERO;
        points.setTotalAchatsAnnuel(totalAchatsActuel.add(facture.getMontantFinal()));
        pointsRepository.save(points);

        historiquePointsRepository.save(HistoriquePoints.builder()
            .client(client)
            .facture(facture)
            .typeOperation(OP_ACHAT)
            .points(pointsGagnes)
            .commentaire("Crédit de points après achat - facture " + facture.getReferenceFacture())
            .build());

        genererBonsAutomatiques10(client, points, facture);
        genererBonsPalier20(client, facture);

        log.info("Fidélité traitée : client={} facture={} points=+{} solde={} totalGagné={}",
            idClient, facture.getReferenceFacture(), pointsGagnes, points.getSoldePoints(), totalPointsGagnes(idClient));
    }

    private int calculerPointsFacture(Facture facture, Client client) {
        String typeFideliteCode = client.getTypeFidelite().getCode();
        BigDecimal fallback = client.getTypeFidelite().getPointsParEuro();

        int total = 0;
        for (LigneFacture ligne : facture.getLignes()) {
            String typeCategorieCode = ligne.getVariant().getProduit().getCategorie().getTypeCategorie().getCode();
            BigDecimal ratio = ratioPointsRepository
                    .findByTypeCategorieCodeAndTypeFideliteCode(typeCategorieCode, typeFideliteCode)
                    .map(r -> r.getRatio())
                    .orElse(fallback);

            BigDecimal montantArrondi = ligne.getMontantLigne().setScale(0, RoundingMode.HALF_UP);
            BigDecimal pointsLigne = montantArrondi.multiply(ratio).setScale(0, RoundingMode.DOWN);
            total += pointsLigne.intValue();
        }
        return total;
    }

    private void genererBonsAutomatiques10(Client client, PointsFidelite points, Facture facture) {
        while (points.getSoldePoints() >= SEUIL_BON_10) {
            BonAchat bon = creerBon(client, facture, VALEUR_BON_10, SEUIL_BON_10, "AUTO10");
            bonAchatRepository.save(bon);

            points.setSoldePoints(points.getSoldePoints() - SEUIL_BON_10);
            historiquePointsRepository.save(HistoriquePoints.builder()
                .client(client)
                .facture(facture)
                .typeOperation(OP_BON_10)
                .points(-SEUIL_BON_10)
                .commentaire("Bon automatique 10 € généré : " + bon.getCodeBon())
                .build());
        }
        pointsRepository.save(points);
    }

    private void genererBonsPalier20(Client client, Facture facture) {
        int totalPointsGagnes = totalPointsGagnes(client.getId());
        long bons20DejaGeneres = bonAchatRepository.countByClientIdAndPointsUtilisesAndValeur(
            client.getId(), SEUIL_BON_20, VALEUR_BON_20);
        int bons20Attendus = totalPointsGagnes / SEUIL_BON_20;
        long bons20Manquants = Math.max(0, bons20Attendus - bons20DejaGeneres);

        for (long i = 0; i < bons20Manquants; i++) {
            BonAchat bon = creerBon(client, facture, VALEUR_BON_20, SEUIL_BON_20, "PALIER20");
            bonAchatRepository.save(bon);

            historiquePointsRepository.save(HistoriquePoints.builder()
                .client(client)
                .facture(facture)
                .typeOperation(OP_BON_20)
                .points(0)
                .commentaire("Bon palier 20 € généré : " + bon.getCodeBon())
                .build());
        }
    }

    private BonAchat creerBon(Client client, Facture facture, BigDecimal valeur, int pointsUtilises, String prefixe) {
        return BonAchat.builder()
            .client(client)
            .facture(facture)
            .codeBon(genererCodeBon(prefixe))
            .valeur(valeur)
            .pointsUtilises(pointsUtilises)
            .utilise(false)
            .build();
    }

    private String genererCodeBon(String prefixe) {
        return "BON-" + prefixe + "-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private int totalPointsGagnes(Long idClient) {
        Long total = historiquePointsRepository.sumPositivePointsByClient(idClient);
        return total != null ? total.intValue() : 0;
    }

    private int calculerPourcentage(int valeur, int seuil) {
        if (seuil <= 0) {
            return 0;
        }
        return Math.min(100, Math.max(0, (int) Math.floor((valeur * 100.0) / seuil)));
    }

    private Client chargerClient(Long idClient) {
        return clientRepository.findByIdAndDeletedFalse(idClient)
            .orElseThrow(() -> new EntityNotFoundException("Client introuvable : " + idClient));
    }

    private PointsFidelite chargerComptePoints(Long idClient) {
        return pointsRepository.findByClientId(idClient)
            .orElseThrow(() -> new EntityNotFoundException("Compte points introuvable : " + idClient));
    }

    private BonAchatResponse toBonResponse(BonAchat bon) {
        return new BonAchatResponse(
            bon.getId(),
            bon.getCodeBon(),
            bon.getValeur(),
            bon.getPointsUtilises(),
            bon.isUtilise(),
            bon.getDateCreation(),
            bon.getDateExpiration(),
            bon.getDateUtilisation(),
            bon.getFacture() != null ? bon.getFacture().getId() : null
        );
    }

    private HistoriquePointsResponse toHistoriqueResponse(HistoriquePoints historique) {
        return new HistoriquePointsResponse(
            historique.getId(),
            historique.getTypeOperation(),
            historique.getPoints(),
            historique.getCommentaire(),
            historique.getDateOperation(),
            historique.getFacture() != null ? historique.getFacture().getId() : null
        );
    }
}
