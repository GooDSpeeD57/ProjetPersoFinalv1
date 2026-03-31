package fr.micromania.util;

import fr.micromania.entity.*;
import fr.micromania.entity.catalog.*;
import fr.micromania.entity.commande.*;
import fr.micromania.entity.referentiel.*;

import java.math.BigDecimal;
import java.time.*;

public final class TestFixtures {

    private TestFixtures() {}

    public static TypeFidelite typeFideliteNormal() {
        TypeFidelite tf = new TypeFidelite();
        tf.setId(1L); tf.setCode("NORMAL");
        tf.setPointsParEuro(new BigDecimal("1.00"));
        return tf;
    }

    public static Avatar avatarDefault() {
        Avatar a = new Avatar();
        a.setId(1L); a.setNom("Default");
        a.setUrl("/images/avatars/default.png"); a.setAlt("Avatar");
        return a;
    }

    public static Client clientActif() {
        Client c = new Client();
        c.setId(1L); c.setPseudo("testuser");
        c.setNom("Dupont"); c.setPrenom("Alice");
        c.setEmail("alice@test.fr"); c.setTelephone("0601020304");
        c.setMotDePasse("$2a$12$hashedpassword");
        c.setDateNaissance(LocalDate.of(1995, 6, 15));
        c.setTypeFidelite(typeFideliteNormal());
        c.setAvatar(avatarDefault());
        c.setEmailVerifie(true); c.setCompteActive(true);
        c.setActif(true); c.setDeleted(false);
        c.setRgpdConsent(true); c.setDoitDefinirMotDePasse(false);
        c.setDemandeSuppression(false);
        c.setDateCreation(LocalDateTime.now().minusDays(10));
        return c;
    }

    public static TypeCategorie typeCategorie() {
        TypeCategorie tc = new TypeCategorie();
        tc.setId(1L); tc.setCode("JEU"); tc.setDescription("Jeux vidéo");
        return tc;
    }

    public static Categorie categorieJeux() {
        Categorie c = new Categorie();
        c.setId(1L); c.setNom("Jeux Video");
        c.setTypeCategorie(typeCategorie()); c.setActif(true);
        return c;
    }

    public static Produit produit() {
        Produit p = new Produit();
        p.setId(1L); p.setNom("Spider-Man PS5");
        p.setSlug("spider-man-ps5");
        p.setCategorie(categorieJeux());
        p.setEditeur("Sony"); p.setPegi(16);
        p.setNiveauAccesMin("NORMAL");
        p.setActif(true); p.setDeleted(false);
        return p;
    }

    public static ProduitVariant variant() {
        Plateforme ps5 = new Plateforme();
        ps5.setId(1L); ps5.setCode("PS5"); ps5.setLibelle("PlayStation 5");
        FormatProduit fp = new FormatProduit();
        fp.setId(1L); fp.setCode("PHYSIQUE");
        StatutProduit sp = new StatutProduit();
        sp.setId(1L); sp.setCode("NEUF");
        ProduitVariant v = new ProduitVariant();
        v.setId(1L); v.setSku("SPIDER-MAN-PS5-NEUF");
        v.setNomCommercial("Spider-Man PS5 Neuf");
        v.setProduit(produit()); v.setPlateforme(ps5);
        v.setFormatProduit(fp); v.setStatutProduit(sp);
        v.setActif(true); v.setEstDemat(false);
        return v;
    }

    public static ProduitPrix prixWeb(ProduitVariant v) {
        CanalVente cv = new CanalVente();
        cv.setId(1L); cv.setCode("WEB");
        ProduitPrix p = new ProduitPrix();
        p.setId(1L); p.setVariant(v); p.setCanalVente(cv);
        p.setPrix(new BigDecimal("69.99"));
        p.setDateDebut(LocalDateTime.now().minusDays(1));
        p.setActif(true);
        return p;
    }

    public static Magasin magasin() {
        Magasin m = new Magasin();
        m.setId(1L); m.setNom("Micromania Paris");
        m.setEmail("paris@micromania.fr"); m.setActif(true);
        return m;
    }

    public static StatutCommande statutCommande(String code) {
        StatutCommande s = new StatutCommande();
        s.setId(1L); s.setCode(code);
        return s;
    }

    public static Commande commande(Client client) {
        ModeLivraison ml = new ModeLivraison();
        ml.setId(1L); ml.setCode("DOMICILE");
        CanalVente cv = new CanalVente();
        cv.setId(1L); cv.setCode("WEB");
        Commande c = new Commande();
        c.setId(1L); c.setReferenceCommande("CMD-TEST-001");
        c.setClient(client); c.setStatutCommande(statutCommande("CREEE"));
        c.setModeLivraison(ml); c.setCanalVente(cv);
        c.setSousTotal(new BigDecimal("69.99"));
        c.setMontantRemise(BigDecimal.ZERO);
        c.setFraisLivraison(new BigDecimal("4.99"));
        c.setMontantTotal(new BigDecimal("74.98"));
        c.setDateCommande(LocalDateTime.now());
        return c;
    }

    public static PointsFidelite points(Client client) {
        PointsFidelite p = new PointsFidelite();
        p.setId(1L); p.setClient(client);
        p.setSoldePoints(150);
        p.setTotalAchatsAnnuel(new BigDecimal("200.00"));
        p.setDateDebutPeriode(LocalDate.of(LocalDate.now().getYear(), 1, 1));
        return p;
    }
}
