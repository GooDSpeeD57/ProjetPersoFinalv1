package fr.micromania.service.impl;

import fr.micromania.dto.catalog.*;
import fr.micromania.entity.catalog.*;
import fr.micromania.mapper.CatalogMapper;
import fr.micromania.repository.*;
import fr.micromania.service.CatalogService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class CatalogServiceImpl implements CatalogService {

    private final ProduitRepository produitRepository;
    private final ProduitVariantRepository variantRepository;
    private final ProduitPrixRepository prixRepository;
    private final CategorieRepository categorieRepository;
    private final CatalogMapper catalogMapper;

    @Override
    public ProduitResponse getProduitById(Long id) {
        Produit produit = produitRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new EntityNotFoundException("Produit introuvable : " + id));
        return enrichirProduitDetail(produit);
    }

    @Override
    public ProduitResponse getProduitBySlug(String slug) {
        Produit produit = produitRepository.findBySlugAndDeletedFalse(slug)
                .orElseThrow(() -> new EntityNotFoundException("Produit introuvable : " + slug));
        return enrichirProduitDetail(produit);
    }

    @Override
    public Page<ProduitSummary> search(String query, Long idCategorie, String niveauAccesMin, Pageable pageable) {
        return produitRepository.search(query, idCategorie, niveauAccesMin, pageable)
            .map(produit -> {
                ProduitSummary base = catalogMapper.toProduitSummary(produit);
                // Résolution image principale
                String imageUrl = produit.getImages().stream()
                    .filter(ProduitImage::isPrincipale).findFirst()
                    .map(ProduitImage::getUrl).orElse(null);
                String imageAlt = produit.getImages().stream()
                    .filter(ProduitImage::isPrincipale).findFirst()
                    .map(ProduitImage::getAlt).orElse(null);
                // Prix minimal tous canaux
                var prixMin = catalogMapper.prixMinimal(produit.getVariants());
                return new ProduitSummary(
                    base.id(), base.nom(), base.slug(), base.categorie(),
                    imageUrl, imageAlt, prixMin,
                    produit.getVariants().stream().anyMatch(v -> v.isActif()),
                    base.misEnAvant(), base.pegi()
                );
            });
    }

    @Override
    public List<ProduitSummary> getMisEnAvant() {
        return produitRepository.findMisEnAvant().stream()
            .map(catalogMapper::toProduitSummary)
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
        log.info("Produit créé : slug={}", produit.getSlug());
        return catalogMapper.toProduitResponse(produit);
    }

    @Override
    @Transactional
    public ProduitResponse modifierProduit(Long id, CreateProduitRequest request) {
        Produit produit = produitRepository.findByIdAndDeletedFalse(id)
            .orElseThrow(() -> new EntityNotFoundException("Produit introuvable : " + id));
        if (request.nom()         != null) produit.setNom(request.nom());
        if (request.description() != null) produit.setDescription(request.description());
        if (request.editeur()     != null) produit.setEditeur(request.editeur());
        if (request.pegi()        != null) produit.setPegi(request.pegi());
        produit.setMisEnAvant(request.misEnAvant());
        return catalogMapper.toProduitResponse(produitRepository.save(produit));
    }

    @Override
    @Transactional
    public void supprimerProduit(Long id) {
        produitRepository.findByIdAndDeletedFalse(id)
            .orElseThrow(() -> new EntityNotFoundException("Produit introuvable : " + id));
        produitRepository.softDelete(id);
    }

    @Override
    @Transactional
    public ProduitVariantResponse creerVariant(CreateVariantRequest request) {
        Produit produit = produitRepository.findByIdAndDeletedFalse(request.idProduit())
            .orElseThrow(() -> new EntityNotFoundException("Produit introuvable : " + request.idProduit()));
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

        // Prix Web si fourni
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
        return catalogMapper.toPrixResponse(prixRepository.save(prix));
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
                base.images()
        );
    }
}

