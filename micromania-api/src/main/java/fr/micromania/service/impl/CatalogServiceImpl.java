package fr.micromania.service.impl;

import fr.micromania.dto.catalog.*;
import fr.micromania.entity.AuditLog;
import fr.micromania.entity.Client;
import fr.micromania.entity.catalog.AvisProduit;
import fr.micromania.entity.catalog.Categorie;
import fr.micromania.entity.catalog.Produit;
import fr.micromania.entity.catalog.ProduitImage;
import fr.micromania.entity.catalog.ProduitPrix;
import fr.micromania.entity.catalog.ProduitVariant;
import fr.micromania.entity.referentiel.StatutAvis;
import fr.micromania.mapper.CatalogMapper;
import fr.micromania.repository.AuditLogRepository;
import fr.micromania.repository.AvisProduitRepository;
import fr.micromania.repository.CategorieRepository;
import fr.micromania.repository.ClientRepository;
import fr.micromania.repository.ProduitPrixRepository;
import fr.micromania.repository.ProduitRepository;
import fr.micromania.repository.ProduitVariantRepository;
import fr.micromania.repository.StatutAvisRepository;
import fr.micromania.service.CatalogService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class CatalogServiceImpl implements CatalogService {

    private static final String STATUT_AVIS_APPROUVE = "APPROUVE";
    private final AuditLogRepository auditLogRepository;
    private final ProduitRepository produitRepository;
    private final ProduitVariantRepository variantRepository;
    private final ProduitPrixRepository prixRepository;
    private final CategorieRepository categorieRepository;
    private final ClientRepository clientRepository;
    private final AvisProduitRepository avisProduitRepository;
    private final StatutAvisRepository statutAvisRepository;
    private final CatalogMapper catalogMapper;

    @Override
    public ProduitResponse getProduitById(Long id) {
        Produit produit = chargerProduit(id);
        return enrichirProduitDetail(produit);
    }

    @Override
    public ProduitResponse getProduitBySlug(String slug) {
        Produit produit = produitRepository.findBySlugAndDeletedFalse(slug)
                .orElseThrow(() -> new EntityNotFoundException("Produit introuvable : " + slug));
        return enrichirProduitDetail(produit);
    }

    @Override
    public AvisProduitClientResponse getMonAvisProduit(Long idClient, Long idProduit) {
        chargerProduit(idProduit);
        return avisProduitRepository.findByClientIdAndProduitId(idClient, idProduit)
                .map(catalogMapper::toAvisProduitClientResponse)
                .orElse(null);
    }

    @Override
    @Transactional
    public AvisProduitClientResponse soumettreAvisProduit(Long idClient, Long idProduit, CreateAvisProduitRequest request) {
        Produit produit = chargerProduit(idProduit);
        Client client = clientRepository.findByIdAndDeletedFalse(idClient)
                .orElseThrow(() -> new EntityNotFoundException("Client introuvable : " + idClient));

        if (!avisProduitRepository.hasClientPurchasedProduct(idClient, idProduit)) {
            throw new IllegalStateException("Vous devez avoir acheté ce produit avant de laisser un avis");
        }

        StatutAvis statutPublie = statutAvisRepository.findByCode(STATUT_AVIS_APPROUVE)
                .orElseThrow(() -> new EntityNotFoundException("Statut d'avis introuvable : " + STATUT_AVIS_APPROUVE));

        String commentaireNettoye = nettoyerCommentaire(request.commentaire());

        AvisProduit avis = avisProduitRepository.findByClientIdAndProduitId(idClient, idProduit)
                .map(existant -> mettreAJourAvisExistant(existant, request.note(), commentaireNettoye, statutPublie))
                .orElseGet(() -> AvisProduit.builder()
                        .client(client)
                        .produit(produit)
                        .statutAvis(statutPublie)
                        .note(request.note())
                        .commentaire(commentaireNettoye)
                        .build());

        avis = avisProduitRepository.save(avis);
        log.info("Avis produit publié : client={} produit={} avis={} statut={}",
                idClient, idProduit, avis.getId(), avis.getStatutAvis().getCode());
        return catalogMapper.toAvisProduitClientResponse(avis);
    }

    @Override
    public Page<ProduitSummary> search(String query, Long idCategorie, String niveauAccesMin, Pageable pageable) {
        Page<Produit> produits = produitRepository.search(query, idCategorie, niveauAccesMin, pageable);
        Map<Long, AvisStats> avisStatsParProduit = chargerAvisStats(produits.getContent());

        return produits.map(produit ->
                toProduitSummaryAvecStats(produit, avisStatsParProduit.getOrDefault(produit.getId(), AvisStats.EMPTY)));
    }

    @Override
    public List<ProduitSummary> getMisEnAvant() {
        List<Produit> produits = produitRepository.findMisEnAvant();
        Map<Long, AvisStats> avisStatsParProduit = chargerAvisStats(produits);

        return produits.stream()
                .map(produit ->
                        toProduitSummaryAvecStats(produit, avisStatsParProduit.getOrDefault(produit.getId(), AvisStats.EMPTY)))
                .toList();
    }

    @Override
    @Transactional
    public ProduitResponse creerProduit(CreateProduitRequest request) {
        if (produitRepository.existsBySlugAndDeletedFalse(request.slug())) {
            throw new IllegalArgumentException("Slug déjà utilisé : " + request.slug());
        }
        Categorie categorie = categorieRepository.findById(request.idCategorie())
            .orElseThrow(() -> new EntityNotFoundException("Catégorie introuvable : " + request.idCategorie()));

        Produit produit = Produit.builder()
            .categorie(categorie)
            .nom(request.nom())
            .slug(request.slug())
            .description(request.description())
            .resumeCourt(request.resumeCourt())
            .dateSortie(request.dateSortie())
            .editeur(request.editeur())
            .constructeur(request.constructeur())
            .pegi(request.pegi())
            .marque(request.marque())
            .niveauAccesMin(request.niveauAccesMin() != null ? request.niveauAccesMin() : "NORMAL")
            .misEnAvant(request.misEnAvant())
            .actif(true).deleted(false)
            .build();

        produit = produitRepository.save(produit);
        auditer("produit", "CREATE", produit.getId());
        log.info("Produit créé : slug={}", produit.getSlug());
        return enrichirProduitDetail(produit);
    }

    @Override
    @Transactional
    public ProduitResponse modifierProduit(Long id, CreateProduitRequest request) {
        Produit produit = chargerProduit(id);
        if (request.nom()         != null) produit.setNom(request.nom());
        if (request.description() != null) produit.setDescription(request.description());
        if (request.editeur()     != null) produit.setEditeur(request.editeur());
        if (request.pegi()        != null) produit.setPegi(request.pegi());
        produit.setMisEnAvant(request.misEnAvant());
        return enrichirProduitDetail(produitRepository.save(produit));
    }

    @Override
    @Transactional
    public void supprimerProduit(Long id) {
        chargerProduit(id);
        produitRepository.softDelete(id);
        auditer("produit", "DELETE", id);
    }

    @Override
    @Transactional
    public ProduitVariantResponse creerVariant(CreateVariantRequest request) {
        Produit produit = chargerProduit(request.idProduit());
        if (variantRepository.findBySkuAndActifTrue(request.sku()).isPresent()) {
            throw new IllegalArgumentException("SKU déjà utilisé : " + request.sku());
        }
        ProduitVariant variant = ProduitVariant.builder()
            .produit(produit)
            .sku(request.sku())
            .ean(request.ean())
            .nomCommercial(request.nomCommercial())
            .edition(request.edition())
            .couleur(request.couleur())
            .langueVente(request.langueVente() != null ? request.langueVente() : "fr")
            .scelle(request.scelle())
            .estDemat(request.estDemat())
            .estTcgUnitaire(request.estTcgUnitaire())
            .estReprise(request.estReprise())
            .actif(true)
            .build();
        variant = variantRepository.save(variant);

        if (request.prixWeb() != null) {
            prixRepository.desactiverPrixActifs(variant.getId(), 1L);
            ProduitPrix prix = ProduitPrix.builder()
                .variant(variant).prix(request.prixWeb())
                .dateDebut(LocalDateTime.now()).actif(true)
                .build();
            prixRepository.save(prix);
        }
        return catalogMapper.toVariantResponse(variant);
    }

    @Override
    @Transactional
    public ProduitVariantResponse modifierVariant(Long idVariant, CreateVariantRequest request) {
        ProduitVariant variant = variantRepository.findById(idVariant)
            .orElseThrow(() -> new EntityNotFoundException("Variant introuvable : " + idVariant));
        if (request.nomCommercial() != null) variant.setNomCommercial(request.nomCommercial());
        if (request.couleur()       != null) variant.setCouleur(request.couleur());
        return catalogMapper.toVariantResponse(variantRepository.save(variant));
    }

    @Override
    @Transactional
    public PrixResponse setPrice(SetPrixRequest request) {
        prixRepository.desactiverPrixActifs(request.idVariant(), request.idCanalVente());
        ProduitVariant variant = variantRepository.findById(request.idVariant())
            .orElseThrow(() -> new EntityNotFoundException("Variant introuvable : " + request.idVariant()));
        ProduitPrix prix = ProduitPrix.builder()
            .variant(variant).prix(request.prix())
            .dateDebut(request.dateDebut()).dateFin(request.dateFin())
            .actif(true).build();
        ProduitPrix saved = prixRepository.save(prix);
        auditer("produit_prix", "UPDATE", saved.getId());
        return catalogMapper.toPrixResponse(saved);
    }

    @Override
    public List<CategorieResponse> getCategories() {
        return categorieRepository.findByActifTrue().stream()
            .map(catalogMapper::toCategorieResponse).toList();
    }

    @Override
    public CategorieResponse getCategorieById(Long id) {
        return catalogMapper.toCategorieResponse(categorieRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Catégorie introuvable : " + id)));
    }

    private ProduitSummary toProduitSummaryAvecStats(Produit produit, AvisStats avisStats) {
        ProduitSummary base = catalogMapper.toProduitSummary(produit);
        String imageUrl = produit.getImages().stream()
                .filter(ProduitImage::isPrincipale).findFirst()
                .map(ProduitImage::getUrl).orElse(null);
        String imageAlt = produit.getImages().stream()
                .filter(ProduitImage::isPrincipale).findFirst()
                .map(ProduitImage::getAlt).orElse(null);
        return new ProduitSummary(
                base.id(), base.nom(), base.slug(), base.categorie(),
                imageUrl, imageAlt,
                catalogMapper.prixNeuf(produit.getVariants()),
                catalogMapper.prixOccasion(produit.getVariants()),
                produit.getVariants().stream().anyMatch(ProduitVariant::isActif),
                base.misEnAvant(), base.pegi(),
                avisStats.noteMoyenne(), avisStats.nbAvis()
        );
    }

    private ProduitResponse enrichirProduitDetail(Produit produit) {
        ProduitResponse base = catalogMapper.toProduitResponse(produit);

        List<ProduitVariantResponse> variants = produit.getVariants().stream()
                .map(variant -> {
                    BigDecimal prixWeb = variant.getPrix() == null ? null :
                            variant.getPrix().stream()
                            .filter(ProduitPrix::isActif)
                            .filter(p -> p.getCanalVente() != null
                                         && "WEB".equalsIgnoreCase(p.getCanalVente().getCode()))
                            .map(ProduitPrix::getPrix)
                            .findFirst()
                            .orElse(null);

                    BigDecimal prixMagasin = variant.getPrix() == null ? null :
                            variant.getPrix().stream()
                            .filter(ProduitPrix::isActif)
                            .filter(p -> p.getCanalVente() != null
                                         && "MAGASIN".equalsIgnoreCase(p.getCanalVente().getCode()))
                            .map(ProduitPrix::getPrix)
                            .findFirst()
                            .orElse(null);

                    return new ProduitVariantResponse(
                            variant.getId(),
                            variant.getSku(),
                            variant.getEan(),
                            variant.getNomCommercial(),
                            variant.getPlateforme() != null ? catalogMapper.toPlatformeDto(variant.getPlateforme()) : null,
                            variant.getFormatProduit() != null ? variant.getFormatProduit().getCode() : null,
                            variant.getStatutProduit() != null ? variant.getStatutProduit().getCode() : null,
                            variant.getEdition(),
                            variant.getCouleur(),
                            variant.getLangueVente(),
                            variant.isScelle(),
                            variant.isEstDemat(),
                            variant.isEstTcgUnitaire(),
                            variant.isEstReprise(),
                            variant.isNecessiteNumeroSerie(),
                            variant.getTauxTva() != null ? catalogMapper.toTauxTvaDto(variant.getTauxTva()) : null,
                            prixWeb,
                            prixMagasin,
                            variant.isActif()
                    );
                })
                .toList();

        Double noteMoyenne = arrondirNote(avisProduitRepository.findNoteMoyenne(produit.getId()));
        long nbAvis = avisProduitRepository.countByProduitIdAndStatutAvisCode(produit.getId(), STATUT_AVIS_APPROUVE);
        List<AvisProduitPublicResponse> avis = avisProduitRepository
                .findTop5ByProduitIdAndStatutAvisCodeOrderByDateCreationDesc(produit.getId(), STATUT_AVIS_APPROUVE)
                .stream()
                .map(catalogMapper::toAvisProduitPublicResponse)
                .toList();

        return new ProduitResponse(
                base.id(),
                base.nom(),
                base.slug(),
                base.description(),
                base.resumeCourt(),
                base.dateSortie(),
                base.editeur(),
                base.constructeur(),
                base.pegi(),
                base.marque(),
                base.niveauAccesMin(),
                base.langue(),
                base.misEnAvant(),
                base.categorie(),
                variants,
                base.images(),
                noteMoyenne,
                nbAvis,
                avis
        );
    }

    private Produit chargerProduit(Long idProduit) {
        return produitRepository.findByIdAndDeletedFalse(idProduit)
                .orElseThrow(() -> new EntityNotFoundException("Produit introuvable : " + idProduit));
    }

    private AvisProduit mettreAJourAvisExistant(AvisProduit avis, byte note, String commentaire, StatutAvis statutPublie) {
        avis.setNote(note);
        avis.setCommentaire(commentaire);
        avis.setStatutAvis(statutPublie);
        avis.setEmployeModerateur(null);
        avis.setMotifModeration(null);
        avis.setDateModeration(null);
        return avis;
    }

    private String nettoyerCommentaire(String commentaire) {
        if (commentaire == null) {
            return null;
        }
        String nettoye = commentaire.trim();
        return nettoye.isEmpty() ? null : nettoye;
    }

    private Map<Long, AvisStats> chargerAvisStats(List<Produit> produits) {
        List<Long> ids = produits.stream().map(Produit::getId).toList();
        if (ids.isEmpty()) {
            return Map.of();
        }

        return avisProduitRepository.findStatsByProduitIds(ids).stream()
                .collect(Collectors.toMap(
                        AvisProduitRepository.ProduitAvisStatsProjection::getProduitId,
                        projection -> new AvisStats(arrondirNote(projection.getNoteMoyenne()), projection.getNbAvis()),
                        (gauche, droite) -> gauche
                ));
    }

    private Double arrondirNote(Double note) {
        if (note == null) {
            return null;
        }
        return BigDecimal.valueOf(note)
                .setScale(1, RoundingMode.HALF_UP)
                .doubleValue();
    }

    private record AvisStats(Double noteMoyenne, long nbAvis) {
        private static final AvisStats EMPTY = new AvisStats(null, 0);
    }

    private void auditer(String tableName, String operationType, Long recordId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userIdentifier = (auth != null && auth.getPrincipal() != null)
            ? auth.getPrincipal().toString()
            : "system";
        auditLogRepository.save(AuditLog.builder()
            .tableName(tableName)
            .operationType(operationType)
            .recordId(recordId)
            .userIdentifier(userIdentifier)
            .build());
    }
}
