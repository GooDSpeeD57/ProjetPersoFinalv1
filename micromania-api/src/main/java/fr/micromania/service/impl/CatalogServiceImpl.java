package fr.micromania.service.impl;

import fr.micromania.dto.catalog.*;
import fr.micromania.entity.AuditLog;
import fr.micromania.entity.Client;
import fr.micromania.specification.ProduitSpecification;
import fr.micromania.specification.VariantCatalogueSpecification;
import org.springframework.data.jpa.domain.Specification;
import fr.micromania.entity.catalog.AvisProduit;
import fr.micromania.entity.catalog.Categorie;
import fr.micromania.entity.catalog.Produit;
import fr.micromania.entity.catalog.ProduitImage;
import fr.micromania.entity.catalog.ProduitPrix;
import fr.micromania.entity.catalog.ProduitVariant;
import fr.micromania.entity.catalog.ProduitScreenshot;
import fr.micromania.entity.catalog.ProduitVideo;
import fr.micromania.entity.referentiel.FormatProduit;
import fr.micromania.entity.referentiel.Plateforme;
import fr.micromania.entity.referentiel.StatutAvis;
import fr.micromania.entity.referentiel.StatutProduit;
import fr.micromania.mapper.CatalogMapper;
import fr.micromania.dto.catalog.CataloguePosSummary;
import fr.micromania.entity.stock.StockMagasin;
import fr.micromania.repository.AuditLogRepository;
import fr.micromania.repository.AvisProduitRepository;
import fr.micromania.repository.CategorieRepository;
import fr.micromania.repository.ClientRepository;
import fr.micromania.repository.ProduitPrixRepository;
import fr.micromania.repository.ProduitRepository;
import fr.micromania.repository.ProduitVariantRepository;
import fr.micromania.repository.StatutAvisRepository;
import fr.micromania.repository.StockMagasinRepository;
import fr.micromania.repository.FormatProduitRepository;
import fr.micromania.repository.ProduitImageRepository;
import fr.micromania.repository.ProduitVideoRepository;
import fr.micromania.repository.PlateformeRepository;
import fr.micromania.repository.StatutProduitRepository;
import fr.micromania.dto.catalog.CreateProduitImageRequest;
import fr.micromania.service.CatalogService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class CatalogServiceImpl implements CatalogService {

    private static final String STATUT_AVIS_APPROUVE = "APPROUVE";

    @Value("${app.media.images-path:./media/images}")
    private String imagesPath;
    private final AuditLogRepository auditLogRepository;
    private final ProduitRepository produitRepository;
    private final ProduitVariantRepository variantRepository;
    private final ProduitPrixRepository prixRepository;
    private final CategorieRepository categorieRepository;
    private final ClientRepository clientRepository;
    private final AvisProduitRepository avisProduitRepository;
    private final StatutAvisRepository statutAvisRepository;
    private final CatalogMapper catalogMapper;
    private final StockMagasinRepository stockMagasinRepository;
    private final ProduitImageRepository produitImageRepository;
    private final PlateformeRepository   plateformeRepository;
    private final FormatProduitRepository formatProduitRepository;
    private final StatutProduitRepository statutProduitRepository;
    private final ProduitVideoRepository     produitVideoRepository;
    private final fr.micromania.repository.ProduitScreenshotRepository screenshotRepository;
    private final fr.micromania.repository.EditionProduitRepository editionProduitRepository;

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
    public boolean peutSoumettreAvis(Long idClient, Long idProduit) {
        chargerProduit(idProduit);
        return avisProduitRepository.hasClientPurchasedProduct(idClient, idProduit);
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
    public Page<ProduitSummary> search(String query, Long idCategorie, String niveauAccesMin,
                                       String plateforme, String famille, String etat, String tri, Pageable pageable) {
        Specification<Produit> spec = Specification.where(ProduitSpecification.actif())
                .and(ProduitSpecification.avecRecherche(query))
                .and(ProduitSpecification.avecCategorie(idCategorie))
                .and(ProduitSpecification.avecNiveau(niveauAccesMin))
                .and(ProduitSpecification.avecPlateforme(mapPlatformeCodes(plateforme)))
                .and(ProduitSpecification.avecFamille(mapFamilleCode(famille)))
                .and(ProduitSpecification.avecEtat(etat));

        // Tris JPA natifs (champs directs sur Produit)
        if ("nouveautes".equals(tri) || "sortie_desc".equals(tri) || "sortie_asc".equals(tri)) {
            // Pour "nouveautes" : filtre sur les 30 derniers jours + tri dateSortie DESC
            if ("nouveautes".equals(tri)) {
                java.time.LocalDate limite = java.time.LocalDate.now().minusDays(30);
                spec = spec.and((root, cq, cb) ->
                        cb.greaterThanOrEqualTo(root.get("dateSortie"), limite));
            }
            Sort jpaSort = switch (tri) {
                case "sortie_desc" -> Sort.by("dateSortie").descending();
                case "sortie_asc"  -> Sort.by("dateSortie").ascending();
                default            -> Sort.by("dateSortie").descending(); // nouveautes
            };
            Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), jpaSort);
            Page<Produit> produits = produitRepository.findAll(spec, sortedPageable);
            Map<Long, AvisStats> stats = chargerAvisStats(produits.getContent());
            return produits.map(p -> toProduitSummaryAvecStats(p, stats.getOrDefault(p.getId(), AvisStats.EMPTY)));
        }

        // Tris sur champs calculés (prix, note, ventes) : chargement complet + tri en mémoire
        if (tri != null && !tri.isBlank()) {
            List<Produit> tous = produitRepository.findAll(spec);
            Map<Long, AvisStats> stats = chargerAvisStats(tous);

            List<ProduitSummary> summaries = tous.stream()
                    .map(p -> toProduitSummaryAvecStats(p, stats.getOrDefault(p.getId(), AvisStats.EMPTY)))
                    .collect(Collectors.toCollection(ArrayList::new));

            Comparator<ProduitSummary> comparator = switch (tri) {
                case "prix_asc"  -> Comparator.comparing(p ->
                        p.prixNeuf() != null ? p.prixNeuf()
                        : (p.prixOccasion() != null ? p.prixOccasion() : BigDecimal.valueOf(999_999)));
                case "prix_desc" -> Comparator.comparing(
                        (ProduitSummary p) -> p.prixNeuf() != null ? p.prixNeuf()
                        : (p.prixOccasion() != null ? p.prixOccasion() : BigDecimal.ZERO)).reversed();
                case "note"   -> Comparator.comparing(
                        (ProduitSummary p) -> p.noteMoyenne() != null ? p.noteMoyenne() : 0.0).reversed();
                case "ventes" -> Comparator.comparingLong(ProduitSummary::nbAvis).reversed();
                default       -> Comparator.comparing(p -> 0);
            };
            summaries.sort(comparator);

            int fromIndex = (int) pageable.getOffset();
            int toIndex   = Math.min(fromIndex + pageable.getPageSize(), summaries.size());
            List<ProduitSummary> pageContent = fromIndex < summaries.size()
                    ? summaries.subList(fromIndex, toIndex)
                    : Collections.emptyList();
            return new PageImpl<>(pageContent, pageable, summaries.size());
        }

        // Tri par défaut (aucun tri spécifique)
        Page<Produit> produits = produitRepository.findAll(spec, pageable);
        Map<Long, AvisStats> avisStatsParProduit = chargerAvisStats(produits.getContent());
        return produits.map(produit ->
                toProduitSummaryAvecStats(produit, avisStatsParProduit.getOrDefault(produit.getId(), AvisStats.EMPTY)));
    }

    /** Mappe la valeur UI (ps4, xbox, switch...) vers les codes exacts de la table plateforme. */
    private List<String> mapPlatformeCodes(String plateforme) {
        if (plateforme == null || plateforme.isBlank()) return List.of();
        return switch (plateforme.toLowerCase()) {
            case "ps4"         -> List.of("PS4");
            case "ps5"         -> List.of("PS5");
            case "xbox"        -> List.of("XBOX", "XBOX_ONE", "XBOX_360");
            case "xbox-series" -> List.of("XBOX_SERIES");
            case "switch"      -> List.of("SWITCH");
            case "switch2"     -> List.of("SWITCH2");
            case "pc"          -> List.of("PC");
            default            -> List.of(plateforme.toUpperCase());
        };
    }

    /** Mappe la valeur UI (jeux, consoles, accessoires) vers le code typeCategorie en base. */
    private String mapFamilleCode(String famille) {
        if (famille == null || famille.isBlank()) return null;
        return switch (famille.toLowerCase()) {
            case "jeux"           -> "jeu";
            case "consoles"       -> "console";
            case "accessoires"    -> "accessoire";
            case "cartes-cadeau"  -> "carte_cadeau";
            default               -> famille.toLowerCase();
        };
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

    // ── Catalogue variant-par-variant ─────────────────────────────────────────

    @Override
    public Page<VariantCatalogueSummary> searchCatalogue(String q, Long idCategorie,
                                                          String plateforme, String famille,
                                                          String etat, String edition,
                                                          String tri, Pageable pageable) {
        Specification<ProduitVariant> spec = VariantCatalogueSpecification.actif()
                .and(VariantCatalogueSpecification.avecRecherche(q))
                .and(VariantCatalogueSpecification.avecCategorie(idCategorie))
                .and(VariantCatalogueSpecification.avecFamille(mapFamilleCode(famille)))
                .and(VariantCatalogueSpecification.avecPlateforme(plateforme))
                .and(VariantCatalogueSpecification.avecStatut(etat))
                .and(VariantCatalogueSpecification.avecEdition(edition));

        // ── Tris JPA natifs : chargement complet + tri Java + pagination manuelle ──
        // (évite le problème count-query/orderBy avec les Specifications paginées)
        if (tri != null && (tri.equals("sortie_desc") || tri.equals("sortie_asc") || tri.equals("nouveautes"))) {
            List<ProduitVariant> tous = variantRepository.findAll(spec);

            // Pour "nouveautes" : garder uniquement les produits sortis dans les 30 derniers jours
            if ("nouveautes".equals(tri)) {
                java.time.LocalDate limite = java.time.LocalDate.now().minusDays(30);
                tous = tous.stream()
                        .filter(v -> v.getProduit() != null
                                && v.getProduit().getDateSortie() != null
                                && !v.getProduit().getDateSortie().isBefore(limite))
                        .collect(Collectors.toList());
            }

            Comparator<ProduitVariant> comparator = switch (tri) {
                case "sortie_desc" -> Comparator.comparing(
                        v -> v.getProduit() != null ? v.getProduit().getDateSortie() : null,
                        Comparator.nullsLast(Comparator.reverseOrder()));
                case "sortie_asc"  -> Comparator.comparing(
                        v -> v.getProduit() != null ? v.getProduit().getDateSortie() : null,
                        Comparator.nullsLast(Comparator.naturalOrder()));
                default            -> Comparator.comparing(   // nouveautes
                        v -> v.getProduit() != null ? v.getProduit().getDateSortie() : null,
                        Comparator.nullsLast(Comparator.reverseOrder()));
            };
            tous.sort(comparator);

            List<Produit> produitsDistincts = tous.stream()
                    .map(ProduitVariant::getProduit).filter(p -> p != null)
                    .collect(Collectors.toMap(Produit::getId, p -> p, (a, b) -> a))
                    .values().stream().toList();
            Map<Long, AvisStats> statsMap = chargerAvisStats(produitsDistincts);

            List<VariantCatalogueSummary> summaries = tous.stream()
                    .map(v -> toVariantCatalogueSummary(v, statsMap.getOrDefault(v.getProduit().getId(), AvisStats.EMPTY)))
                    .collect(Collectors.toList());

            int fromIndex = (int) pageable.getOffset();
            int toIndex   = Math.min(fromIndex + pageable.getPageSize(), summaries.size());
            List<VariantCatalogueSummary> pageContent = fromIndex >= summaries.size()
                    ? List.of() : summaries.subList(fromIndex, toIndex);
            return new PageImpl<>(pageContent, pageable, summaries.size());
        }

        // ── Tris en mémoire (prix, note, ventes) ─────────────────────────────
        if (tri != null && !tri.isBlank()) {
            List<ProduitVariant> tous = variantRepository.findAll(spec);
            List<Produit> produitsDistincts = tous.stream()
                    .map(ProduitVariant::getProduit).filter(p -> p != null)
                    .collect(Collectors.toMap(Produit::getId, p -> p, (a, b) -> a))
                    .values().stream().toList();
            Map<Long, AvisStats> statsMap = chargerAvisStats(produitsDistincts);

            List<VariantCatalogueSummary> summaries = tous.stream()
                    .map(v -> toVariantCatalogueSummary(v, statsMap.getOrDefault(v.getProduit().getId(), AvisStats.EMPTY)))
                    .collect(Collectors.toCollection(ArrayList::new));

            Comparator<VariantCatalogueSummary> comparator = switch (tri) {
                case "prix_asc"  -> Comparator.comparing(v ->
                        v.prix() != null ? v.prix() : BigDecimal.valueOf(999_999));
                case "prix_desc" -> Comparator.comparing((VariantCatalogueSummary v) ->
                        v.prix() != null ? v.prix() : BigDecimal.ZERO).reversed();
                case "note"   -> Comparator.comparing(
                        (VariantCatalogueSummary v) -> v.noteMoyenne() != null ? v.noteMoyenne() : 0.0).reversed();
                case "ventes" -> Comparator.comparingLong(VariantCatalogueSummary::nbAvis).reversed();
                default       -> Comparator.comparing(v -> 0);
            };
            summaries.sort(comparator);

            int fromIndex = (int) pageable.getOffset();
            int toIndex   = Math.min(fromIndex + pageable.getPageSize(), summaries.size());
            List<VariantCatalogueSummary> pageContent = fromIndex >= summaries.size()
                    ? List.of() : summaries.subList(fromIndex, toIndex);
            return new PageImpl<>(pageContent, pageable, summaries.size());
        }

        // ── Tri par défaut ───────────────────────────────────────────────────
        Page<ProduitVariant> page =
                variantRepository.findAll(spec, PageRequest.of(
                        pageable.getPageNumber(),
                        pageable.getPageSize(),
                        pageable.getSort()
                ));

        // Stats avis par produit (dédupliqués)
        List<Produit> produitsDistincts = page.getContent().stream()
                .map(ProduitVariant::getProduit)
                .filter(p -> p != null)
                .collect(Collectors.toMap(Produit::getId, p -> p, (a, b) -> a))
                .values().stream().toList();
        Map<Long, AvisStats> statsMap = chargerAvisStats(produitsDistincts);

        return page.map(v ->
                toVariantCatalogueSummary(v, statsMap.getOrDefault(v.getProduit().getId(), AvisStats.EMPTY)));
    }

    private VariantCatalogueSummary toVariantCatalogueSummary(ProduitVariant v, AvisStats stats) {
        Produit p = v.getProduit();

        // Prix catalogue : prixNeuf si statut NEUF, prixOccasion si statut OCCASION
        String statut = v.getStatutProduit() != null ? v.getStatutProduit().getCode() : "";
        BigDecimal prix = v.getPrix() == null ? null : v.getPrix().stream()
                .filter(ProduitPrix::isActif)
                .map(px -> "OCCASION".equalsIgnoreCase(statut) ? px.getPrixOccasion() : px.getPrixNeuf())
                .filter(val -> val != null)
                .findFirst().orElse(null);

        // Image du variant courant en priorité ; fallback sur les autres variants du produit
        // (cas typique : variant OCCASION sans images propres → on prend l'image du variant NEUF)
        String imageUrl = null, imageAlt = null;
        ProduitImage img = resolveImage(v, p);
        if (img != null) {
            imageUrl = img.getUrl();
            imageAlt = img.getAlt();
        }

        return new VariantCatalogueSummary(
                v.getId(),
                p.getId(),
                p.getNom(),
                p.getSlug(),
                p.getCategorie() != null ? p.getCategorie().getNom() : null,
                p.getCategorie() != null && p.getCategorie().getTypeCategorie() != null
                        ? p.getCategorie().getTypeCategorie().getCode() : null,
                v.getPlateforme() != null ? v.getPlateforme().getLibelle() : null,
                v.getStatutProduit() != null ? v.getStatutProduit().getCode() : null,
                v.getEdition() != null ? v.getEdition().getLibelle() : null,
                imageUrl,
                imageAlt,
                prix,
                p.isMisEnAvant(),
                p.isEstPreCommande(),
                p.getPegi(),
                stats.noteMoyenne(),
                stats.nbAvis()
        );
    }

    /**
     * Résout l'image principale à afficher pour un variant.
     * Priorité :
     *  1. Image principale du variant lui-même
     *  2. N'importe quelle image du variant lui-même
     *  3. Image principale d'un autre variant actif du même produit (ex : NEUF pour un OCCASION)
     *  4. N'importe quelle image des autres variants actifs du produit
     */
    private ProduitImage resolveImage(ProduitVariant variant, Produit produit) {
        // 1 & 2 — images propres au variant
        if (variant.getImages() != null && !variant.getImages().isEmpty()) {
            return variant.getImages().stream()
                    .filter(ProduitImage::isPrincipale)
                    .findFirst()
                    .orElse(variant.getImages().get(0));
        }
        // 3 & 4 — fallback : autres variants actifs du produit
        if (produit.getVariants() != null) {
            return produit.getVariants().stream()
                    .filter(ProduitVariant::isActif)
                    .filter(other -> !other.getId().equals(variant.getId()))
                    .filter(other -> other.getImages() != null && !other.getImages().isEmpty())
                    .flatMap(other -> other.getImages().stream())
                    .filter(ProduitImage::isPrincipale)
                    .findFirst()
                    .or(() -> produit.getVariants().stream()
                            .filter(ProduitVariant::isActif)
                            .filter(other -> !other.getId().equals(variant.getId()))
                            .filter(other -> other.getImages() != null && !other.getImages().isEmpty())
                            .flatMap(other -> other.getImages().stream())
                            .findFirst())
                    .orElse(null);
        }
        return null;
    }

    // ─────────────────────────────────────────────────────────────────────────

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
            .langue(request.langue() != null && !request.langue().isBlank() ? request.langue() : "fr")
            .misEnAvant(request.misEnAvant())
            .estPreCommande(request.estPreCommande())
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

        // Vérifier que le nouveau slug n'est pas déjà pris par un autre produit
        if (request.slug() != null && !request.slug().equals(produit.getSlug())
                && produitRepository.existsBySlugAndDeletedFalse(request.slug())) {
            throw new IllegalArgumentException("Slug déjà utilisé : " + request.slug());
        }

        if (request.nom()         != null) produit.setNom(request.nom());
        if (request.slug()        != null) produit.setSlug(request.slug());
        if (request.description() != null) produit.setDescription(request.description());
        if (request.resumeCourt() != null) produit.setResumeCourt(request.resumeCourt());
        if (request.dateSortie()  != null) produit.setDateSortie(request.dateSortie());
        if (request.editeur()     != null) produit.setEditeur(request.editeur());
        if (request.constructeur()!= null) produit.setConstructeur(request.constructeur());
        if (request.pegi()        != null) produit.setPegi(request.pegi());
        if (request.marque()      != null) produit.setMarque(request.marque());
        if (request.niveauAccesMin() != null) produit.setNiveauAccesMin(request.niveauAccesMin());
        if (request.langue()      != null && !request.langue().isBlank())
            produit.setLangue(request.langue());
        if (request.idCategorie() != null) {
            Categorie cat = categorieRepository.findById(request.idCategorie())
                .orElseThrow(() -> new EntityNotFoundException("Catégorie introuvable : " + request.idCategorie()));
            produit.setCategorie(cat);
        }
        produit.setMisEnAvant(request.misEnAvant());
        produit.setEstPreCommande(request.estPreCommande());
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
        FormatProduit formatProduit = formatProduitRepository.findById(request.idFormatProduit())
            .orElseThrow(() -> new EntityNotFoundException("Format produit introuvable : " + request.idFormatProduit()));
        StatutProduit statutProduit = statutProduitRepository.findById(request.idStatutProduit())
            .orElseThrow(() -> new EntityNotFoundException("Statut produit introuvable : " + request.idStatutProduit()));

        Plateforme plateforme = null;
        if (request.idPlateforme() != null) {
            plateforme = plateformeRepository.findById(request.idPlateforme())
                .orElseThrow(() -> new EntityNotFoundException("Plateforme introuvable : " + request.idPlateforme()));
        }
        fr.micromania.entity.referentiel.EditionProduit edition = null;
        if (request.idEdition() != null) {
            edition = editionProduitRepository.findById(request.idEdition())
                .orElseThrow(() -> new EntityNotFoundException("Edition introuvable : " + request.idEdition()));
        }

        ProduitVariant variant = ProduitVariant.builder()
            .produit(produit)
            .sku(request.sku())
            .ean(request.ean())
            .nomCommercial(request.nomCommercial())
            .plateforme(plateforme)
            .edition(edition)
            .couleur(request.couleur())
            .langueVente(request.langueVente() != null ? request.langueVente() : "fr")
            .scelle(request.scelle())
            .estDemat(request.estDemat())
            .estTcgUnitaire(request.estTcgUnitaire())
            .estReprise(request.estReprise())
            .formatProduit(formatProduit)
            .statutProduit(statutProduit)
            .actif(true)
            .build();
        variant = variantRepository.save(variant);

        if (request.prixNeuf() != null || request.prixOccasion() != null
                || request.prixReprise() != null || request.prixLocation() != null) {
            prixRepository.desactiverPrixActifs(variant.getId());
            prixRepository.save(ProduitPrix.builder()
                .variant(variant)
                .prixNeuf(request.prixNeuf())
                .prixOccasion(request.prixOccasion())
                .prixReprise(request.prixReprise())
                .prixLocation(request.prixLocation())
                .dateDebut(LocalDateTime.now()).actif(true)
                .build());
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
        if (request.idEdition() != null) {
            fr.micromania.entity.referentiel.EditionProduit ed =
                editionProduitRepository.findById(request.idEdition())
                    .orElseThrow(() -> new EntityNotFoundException("Edition introuvable : " + request.idEdition()));
            variant.setEdition(ed);
        }

        if (request.idPlateforme() != null) {
            Plateforme plateforme = plateformeRepository.findById(request.idPlateforme())
                .orElseThrow(() -> new EntityNotFoundException("Plateforme introuvable : " + request.idPlateforme()));
            variant.setPlateforme(plateforme);
        }
        if (request.idStatutProduit() != null) {
            StatutProduit statut = statutProduitRepository.findById(request.idStatutProduit())
                .orElseThrow(() -> new EntityNotFoundException("Statut introuvable : " + request.idStatutProduit()));
            variant.setStatutProduit(statut);
        }
        if (request.idFormatProduit() != null) {
            FormatProduit format = formatProduitRepository.findById(request.idFormatProduit())
                .orElseThrow(() -> new EntityNotFoundException("Format introuvable : " + request.idFormatProduit()));
            variant.setFormatProduit(format);
        }

        final ProduitVariant savedVariant = variantRepository.save(variant);

        if (request.prixNeuf() != null || request.prixOccasion() != null
                || request.prixReprise() != null || request.prixLocation() != null) {
            ProduitPrix prix = prixRepository.findPrixActif(savedVariant.getId())
                .orElseGet(() -> ProduitPrix.builder()
                    .variant(savedVariant)
                    .dateDebut(LocalDateTime.now())
                    .actif(true)
                    .build());
            // Mise à jour en place : on écrase uniquement les champs fournis
            if (request.prixNeuf()     != null) prix.setPrixNeuf(request.prixNeuf());
            if (request.prixOccasion() != null) prix.setPrixOccasion(request.prixOccasion());
            if (request.prixReprise()  != null) prix.setPrixReprise(request.prixReprise());
            if (request.prixLocation() != null) prix.setPrixLocation(request.prixLocation());
            prixRepository.save(prix);
        }

        return catalogMapper.toVariantResponse(savedVariant);
    }

    @Override
    @Transactional
    public ProduitVariantResponse toggleActifVariant(Long idVariant, boolean actif) {
        ProduitVariant variant = variantRepository.findById(idVariant)
            .orElseThrow(() -> new EntityNotFoundException("Variant introuvable : " + idVariant));
        variant.setActif(actif);
        return catalogMapper.toVariantResponse(variantRepository.save(variant));
    }

    @Override
    @Transactional
    public PrixResponse setPrice(SetPrixRequest request) {
        ProduitVariant variant = variantRepository.findById(request.idVariant())
            .orElseThrow(() -> new EntityNotFoundException("Variant introuvable : " + request.idVariant()));
        prixRepository.desactiverPrixActifs(variant.getId());
        ProduitPrix prix = ProduitPrix.builder()
            .variant(variant)
            .prixNeuf(request.prixNeuf())
            .prixOccasion(request.prixOccasion())
            .prixReprise(request.prixReprise())
            .prixLocation(request.prixLocation())
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
        String imageUrl = produit.getVariants().stream()
                .filter(ProduitVariant::isActif)
                .flatMap(v -> v.getImages().stream())
                .filter(ProduitImage::isPrincipale)
                .findFirst()
                .map(ProduitImage::getUrl).orElse(null);
        String imageAlt = produit.getVariants().stream()
                .filter(ProduitVariant::isActif)
                .flatMap(v -> v.getImages().stream())
                .filter(ProduitImage::isPrincipale)
                .findFirst()
                .map(ProduitImage::getAlt).orElse(null);

        String typeCategorie = produit.getCategorie() != null
                && produit.getCategorie().getTypeCategorie() != null
                ? produit.getCategorie().getTypeCategorie().getCode()
                : null;

        String plateformeCode = produit.getVariants().stream()
                .filter(ProduitVariant::isActif)
                .filter(v -> v.getPlateforme() != null)
                .map(v -> v.getPlateforme().getCode().toLowerCase())
                .findFirst()
                .orElse(null);

        return new ProduitSummary(
                base.id(), base.nom(), base.slug(), base.categorie(),
                typeCategorie, plateformeCode,
                imageUrl, imageAlt,
                catalogMapper.prixNeuf(produit.getVariants()),
                catalogMapper.prixOccasion(produit.getVariants()),
                catalogMapper.prixReprise(produit.getVariants()),
                catalogMapper.prixLocation(produit.getVariants()),
                produit.getVariants().stream().anyMatch(ProduitVariant::isActif),
                base.misEnAvant(), base.pegi(),
                avisStats.noteMoyenne(), avisStats.nbAvis(),
                catalogMapper.variantIdParStatut(produit.getVariants(), "NEUF"),
                catalogMapper.variantIdParStatut(produit.getVariants(), "OCCASION"),
                catalogMapper.variantIdParStatut(produit.getVariants(), "LOCATION"),
                produit.isEstPreCommande()
        );
    }

    private ProduitResponse enrichirProduitDetail(Produit produit) {
        ProduitResponse base = catalogMapper.toProduitResponse(produit);

        List<ProduitVariantResponse> variants = produit.getVariants().stream()
                .map(variant -> {
                    ProduitPrix px = variant.getPrix() == null ? null :
                            variant.getPrix().stream()
                            .filter(ProduitPrix::isActif)
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
                            catalogMapper.toEditionDto(variant.getEdition()),
                            variant.getCouleur(),
                            variant.getLangueVente(),
                            variant.isScelle(),
                            variant.isEstDemat(),
                            variant.isEstTcgUnitaire(),
                            variant.isEstReprise(),
                            variant.isNecessiteNumeroSerie(),
                            variant.getTauxTva() != null ? catalogMapper.toTauxTvaDto(variant.getTauxTva()) : null,
                            px != null ? px.getPrixNeuf()     : null,
                            px != null ? px.getPrixOccasion() : null,
                            px != null ? px.getPrixReprise()  : null,
                            px != null ? px.getPrixLocation() : null,
                            variant.isActif(),
                            catalogMapper.toImageDtoList(variant.getImages())
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
                produit.isEstPreCommande(),
                base.categorie(),
                variants,
                produit.getVariants().stream()
                        .flatMap(v -> v.getImages().stream())
                        .map(catalogMapper::toImageDto)
                        .toList(),
                catalogMapper.toScreenshotDtoList(
                        screenshotRepository.findByProduitIdOrderByOrdreAffichageAsc(produit.getId())),
                produitVideoRepository.findByProduitIdOrderByOrdreAffichageAsc(produit.getId())
                        .stream().map(v -> new ProduitVideoResponse(
                                v.getId(), v.getUrl(), v.getTitre(),
                                v.getOrdreAffichage(), v.getLangue(),
                                v.getSousTitresUrl(), v.getAudioDescUrl(),
                                v.getDateCreation()))
                        .toList(),
                noteMoyenne,
                nbAvis,
                avis
        );
    }

    @Override
    @Transactional
    public ProduitImageDto ajouterImage(Long idVariant, CreateProduitImageRequest request) {
        ProduitVariant variant = variantRepository.findById(idVariant)
                .orElseThrow(() -> new EntityNotFoundException("Variant introuvable : " + idVariant));
        if (request.principale()) {
            produitImageRepository.clearPrincipaleByVariantId(idVariant);
        }
        ProduitImage image = new ProduitImage();
        image.setVariant(variant);
        image.setUrl(request.url());
        image.setAlt(request.alt() != null ? request.alt() : "");
        image.setPrincipale(request.principale());
        return catalogMapper.toImageDto(produitImageRepository.save(image));
    }

    @Override
    @Transactional
    public ProduitImageDto uploadImage(Long idVariant, MultipartFile file, String alt, boolean principale) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Fichier image manquant.");
        }

        ProduitVariant variant = variantRepository.findById(idVariant)
                .orElseThrow(() -> new EntityNotFoundException("Variant introuvable : " + idVariant));

        // ── Sous-dossier : catalogue/{typeCategorie}/{plateforme}/ ────────────
        String typeCode = (variant.getProduit().getCategorie() != null
                && variant.getProduit().getCategorie().getTypeCategorie() != null)
                ? variant.getProduit().getCategorie().getTypeCategorie().getCode().toLowerCase()
                : "divers";

        String platCode = variant.getPlateforme() != null
                ? variant.getPlateforme().getCode().toLowerCase().replace('_', '-')
                : "generique";

        // ── Nom de fichier : slug-produit + extension ─────────────────────────
        String originalName = file.getOriginalFilename() != null ? file.getOriginalFilename() : "image";
        String ext = originalName.contains(".")
                ? originalName.substring(originalName.lastIndexOf('.')).toLowerCase()
                : ".jpg";
        String filename = variant.getProduit().getSlug() + "-" + platCode + ext;

        // ── Sauvegarde physique ───────────────────────────────────────────────
        Path targetDir  = Paths.get(imagesPath, "catalogue", typeCode, platCode).toAbsolutePath().normalize();
        Path targetFile = targetDir.resolve(filename);
        try {
            Files.createDirectories(targetDir);
            file.transferTo(targetFile);
            log.info("Image sauvegardée : {}", targetFile);
        } catch (IOException e) {
            throw new RuntimeException("Impossible de sauvegarder l'image : " + e.getMessage(), e);
        }

        // ── URL publique servie par /images/** ────────────────────────────────
        String url = "/images/catalogue/" + typeCode + "/" + platCode + "/" + filename;

        if (principale) {
            produitImageRepository.clearPrincipaleByVariantId(idVariant);
        }
        ProduitImage image = new ProduitImage();
        image.setVariant(variant);
        image.setUrl(url);
        image.setAlt(alt != null && !alt.isBlank() ? alt : variant.getProduit().getNom());
        image.setPrincipale(principale);
        return catalogMapper.toImageDto(produitImageRepository.save(image));
    }

    @Override
    @Transactional
    public ProduitImageDto modifierImage(Long idVariant, Long imageId, UpdateProduitImageRequest request) {
        ProduitImage image = produitImageRepository.findById(imageId)
                .orElseThrow(() -> new EntityNotFoundException("Image introuvable : " + imageId));
        if (!image.getVariant().getId().equals(idVariant)) {
            throw new IllegalArgumentException("Cette image n'appartient pas au variant " + idVariant);
        }
        if (request.url()       != null) image.setUrl(request.url());
        if (request.alt()       != null) image.setAlt(request.alt());
        if (request.principale() != null) {
            if (request.principale()) {
                // Retirer le flag principale des autres images du même variant
                produitImageRepository.clearPrincipaleByVariantId(idVariant);
            }
            image.setPrincipale(request.principale());
        }
        return catalogMapper.toImageDto(produitImageRepository.save(image));
    }

    @Override
    @Transactional
    public void supprimerImage(Long idVariant, Long imageId) {
        ProduitImage image = produitImageRepository.findById(imageId)
                .orElseThrow(() -> new EntityNotFoundException("Image introuvable : " + imageId));
        if (!image.getVariant().getId().equals(idVariant)) {
            throw new IllegalArgumentException("Cette image n'appartient pas au variant " + idVariant);
        }
        produitImageRepository.delete(image);
    }

    @Override
    public List<ProduitVideoResponse> getVideos(Long idProduit) {
        chargerProduit(idProduit);
        return produitVideoRepository.findByProduitIdOrderByOrdreAffichageAsc(idProduit).stream()
                .map(this::toVideoResponse)
                .toList();
    }

    @Override
    @Transactional
    public ProduitVideoResponse ajouterVideo(Long idProduit, CreateProduitVideoRequest request) {
        Produit produit = chargerProduit(idProduit);
        String langue = (request.langue() != null && !request.langue().isBlank()) ? request.langue() : "fr";
        ProduitVideo video = ProduitVideo.builder()
                .produit(produit)
                .url(request.url())
                .titre(request.titre())
                .ordreAffichage(request.ordreAffichage())
                .langue(langue)
                .sousTitresUrl(request.sousTitresUrl())
                .audioDescUrl(request.audioDescUrl())
                .transcription(request.transcription())
                .build();
        return toVideoResponse(produitVideoRepository.save(video));
    }

    @Override
    @Transactional
    public void supprimerVideo(Long idProduit, Long idVideo) {
        ProduitVideo video = produitVideoRepository.findById(idVideo)
                .orElseThrow(() -> new EntityNotFoundException("Vidéo introuvable : " + idVideo));
        if (!video.getProduit().getId().equals(idProduit)) {
            throw new IllegalArgumentException("Cette vidéo n'appartient pas au produit " + idProduit);
        }
        produitVideoRepository.delete(video);
    }

    private ProduitVideoResponse toVideoResponse(ProduitVideo v) {
        return new ProduitVideoResponse(
                v.getId(),
                v.getUrl(),
                v.getTitre(),
                v.getOrdreAffichage(),
                v.getLangue(),
                v.getSousTitresUrl(),
                v.getAudioDescUrl(),
                v.getDateCreation()
        );
    }

    @Override
    public Page<CataloguePosSummary> getCataloguePOS(Long idMagasin, String q, String plateforme, String etat, Pageable pageable) {
        String qParam    = (q          != null && !q.isBlank())          ? q.trim()          : null;
        String platParam = (plateforme != null && !plateforme.isBlank()) ? plateforme.trim() : null;
        String etatParam = (etat       != null && !etat.isBlank())       ? etat.trim()       : null;

        Page<StockMagasin> stocks = stockMagasinRepository.findCataloguePOS(idMagasin, qParam, platParam, etatParam, pageable);

        return stocks.map(s -> {
            ProduitVariant v      = s.getVariant();
            String         statut = v.getStatutProduit() != null ? v.getStatutProduit().getCode().toUpperCase() : "NEUF";

            // Récupérer les 4 prix depuis produit_prix
            BigDecimal prixNeuf     = null;
            BigDecimal prixOccasion = null;
            BigDecimal prixLocation = null;
            BigDecimal prixReprise  = null;
            if (v.getPrix() != null) {
                for (ProduitPrix px : v.getPrix()) {
                    if (px.isActif()) {
                        prixNeuf     = px.getPrixNeuf();
                        prixOccasion = px.getPrixOccasion();
                        prixLocation = px.getPrixLocation();
                        prixReprise  = px.getPrixReprise();
                        break;
                    }
                }
            }

            boolean disponible = s.getQuantiteNeuf() > 0 || s.getQuantiteOccasion() > 0;

            return new CataloguePosSummary(
                v.getProduit().getId(),
                v.getId(),
                v.getNomCommercial(),
                v.getSku(),
                statut,
                v.getPlateforme()    != null ? v.getPlateforme().getCode()    : null,
                v.getEdition()       != null ? v.getEdition().getLibelle()    : null,
                v.getFormatProduit() != null ? v.getFormatProduit().getCode() : null,
                prixNeuf,
                prixOccasion,
                prixLocation,
                prixReprise,
                s.getQuantiteNeuf(),
                s.getQuantiteOccasion(),
                disponible,
                v.getProduit().getCategorie() != null ? v.getProduit().getCategorie().getId() : null
            );
        });
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

    // ════════════════════════════════════════════════════════════════
    //  SCREENSHOTS — niveau produit (communs à tous les variants)
    // ════════════════════════════════════════════════════════════════

    @Override
    public List<ScreenshotDto> getScreenshots(Long idProduit) {
        chargerProduit(idProduit);
        return catalogMapper.toScreenshotDtoList(
                screenshotRepository.findByProduitIdOrderByOrdreAffichageAsc(idProduit));
    }

    @Override
    @Transactional
    public ScreenshotDto ajouterScreenshot(Long idProduit, CreateScreenshotRequest request) {
        Produit produit = chargerProduit(idProduit);
        ProduitScreenshot s = ProduitScreenshot.builder()
                .produit(produit)
                .url(request.url())
                .alt(request.alt() != null ? request.alt() : "")
                .ordreAffichage(request.ordreAffichage())
                .build();
        return catalogMapper.toScreenshotDto(screenshotRepository.save(s));
    }

    @Override
    @Transactional
    public ScreenshotDto uploadScreenshot(Long idProduit, MultipartFile file, String alt, int ordre) {
        if (file == null || file.isEmpty()) throw new IllegalArgumentException("Fichier manquant.");
        Produit produit = chargerProduit(idProduit);

        String originalName = file.getOriginalFilename() != null ? file.getOriginalFilename() : "screenshot";
        String ext = originalName.contains(".")
                ? originalName.substring(originalName.lastIndexOf('.')).toLowerCase() : ".jpg";
        String filename = produit.getSlug() + "-screen-" + System.currentTimeMillis() + ext;

        Path targetDir  = Paths.get(imagesPath, "screenshots", produit.getSlug()).toAbsolutePath().normalize();
        Path targetFile = targetDir.resolve(filename);
        try {
            Files.createDirectories(targetDir);
            file.transferTo(targetFile);
        } catch (IOException e) {
            throw new RuntimeException("Impossible de sauvegarder le screenshot : " + e.getMessage(), e);
        }

        String url = "/images/screenshots/" + produit.getSlug() + "/" + filename;
        ProduitScreenshot s = ProduitScreenshot.builder()
                .produit(produit)
                .url(url)
                .alt(alt != null && !alt.isBlank() ? alt : produit.getNom())
                .ordreAffichage(ordre)
                .build();
        return catalogMapper.toScreenshotDto(screenshotRepository.save(s));
    }

    @Override
    @Transactional
    public void supprimerScreenshot(Long idProduit, Long idScreenshot) {
        fr.micromania.entity.catalog.ProduitScreenshot s = screenshotRepository.findById(idScreenshot)
                .orElseThrow(() -> new EntityNotFoundException("Screenshot introuvable : " + idScreenshot));
        if (!s.getProduit().getId().equals(idProduit)) {
            throw new IllegalArgumentException("Ce screenshot n'appartient pas au produit " + idProduit);
        }
        screenshotRepository.delete(s);
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
