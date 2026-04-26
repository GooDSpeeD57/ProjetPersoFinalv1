package fr.micromania.specification;

import fr.micromania.entity.catalog.ProduitVariant;
import org.springframework.data.jpa.domain.Specification;

public class VariantCatalogueSpecification {

    private VariantCatalogueSpecification() {}

    private static final Specification<ProduitVariant> AUCUN_FILTRE = (r, q, cb) -> null;

    /** Variant actif + produit actif non supprimé. */
    public static Specification<ProduitVariant> actif() {
        return (root, query, cb) -> {
            query.distinct(false);
            return cb.and(
                    cb.isTrue(root.get("actif")),
                    cb.isFalse(root.get("produit").get("deleted")),
                    cb.isTrue(root.get("produit").get("actif"))
            );
        };
    }

    public static Specification<ProduitVariant> avecRecherche(String q) {
        if (q == null || q.isBlank()) return AUCUN_FILTRE;
        String pattern = "%" + q.toLowerCase() + "%";
        return (root, query, cb) -> cb.or(
                cb.like(cb.lower(root.get("produit").get("nom")),           pattern),
                cb.like(cb.lower(root.get("produit").get("editeur")),       pattern),
                cb.like(cb.lower(root.get("nomCommercial")),                pattern)
        );
    }

    public static Specification<ProduitVariant> avecCategorie(Long idCategorie) {
        if (idCategorie == null) return AUCUN_FILTRE;
        return (root, query, cb) ->
                cb.equal(root.get("produit").get("categorie").get("id"), idCategorie);
    }

    public static Specification<ProduitVariant> avecFamille(String familleCode) {
        if (familleCode == null || familleCode.isBlank()) return AUCUN_FILTRE;
        return (root, query, cb) ->
                cb.equal(
                        cb.lower(root.get("produit").get("categorie").get("typeCategorie").get("code")),
                        familleCode.toLowerCase()
                );
    }

    public static Specification<ProduitVariant> avecPlateforme(String code) {
        if (code == null || code.isBlank()) return AUCUN_FILTRE;
        return (root, query, cb) ->
                cb.equal(cb.lower(root.get("plateforme").get("code")), code.toLowerCase());
    }

    public static Specification<ProduitVariant> avecStatut(String code) {
        if (code == null || code.isBlank()) return AUCUN_FILTRE;
        return (root, query, cb) ->
                cb.equal(cb.upper(root.get("statutProduit").get("code")), code.toUpperCase());
    }

    public static Specification<ProduitVariant> avecEdition(String code) {
        if (code == null || code.isBlank()) return AUCUN_FILTRE;
        return (root, query, cb) ->
                cb.equal(cb.upper(root.get("edition").get("code")), code.toUpperCase());
    }

    public static Specification<ProduitVariant> misEnAvantSeulement() {
        return (root, query, cb) -> cb.isTrue(root.get("produit").get("misEnAvant"));
    }
}
