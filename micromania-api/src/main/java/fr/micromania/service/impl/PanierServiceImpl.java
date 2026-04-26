package fr.micromania.service.impl;

import fr.micromania.dto.panier.*;
import fr.micromania.entity.catalog.ProduitVariant;
import fr.micromania.entity.Client;
import fr.micromania.entity.commande.LignePanier;
import fr.micromania.entity.commande.Panier;
import fr.micromania.entity.referentiel.CanalVente;
import fr.micromania.entity.referentiel.StatutPanier;
import fr.micromania.entity.referentiel.TypeGarantie;
import fr.micromania.mapper.PanierMapper;
import fr.micromania.repository.CanalVenteRepository;
import fr.micromania.repository.ClientRepository;
import fr.micromania.repository.LignePanierRepository;
import fr.micromania.repository.PanierRepository;
import fr.micromania.repository.ProduitVariantRepository;
import fr.micromania.repository.StatutPanierRepository;
import fr.micromania.repository.StockMagasinRepository;
import fr.micromania.repository.TypeGarantieRepository;
import fr.micromania.service.PanierService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PanierServiceImpl implements PanierService {

    private final PanierRepository panierRepository;
    private final LignePanierRepository lignePanierRepository;
    private final ProduitVariantRepository variantRepository;
    private final ClientRepository clientRepository;
    private final StatutPanierRepository statutPanierRepository;
    private final CanalVenteRepository canalVenteRepository;
    private final StockMagasinRepository stockMagasinRepository;
    private final TypeGarantieRepository typeGarantieRepository;
    private final PanierMapper panierMapper;

    @Override
    @Transactional
    public PanierResponse getPanierActif(Long idClient, String canalVente) {
        Panier panier = panierRepository.findPanierActif(idClient, canalVente)
            .orElseGet(() -> creerPanierVide(idClient, canalVente));
        return enrichirPanier(panier);
    }

    @Override
    @Transactional
    public PanierResponse addLigne(Long idClient, AddLignePanierRequest request) {
        CanalVente canalVente = canalVenteRepository.findById(request.idCanalVente())
            .orElseThrow(() -> new EntityNotFoundException("Canal de vente introuvable : " + request.idCanalVente()));

        Panier panier = panierRepository.findPanierActif(idClient, canalVente.getCode())
            .orElseGet(() -> creerPanierVide(idClient, canalVente.getCode()));

        ProduitVariant variant = variantRepository.findById(request.idVariant())
            .orElseThrow(() -> new EntityNotFoundException("Variant introuvable : " + request.idVariant()));

        verifierStock(variant, request.quantite());

        TypeGarantie typeGarantie = null;
        if (request.idTypeGarantie() != null) {
            typeGarantie = typeGarantieRepository.findById(request.idTypeGarantie())
                .orElse(null);
        }
        final TypeGarantie garantieFinale = typeGarantie;

        lignePanierRepository.findByPanierIdAndVariantId(panier.getId(), variant.getId())
            .ifPresentOrElse(
                ligne -> {
                    ligne.setQuantite(ligne.getQuantite() + request.quantite());
                    if (garantieFinale != null) ligne.setTypeGarantie(garantieFinale);
                    lignePanierRepository.save(ligne);
                },
                () -> {
                    BigDecimal prix = resoudrePrix(variant);
                    LignePanier ligne = LignePanier.builder()
                        .panier(panier)
                        .variant(variant)
                        .quantite(request.quantite())
                        .prixUnitaire(prix)
                        .typeGarantie(garantieFinale)
                        .build();
                    lignePanierRepository.save(ligne);
                    panier.getLignes().add(ligne);
                }
            );

        panier.setDateDerniereActivite(LocalDateTime.now());
        panierRepository.save(panier);
        return enrichirPanier(panier);
    }

    @Override
    @Transactional
    public PanierResponse updateLigne(Long idClient, Long idLigne, UpdateLignePanierRequest request) {
        LignePanier ligne = lignePanierRepository.findById(idLigne)
            .orElseThrow(() -> new EntityNotFoundException("Ligne panier introuvable : " + idLigne));

        validerAppartenance(ligne, idClient);
        verifierStock(ligne.getVariant(), request.quantite());
        ligne.setQuantite(request.quantite());
        lignePanierRepository.save(ligne);
        ligne.getPanier().setDateDerniereActivite(LocalDateTime.now());
        panierRepository.save(ligne.getPanier());
        return enrichirPanier(ligne.getPanier());
    }

    @Override
    @Transactional
    public PanierResponse removeLigne(Long idClient, Long idLigne) {
        LignePanier ligne = lignePanierRepository.findById(idLigne)
            .orElseThrow(() -> new EntityNotFoundException("Ligne panier introuvable : " + idLigne));

        validerAppartenance(ligne, idClient);
        Panier panier = ligne.getPanier();
        panier.getLignes().remove(ligne);
        lignePanierRepository.delete(ligne);
        panier.setDateDerniereActivite(LocalDateTime.now());
        panierRepository.save(panier);
        return enrichirPanier(panier);
    }

    @Override
    @Transactional
    public void vider(Long idClient, String canalVente) {
        panierRepository.findPanierActif(idClient, canalVente).ifPresent(p -> {
            lignePanierRepository.deleteByPanierId(p.getId());
            p.getLignes().clear();
            p.setDateDerniereActivite(LocalDateTime.now());
            panierRepository.save(p);
        });
    }

    @Override
    @Transactional
    public PanierResponse appliquerCodePromo(Long idClient, String codePromo, String canalVente) {
        Panier panier = panierRepository.findPanierActif(idClient, canalVente)
            .orElseThrow(() -> new EntityNotFoundException("Panier introuvable"));
        panier.setCodePromo(codePromo);
        panier.setDateDerniereActivite(LocalDateTime.now());
        panierRepository.save(panier);
        return enrichirPanier(panier);
    }

    @Override
    @Transactional
    public PanierResponse retirerCodePromo(Long idClient, String canalVente) {
        Panier panier = panierRepository.findPanierActif(idClient, canalVente)
            .orElseThrow(() -> new EntityNotFoundException("Panier introuvable"));
        panier.setCodePromo(null);
        panier.setDateDerniereActivite(LocalDateTime.now());
        panierRepository.save(panier);
        return enrichirPanier(panier);
    }

    private void verifierStock(ProduitVariant variant, int quantiteDemandee) {
        if (variant.isEstDemat()) {
            return;
        }
        int stockTotal = stockMagasinRepository.findByVariantId(variant.getId()).stream()
                .mapToInt(s -> s.getQuantiteNeuf())
                .sum();
        if (stockTotal < quantiteDemandee) {
            throw new IllegalStateException("Stock insuffisant pour ce produit");
        }
    }

    private Panier creerPanierVide(Long idClient, String canalCode) {
        Client client = clientRepository.findById(idClient)
            .orElseThrow(() -> new EntityNotFoundException("Client introuvable : " + idClient));

        StatutPanier statutActif = statutPanierRepository.findByCode("ACTIF")
            .orElseThrow(() -> new EntityNotFoundException("Statut panier ACTIF introuvable"));

        CanalVente canalVente = canalVenteRepository.findByCode(canalCode)
            .orElseThrow(() -> new EntityNotFoundException("Canal de vente introuvable : " + canalCode));

        Panier panier = Panier.builder()
            .client(client)
            .statutPanier(statutActif)
            .canalVente(canalVente)
            .codePromo(null)
            .dateCreation(LocalDateTime.now())
            .dateDerniereActivite(LocalDateTime.now())
            .build();

        return panierRepository.save(panier);
    }

    private BigDecimal resoudrePrix(ProduitVariant variant) {
        String statut = variant.getStatutProduit() != null
                ? variant.getStatutProduit().getCode().toUpperCase() : "NEUF";
        return variant.getPrix().stream()
            .filter(fr.micromania.entity.catalog.ProduitPrix::isActif)
            .map(p -> switch (statut) {
                case "OCCASION" -> p.getPrixOccasion();
                case "LOCATION" -> p.getPrixLocation();
                case "REPRISE"  -> p.getPrixReprise();
                default         -> p.getPrixNeuf();
            })
            .filter(px -> px != null)
            .findFirst()
            .orElseThrow(() -> new IllegalStateException(
                "Aucun prix actif pour variant=" + variant.getSku()));
    }

    private PanierResponse enrichirPanier(Panier panier) {
        List<LignePanierResponse> lignes = panierMapper.toLignePanierResponseList(panier.getLignes());
        BigDecimal sousTotal = lignes.stream()
            .map(LignePanierResponse::montantLigne)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalGaranties = lignes.stream()
            .filter(l -> l.garantiePrix() != null)
            .map(LignePanierResponse::garantiePrix)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal total = sousTotal.add(totalGaranties);
        PanierResponse base = panierMapper.toPanierResponse(panier);
        return new PanierResponse(
            base.id(), base.statutPanier(), base.canalVente(), base.codePromo(),
            lignes, sousTotal, BigDecimal.ZERO, total, base.dateDerniereActivite()
        );
    }

    private void validerAppartenance(LignePanier ligne, Long idClient) {
        if (!ligne.getPanier().getClient().getId().equals(idClient)) {
            throw new SecurityException("Ligne panier n'appartient pas au client " + idClient);
        }
    }
}
