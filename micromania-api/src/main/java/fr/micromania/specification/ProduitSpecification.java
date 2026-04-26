package fr.micromania.specification;

import fr.micromania.entity.catalog.Produit;
import fr.micromania.entity.catalog.ProduitVariant;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public class ProduitSpecification {

    private ProduitSpecification() {}

    public static Specification<Produit> actif() {
        return (root, query, cb) -> cb.and(
                cb.isFalse(root.get("deleted")),
                cb.isTrue(root.get("actif"))
        );
    }

    /** Spec "no restriction" utilisée quand un filtre est absent. */
    private static final Specification<Produit> AUCUN_FILTRE = (root, query, cb) -> null;

    public static Specification<Produit> avecRecherche(String q) {
        if (q == null || q.isBlank()) return AUCUN_FILTRE;
        String pattern = "%" + q.toLowerCase() + "%";
        return (root, query, cb) -> cb.or(
                cb.like(cb.lower(root.get("nom")), pattern),
                cb.like(cb.lower(root.get("editeur")), pattern)
        );
    }

    public static Specification<Produit> avecCategorie(Long idCategorie) {
        if (idCategorie == null) return AUCUN_FILTRE;
        return (root, query, cb) -> cb.equal(root.get("categorie").get("id"), idCategorie);
    }

    public static Specification<Produit> avecNiveau(String niveauAccesMin) {
        if (niveauAccesMin == null || niveauAccesMin.isBlank()) return AUCUN_FILTRE;
        return (root, query, cb) -> cb.equal(root.get("niveauAccesMin"), niveauAccesMin);
    }

    /**
     * Filtre par plateforme : une liste de codes exacts (ex: ["PS4"], ["XBOX","XBOX_ONE","XBOX_SERIES"]).
     * Utilise un EXISTS sur les variants actifs.
     */
    public static Specification<Produit> avecPlateforme(List<String> codes) {
        if (codes == null || codes.isEmpty()) return AUCUN_FILTRE;
        return (root, query, cb) -> {
            Subquery<Long> sub = query.subquery(Long.class);
            Root<ProduitVariant> v = sub.from(ProduitVariant.class);
            sub.select(v.get("id"));
            sub.where(
                    cb.equal(v.get("produit"), root),
                    cb.isTrue(v.get("actif")),
                    v.get("plateforme").get("code").in(codes)
            );
            return cb.exists(sub);
        };
    }

    /**
     * Filtre par famille : code du typeCategorie en base (ex: "jeu", "console", "accessoire").
     */
    public static Specification<Produit> avecFamille(String familleCode) {
        if (familleCode == null || familleCode.isBlank()) return AUCUN_FILTRE;
        return (root, query, cb) ->
                cb.equal(
                        cb.lower(root.get("categorie").get("typeCategorie").get("code")),
                        familleCode.toLowerCase()
                );
    }

    /**
     * Filtre par état : code du statutProduit en base (ex: "NEUF", "OCCASION", "LOCATION").
     * Utilise un EXISTS sur les variants actifs.
     */
    public static Specification<Produit> avecEtat(String etatCode) {
        if (etatCode == null || etatCode.isBlank()) return AUCUN_FILTRE;
        return (root, query, cb) -> {
            Subquery<Long> sub = query.subquery(Long.class);
            Root<ProduitVariant> v = sub.from(ProduitVariant.class);
            sub.select(v.get("id"));
            sub.where(
                    cb.equal(v.get("produit"), root),
                    cb.isTrue(v.get("actif")),
                    cb.equal(
                            cb.upper(v.get("statutProduit").get("code")),
                            etatCode.toUpperCase()
                    )
            );
            return cb.exists(sub);
        };
    }
}
