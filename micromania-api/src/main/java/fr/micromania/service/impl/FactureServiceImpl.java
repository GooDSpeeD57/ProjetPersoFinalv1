package fr.micromania.service.impl;

import fr.micromania.dto.facture.*;
import fr.micromania.entity.Adresse;
import fr.micromania.entity.Client;
import fr.micromania.entity.Magasin;
import fr.micromania.entity.catalog.ProduitVariant;
import fr.micromania.entity.commande.*;
import fr.micromania.entity.referentiel.*;
import fr.micromania.mapper.FactureMapper;
import fr.micromania.repository.AdresseRepository;
import fr.micromania.repository.BonAchatRepository;
import fr.micromania.repository.CommandeRepository;
import fr.micromania.repository.ContexteVenteRepository;
import fr.micromania.repository.FactureRepository;
import fr.micromania.repository.MagasinRepository;
import fr.micromania.repository.ModePaiementRepository;
import fr.micromania.repository.PanierRepository;
import fr.micromania.repository.ProduitVariantRepository;
import fr.micromania.repository.StatutFactureRepository;
import fr.micromania.repository.StockMagasinRepository;
import fr.micromania.service.FactureService;
import fr.micromania.service.FideliteService;
import fr.micromania.service.FideliteUpgradeService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class FactureServiceImpl implements FactureService {

    private static final BigDecimal VALEUR_BON_10 = new BigDecimal("10.00");
    private static final BigDecimal VALEUR_BON_20 = new BigDecimal("20.00");

    private final FactureRepository factureRepository;
    private final CommandeRepository commandeRepository;
    private final ProduitVariantRepository variantRepository;
    private final StockMagasinRepository stockMagasinRepository;
    private final BonAchatRepository bonAchatRepository;
    private final PanierRepository panierRepository;
    private final AdresseRepository adresseRepository;
    private final MagasinRepository magasinRepository;
    private final ModePaiementRepository modePaiementRepository;
    private final ContexteVenteRepository contexteVenteRepository;
    private final StatutFactureRepository statutFactureRepository;
    private final FactureMapper factureMapper;
    private final FideliteUpgradeService fideliteUpgradeService;
    private final FideliteService fideliteService;

    @Override
    @Transactional
    public FactureResponse genererDepuisCommande(Long idCommande) {
        Commande commande = commandeRepository.findById(idCommande)
            .orElseThrow(() -> new EntityNotFoundException("Commande introuvable : " + idCommande));

        if (factureRepository.existsByCommandeId(idCommande)) {
            throw new IllegalStateException("Une facture existe déjà pour la commande " + idCommande);
        }

        Facture facture = Facture.builder()
            .referenceFacture(genererReferenceFacture())
            .commande(commande)
            .client(commande.getClient())
            .modePaiement(commande.getModePaiement())
            .montantRemise(commande.getMontantRemise())
            .montantTotal(BigDecimal.ZERO)
            .montantHtTotal(BigDecimal.ZERO)
            .montantTvaTotal(BigDecimal.ZERO)
            .montantFinal(BigDecimal.ZERO)
            .build();

        facture.setStatutFacture(chargerStatutFacture("EMISE"));
        facture.setContexteVente(chargerContexteVente("EN_LIGNE"));

        if (commande.getMagasinRetrait() != null) {
            facture.setMagasin(commande.getMagasinRetrait());
        } else if (commande.getEntrepotExpedition() != null) {
            facture.setMagasin(resoudreMagasinPourCheckout(null));
        }

        for (LigneCommande lc : commande.getLignes()) {
            LigneFacture lf = construireLigneFacture(
                facture,
                lc.getVariant(),
                lc.getQuantite(),
                lc.getPrixUnitaire(),
                lc
            );
            facture.getLignes().add(lf);
        }

        BonAchat bonAchat = null;
        if (commande.getBonAchat() != null && commande.getBonAchat().getId() != null) {
            bonAchat = chargerBonAchatDisponible(commande.getClient().getId(), commande.getBonAchat().getId());
        }

        recalculerTotauxFacture(facture);
        if (bonAchat != null) {
            appliquerBonAchat(facture, bonAchat);
        }

        facture = factureRepository.save(facture);
        if (bonAchat != null) {
            marquerBonAchatCommeUtilise(bonAchat, facture);
        }

        fideliteService.traiterFideliteApresFacture(facture);
        fideliteUpgradeService.appliquerUpgradeAutomatique(commande.getClient().getId());
        decrementerStock(facture);

        log.info("Facture générée : ref={} commande={}", facture.getReferenceFacture(), idCommande);
        return factureMapper.toResponse(facture);
    }

    @Override
    @Transactional
    public FactureResponse creerVenteMagasin(CreateFactureVenteRequest request) {
        Facture facture = Facture.builder()
            .referenceFacture(genererReferenceFacture())
            .nomClient(request.nomClient())
            .emailClient(request.emailClient())
            .telephoneClient(request.telephoneClient())
            .montantRemise(BigDecimal.ZERO)
            .montantTotal(BigDecimal.ZERO)
            .montantHtTotal(BigDecimal.ZERO)
            .montantTvaTotal(BigDecimal.ZERO)
            .montantFinal(BigDecimal.ZERO)
            .build();

        if (request.idClient() != null) {
            Client client = new Client();
            client.setId(request.idClient());
            facture.setClient(client);
        }

        Magasin magasin = new Magasin();
        magasin.setId(request.idMagasin());
        facture.setMagasin(magasin);

        ModePaiement modePaiement = new ModePaiement();
        modePaiement.setId(request.idModePaiement());
        facture.setModePaiement(modePaiement);

        facture.setStatutFacture(chargerStatutFacture("EMISE"));

        ContexteVente contexte = new ContexteVente();
        contexte.setId(request.idContexteVente());
        facture.setContexteVente(contexte);

        if (request.idEmploye() != null) {
            fr.micromania.entity.Employe emp = new fr.micromania.entity.Employe();
            emp.setId(request.idEmploye());
            facture.setEmploye(emp);
        }

        for (LigneFactureRequest lReq : request.lignes()) {
            ProduitVariant variant = variantRepository.findById(lReq.idVariant())
                .orElseThrow(() -> new EntityNotFoundException("Variant introuvable : " + lReq.idVariant()));
            LigneFacture lf = construireLigneFacture(
                facture,
                variant,
                lReq.quantite(),
                lReq.prixUnitaire(),
                null
            );
            facture.getLignes().add(lf);
        }

        BonAchat bonAchat = null;
        if (request.idBonAchat() != null) {
            if (request.idClient() == null) {
                throw new IllegalStateException("Un bon d'achat ne peut être utilisé que pour un client identifié");
            }
            bonAchat = chargerBonAchatDisponible(request.idClient(), request.idBonAchat());
        }

        recalculerTotauxFacture(facture);

        if (request.codePromo() != null) {
            BigDecimal remise = calculerRemisePromo(request.codePromo(), facture.getMontantTotal());
            facture.setMontantRemise(facture.getMontantRemise().add(remise));
        }

        if (bonAchat != null) {
            appliquerBonAchat(facture, bonAchat);
        } else {
            facture.setMontantFinal(
                facture.getMontantTotal().subtract(facture.getMontantRemise()).max(BigDecimal.ZERO)
            );
        }

        facture = factureRepository.save(facture);
        if (bonAchat != null) {
            marquerBonAchatCommeUtilise(bonAchat, facture);
        }

        if (request.idClient() != null) {
            fideliteService.traiterFideliteApresFacture(facture);
            fideliteUpgradeService.appliquerUpgradeAutomatique(request.idClient());
        }

        decrementerStock(facture);

        log.info("Vente magasin facturée : ref={} magasin={}", facture.getReferenceFacture(), request.idMagasin());
        return factureMapper.toResponse(facture);
    }

    @Override
    @Transactional
    public FactureResponse checkoutPanierClient(Long idClient, CheckoutPanierRequest request) {
        Panier panier = panierRepository.findPanierActif(idClient, "WEB")
            .orElseThrow(() -> new EntityNotFoundException("Panier introuvable pour le client " + idClient));

        if (panier.getLignes() == null || panier.getLignes().isEmpty()) {
            throw new IllegalStateException("Votre panier est vide");
        }

        String modeLivraison = normaliserModeLivraison(request != null ? request.modeLivraisonCode() : null);
        Adresse adresse = chargerAdresseCheckout(idClient, request != null ? request.idAdresse() : null);
        if (adresse == null) {
            throw new IllegalStateException("Ajoutez une adresse puis sélectionnez-la pour finaliser votre commande");
        }

        Magasin magasinCheckout = "RETRAIT_MAGASIN".equals(modeLivraison)
            ? chargerMagasinRetraitOuProche(idClient, request != null ? request.idMagasinRetrait() : null, adresse)
            : resoudreMagasinPourCheckout(adresse);

        BonAchat bonAchat = null;
        if (request != null && request.idBonAchat() != null) {
            bonAchat = chargerBonAchatDisponible(idClient, request.idBonAchat());
        }

        Facture facture = Facture.builder()
            .referenceFacture(genererReferenceFacture())
            .client(panier.getClient())
            .nomClient(panier.getClient().getNom() + " " + panier.getClient().getPrenom())
            .emailClient(panier.getClient().getEmail())
            .telephoneClient(panier.getClient().getTelephone())
            .magasin(magasinCheckout)
            .modePaiement(chargerModePaiement(request != null ? request.modePaiementCode() : null))
            .statutFacture(chargerStatutFacture("EMISE"))
            .contexteVente(chargerContexteVente("EN_LIGNE"))
            .montantRemise(BigDecimal.ZERO)
            .montantTotal(BigDecimal.ZERO)
            .montantHtTotal(BigDecimal.ZERO)
            .montantTvaTotal(BigDecimal.ZERO)
            .montantFinal(BigDecimal.ZERO)
            .build();

        for (LignePanier lignePanier : panier.getLignes()) {
            LigneFacture ligneFacture = construireLigneFacture(
                facture,
                lignePanier.getVariant(),
                lignePanier.getQuantite(),
                lignePanier.getPrixUnitaire(),
                null
            );
            facture.getLignes().add(ligneFacture);
        }

        recalculerTotauxFacture(facture);
        if (bonAchat != null) {
            appliquerBonAchat(facture, bonAchat);
        }

        facture = factureRepository.save(facture);
        if (bonAchat != null) {
            marquerBonAchatCommeUtilise(bonAchat, facture);
        }

        fideliteService.traiterFideliteApresFacture(facture);
        fideliteUpgradeService.appliquerUpgradeAutomatique(idClient);
        decrementerStock(facture);
        viderPanier(panier);

        log.info("Checkout web validé : client={} facture={} mode={} magasin={}",
            idClient,
            facture.getReferenceFacture(),
            modeLivraison,
            facture.getMagasin() != null ? facture.getMagasin().getId() : null);
        return factureMapper.toResponse(facture);
    }

    @Override
    public FactureResponse getById(Long id) {
        return factureMapper.toResponse(
            factureRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Facture introuvable : " + id))
        );
    }

    @Override
    public FactureResponse getByIdForClient(Long idClient, Long id) {
        Facture facture = factureRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Facture introuvable : " + id));

        if (facture.getClient() == null || !facture.getClient().getId().equals(idClient)) {
            throw new EntityNotFoundException("Facture introuvable : " + id);
        }

        return factureMapper.toResponse(facture);
    }

    @Override
    public FactureResponse getByReference(String reference) {
        return factureMapper.toResponse(
            factureRepository.findByReferenceFacture(reference)
                .orElseThrow(() -> new EntityNotFoundException("Facture introuvable : " + reference))
        );
    }

    @Override
    public Page<FactureSummary> getByClient(Long idClient, Pageable pageable) {
        return factureRepository.findByClientIdOrderByDateFactureDesc(idClient, pageable)
            .map(factureMapper::toSummary);
    }

    @Override
    public Page<FactureSummary> getByMagasin(Long idMagasin, Pageable pageable) {
        return factureRepository.findByMagasinIdOrderByDateFactureDesc(idMagasin, pageable)
            .map(factureMapper::toSummary);
    }

    @Override
    @Transactional
    public void annuler(Long id) {
        Facture facture = factureRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Facture introuvable : " + id));

        if ("ANNULEE".equals(facture.getStatutFacture().getCode())) {
            throw new IllegalStateException("Facture déjà annulée");
        }

        facture.setStatutFacture(chargerStatutFacture("ANNULEE"));
        factureRepository.save(facture);
        log.info("Facture {} annulée", facture.getReferenceFacture());
    }

    private LigneFacture construireLigneFacture(Facture facture,
                                                ProduitVariant variant,
                                                int quantite,
                                                BigDecimal prixUnitaire,
                                                LigneCommande ligneCommande) {
        BigDecimal taux = resoudreTauxTva(variant);
        BigDecimal montantLigne = prixUnitaire.multiply(BigDecimal.valueOf(quantite));
        BigDecimal montantHt = montantLigne.divide(
            BigDecimal.ONE.add(taux.divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP)),
            2,
            RoundingMode.HALF_UP
        );
        BigDecimal montantTva = montantLigne.subtract(montantHt);

        LigneFacture ligne = LigneFacture.builder()
            .facture(facture)
            .variant(variant)
            .ligneCommande(ligneCommande)
            .quantite(quantite)
            .prixUnitaire(prixUnitaire)
            .tauxTvaApplique(taux)
            .montantLigne(montantLigne)
            .montantHtLigne(montantHt)
            .montantTvaLigne(montantTva)
            .build();

        if (variant.getTauxTva() != null) {
            ligne.setTauxTva(variant.getTauxTva());
        }
        return ligne;
    }

    private BigDecimal resoudreTauxTva(ProduitVariant variant) {
        if (variant.getTauxTva() != null) {
            return variant.getTauxTva().getTaux();
        }
        var tvaDefaut = variant.getProduit().getCategorie().getTypeCategorie().getTauxTvaDefaut();
        if (tvaDefaut != null) {
            return tvaDefaut.getTaux();
        }
        return new BigDecimal("20.00");
    }

    private void recalculerTotauxFacture(Facture facture) {
        BigDecimal total = facture.getLignes().stream()
            .map(LigneFacture::getMontantLigne)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalHt = facture.getLignes().stream()
            .map(LigneFacture::getMontantHtLigne)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalTva = facture.getLignes().stream()
            .map(LigneFacture::getMontantTvaLigne)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        facture.setMontantTotal(total);
        facture.setMontantHtTotal(totalHt);
        facture.setMontantTvaTotal(totalTva);
        facture.setMontantFinal(total.subtract(facture.getMontantRemise()).max(BigDecimal.ZERO));
    }

    private BonAchat chargerBonAchatDisponible(Long idClient, Long idBonAchat) {
        BonAchat bonAchat = bonAchatRepository.findByIdAndClientIdAndUtiliseFalse(idBonAchat, idClient)
            .orElseThrow(() -> new EntityNotFoundException(
                "Bon d'achat introuvable ou déjà utilisé : " + idBonAchat
            ));

        boolean bon10Valide = bonAchat.getPointsUtilises() == 2000
            && VALEUR_BON_10.compareTo(bonAchat.getValeur()) == 0;

        boolean bon20Valide = bonAchat.getPointsUtilises() == 8000
            && VALEUR_BON_20.compareTo(bonAchat.getValeur()) == 0;

        if (!bon10Valide && !bon20Valide) {
            throw new IllegalStateException("Bon d'achat invalide ou non conforme aux règles fidélité");
        }

        return bonAchat;
    }

    private void appliquerBonAchat(Facture facture, BonAchat bonAchat) {
        facture.setBonAchat(bonAchat);
        BigDecimal remiseCalculee = facture.getMontantRemise().add(bonAchat.getValeur());
        BigDecimal remisePlafonnee = remiseCalculee.min(facture.getMontantTotal());
        facture.setMontantRemise(remisePlafonnee);
        facture.setMontantFinal(facture.getMontantTotal().subtract(remisePlafonnee).max(BigDecimal.ZERO));
    }

    private void marquerBonAchatCommeUtilise(BonAchat bonAchat, Facture facture) {
        bonAchat.setUtilise(true);
        bonAchat.setDateUtilisation(LocalDateTime.now());
        bonAchat.setFacture(facture);
        bonAchatRepository.save(bonAchat);
    }

    private void decrementerStock(Facture facture) {
        for (LigneFacture ligne : facture.getLignes()) {
            ProduitVariant variant = ligne.getVariant();
            if (variant.isEstDemat()) {
                continue;
            }

            stockMagasinRepository.findByVariantIdAndMagasinId(
                    variant.getId(),
                    facture.getMagasin().getId()
                )
                .ifPresent(stock -> {
                    int nouvelleQuantite = Math.max(0, stock.getQuantiteNeuf() - ligne.getQuantite());
                    stock.setQuantiteNeuf(nouvelleQuantite);
                    stockMagasinRepository.save(stock);
                });
        }
    }

    private BigDecimal calculerRemisePromo(String codePromo, BigDecimal montant) {
        return BigDecimal.ZERO;
    }

    private String genererReferenceFacture() {
        return "FAC-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private ModePaiement chargerModePaiement(String code) {
        String mode = (code == null || code.isBlank()) ? "CB" : code;
        return modePaiementRepository.findByCode(mode)
            .orElseThrow(() -> new EntityNotFoundException("Mode de paiement introuvable : " + mode));
    }

    private ContexteVente chargerContexteVente(String code) {
        return contexteVenteRepository.findByCode(code)
            .orElseThrow(() -> new EntityNotFoundException("Contexte de vente introuvable : " + code));
    }

    private StatutFacture chargerStatutFacture(String code) {
        return statutFactureRepository.findByCode(code)
            .orElseThrow(() -> new EntityNotFoundException("Statut de facture introuvable : " + code));
    }

    private Adresse chargerAdresseCheckout(Long idClient, Long idAdresse) {
        if (idAdresse != null) {
            Adresse adresse = adresseRepository.findById(idAdresse)
                .orElseThrow(() -> new EntityNotFoundException("Adresse introuvable : " + idAdresse));

            if (adresse.getClient() == null || !adresse.getClient().getId().equals(idClient)) {
                throw new EntityNotFoundException("Adresse introuvable : " + idAdresse);
            }
            return adresse;
        }

        return adresseRepository.findByClientIdAndEstDefautTrue(idClient).orElse(null);
    }

    private String normaliserModeLivraison(String modeLivraisonCode) {
        return (modeLivraisonCode == null || modeLivraisonCode.isBlank()) ? "DOMICILE" : modeLivraisonCode;
    }

    private Magasin chargerMagasinRetraitOuProche(Long idClient, Long idMagasinRetrait, Adresse adresse) {
        if (adresse == null) {
            throw new IllegalStateException("Choisissez une adresse de référence pour le retrait magasin");
        }
        if (idMagasinRetrait != null) {
            return magasinRepository.findByIdAndActifTrue(idMagasinRetrait)
                .orElseThrow(() -> new EntityNotFoundException("Magasin introuvable : " + idMagasinRetrait));
        }
        return resoudreMagasinPourCheckout(adresse);
    }

    private Magasin resoudreMagasinPourCheckout(Adresse adresse) {
        if (adresse != null && adresse.getMagasin() != null) {
            return adresse.getMagasin();
        }

        List<Adresse> adressesMagasins = adresseRepository.findAllMagasinAddressesActives();
        if (!adressesMagasins.isEmpty()) {
            return adressesMagasins.stream()
                .sorted(Comparator
                    .comparingDouble((Adresse a) -> scoreMagasin(adresse, a))
                    .thenComparing(a -> a.getMagasin().getNom(), String.CASE_INSENSITIVE_ORDER))
                .map(Adresse::getMagasin)
                .findFirst()
                .orElseGet(() -> magasinRepository.findByActifTrue().stream().findFirst().orElse(null));
        }

        return magasinRepository.findByActifTrue().stream().findFirst()
            .orElseThrow(() -> new EntityNotFoundException(
                "Aucun magasin actif disponible pour facturer la commande web"
            ));
    }

    private double scoreMagasin(Adresse adresseClient, Adresse adresseMagasin) {
        if (adresseClient == null) {
            return 999.0;
        }

        Double distance = calculerDistanceKm(adresseClient, adresseMagasin);
        if (distance != null) {
            return distance;
        }

        String cpClient = adresseClient.getCodePostal() != null ? adresseClient.getCodePostal().trim() : "";
        String cpMagasin = adresseMagasin.getCodePostal() != null ? adresseMagasin.getCodePostal().trim() : "";
        String villeClient = adresseClient.getVille() != null ? adresseClient.getVille().trim() : "";
        String villeMagasin = adresseMagasin.getVille() != null ? adresseMagasin.getVille().trim() : "";

        if (!cpClient.isBlank() && cpClient.equalsIgnoreCase(cpMagasin)) return 0.05;
        if (!villeClient.isBlank() && villeClient.equalsIgnoreCase(villeMagasin)) return 0.10;
        if (cpClient.length() >= 2 && cpMagasin.length() >= 2 && cpClient.substring(0, 2).equals(cpMagasin.substring(0, 2))) {
            return 0.20;
        }
        return 999.0;
    }

    private Double calculerDistanceKm(Adresse a, Adresse b) {
        if (a == null || a.getLatitude() == null || a.getLongitude() == null || b == null || b.getLatitude() == null || b.getLongitude() == null) {
            return null;
        }

        double lat1 = a.getLatitude().doubleValue();
        double lon1 = a.getLongitude().doubleValue();
        double lat2 = b.getLatitude().doubleValue();
        double lon2 = b.getLongitude().doubleValue();

        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double originLat = Math.toRadians(lat1);
        double targetLat = Math.toRadians(lat2);

        double h = Math.pow(Math.sin(dLat / 2), 2)
            + Math.cos(originLat) * Math.cos(targetLat) * Math.pow(Math.sin(dLon / 2), 2);
        double c = 2 * Math.atan2(Math.sqrt(h), Math.sqrt(1 - h));
        return 6371.0 * c;
    }

    private void viderPanier(Panier panier) {
        panier.getLignes().clear();
        panier.setDateDerniereActivite(LocalDateTime.now());
        panierRepository.save(panier);
    }
}
