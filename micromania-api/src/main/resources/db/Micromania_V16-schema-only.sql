-- ============================================================
-- Micromania_V16-complet.sql
-- ============================================================

CREATE DATABASE IF NOT EXISTS Micromania;
USE Micromania;
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ============================================================
-- DROP TABLES
-- ============================================================

DROP TABLE IF EXISTS token_blacklist;
DROP TABLE IF EXISTS reset_password_token;
DROP TABLE IF EXISTS paiement_transaction;
DROP TABLE IF EXISTS precommande_ligne;
DROP TABLE IF EXISTS precommande;
DROP TABLE IF EXISTS client_auth_provider;
DROP TABLE IF EXISTS favori_magasin;
DROP TABLE IF EXISTS favori_produit;
DROP TABLE IF EXISTS extension_garantie;
DROP TABLE IF EXISTS garantie;
DROP TABLE IF EXISTS dossier_sav;
DROP TABLE IF EXISTS retour_ligne;
DROP TABLE IF EXISTS retour_produit;
DROP TABLE IF EXISTS vente_unite;
DROP TABLE IF EXISTS bibliotheque_client;
DROP TABLE IF EXISTS cle_produit;
DROP TABLE IF EXISTS tcg_carte_inventaire;
DROP TABLE IF EXISTS tcg_carte_reference;
DROP TABLE IF EXISTS tcg_extension;
DROP TABLE IF EXISTS tcg_jeu;
DROP TABLE IF EXISTS reprise_ligne;
DROP TABLE IF EXISTS reprise;
DROP TABLE IF EXISTS ligne_facture;
DROP TABLE IF EXISTS facture;
DROP TABLE IF EXISTS ligne_commande;
DROP TABLE IF EXISTS commande;
DROP TABLE IF EXISTS reservation_stock;
DROP TABLE IF EXISTS mouvement_stock;
DROP TABLE IF EXISTS stock_entrepot;
DROP TABLE IF EXISTS mouvement_stock_entrepot;
DROP TABLE IF EXISTS stock_magasin;
DROP TABLE IF EXISTS promotion_categorie;
DROP TABLE IF EXISTS promotion_variant;
DROP TABLE IF EXISTS promotion_usage;
DROP TABLE IF EXISTS promotion;
DROP TABLE IF EXISTS avis_produit;
DROP TABLE IF EXISTS statut_avis;
DROP TABLE IF EXISTS ligne_panier;
DROP TABLE IF EXISTS panier;
DROP TABLE IF EXISTS produit_screenshot;
DROP TABLE IF EXISTS produit_video;
DROP TABLE IF EXISTS produit_image;
DROP TABLE IF EXISTS produit_prix;
DROP TABLE IF EXISTS produit_variant;
DROP TABLE IF EXISTS edition_produit;
DROP TABLE IF EXISTS produit;
DROP TABLE IF EXISTS categorie;
DROP TABLE IF EXISTS adresse;
DROP TABLE IF EXISTS abonnement_client;
DROP TABLE IF EXISTS bon_achat;
DROP TABLE IF EXISTS historique_points;
DROP TABLE IF EXISTS points_fidelite;
DROP TABLE IF EXISTS push_notification_token;
DROP TABLE IF EXISTS remember_me_token;
DROP TABLE IF EXISTS audit_log;
DROP TABLE IF EXISTS planning_employe;
DROP TABLE IF EXISTS employe;
DROP TABLE IF EXISTS client;
DROP TABLE IF EXISTS avatar;
DROP TABLE IF EXISTS entrepot;
DROP TABLE IF EXISTS magasin;
DROP TABLE IF EXISTS role_permission;
DROP TABLE IF EXISTS permission;
DROP TABLE IF EXISTS role;
DROP TABLE IF EXISTS type_garantie;
DROP TABLE IF EXISTS type_fidelite;
DROP TABLE IF EXISTS ratio_points;
DROP TABLE IF EXISTS type_categorie;
DROP TABLE IF EXISTS type_adresse;
DROP TABLE IF EXISTS statut_produit;
DROP TABLE IF EXISTS statut_panier;
DROP TABLE IF EXISTS statut_commande;
DROP TABLE IF EXISTS statut_facture;
DROP TABLE IF EXISTS statut_planning;
DROP TABLE IF EXISTS statut_abonnement;
DROP TABLE IF EXISTS statut_retour;
DROP TABLE IF EXISTS statut_sav;
DROP TABLE IF EXISTS statut_reprise;
DROP TABLE IF EXISTS etat_carte_tcg;
DROP TABLE IF EXISTS type_reduction;
DROP TABLE IF EXISTS type_mouvement;
DROP TABLE IF EXISTS type_retour;
DROP TABLE IF EXISTS mode_compensation_reprise;
DROP TABLE IF EXISTS provider_auth;
DROP TABLE IF EXISTS statut_paiement;
DROP TABLE IF EXISTS statut_precommande;
DROP TABLE IF EXISTS mode_paiement;
DROP TABLE IF EXISTS mode_livraison;
DROP TABLE IF EXISTS canal_vente;
DROP TABLE IF EXISTS format_produit;
DROP TABLE IF EXISTS plateforme;
DROP TABLE IF EXISTS contexte_vente;
DROP TABLE IF EXISTS tentative_connexion_echec;
DROP TABLE IF EXISTS session_active;
DROP TABLE IF EXISTS connexion_log;
DROP TABLE IF EXISTS taux_tva;

-- ============================================================
-- TABLES DE REFERENCE
-- ============================================================
CREATE TABLE token_blacklist (
    jti       VARCHAR(36)  NOT NULL,
    expire_le DATETIME     NOT NULL,
    PRIMARY KEY (jti)
);

CREATE TABLE type_fidelite (
    id_type_fidelite   BIGINT AUTO_INCREMENT PRIMARY KEY,
    code               VARCHAR(50) NOT NULL UNIQUE,
    description        VARCHAR(255),
    points_par_euro    DECIMAL(5,2) NOT NULL DEFAULT 1.00,
    seuil_upgrade_euro DECIMAL(10,2) NULL,
    prix_abonnement    DECIMAL(10,2) NULL
);

CREATE TABLE taux_tva (
    id_taux_tva  BIGINT AUTO_INCREMENT PRIMARY KEY,
    code         VARCHAR(50)    NOT NULL UNIQUE,
    description  VARCHAR(255)   NULL,
    taux         DECIMAL(5,2)   NOT NULL,
    actif        BOOLEAN        NOT NULL DEFAULT TRUE,
    CONSTRAINT chk_taux_tva_positif CHECK (taux >= 0)
);

CREATE TABLE type_categorie (
    id_type_categorie     BIGINT AUTO_INCREMENT PRIMARY KEY,
    code                  VARCHAR(50) NOT NULL UNIQUE,
    description           VARCHAR(255),
    id_taux_tva_defaut    BIGINT NULL,
    CONSTRAINT fk_type_categorie_tva FOREIGN KEY (id_taux_tva_defaut) REFERENCES taux_tva(id_taux_tva)
);

CREATE TABLE type_adresse (
    id_type_adresse BIGINT AUTO_INCREMENT PRIMARY KEY,
    code            VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE role (
    id_role BIGINT AUTO_INCREMENT PRIMARY KEY,
    code    VARCHAR(50) NOT NULL UNIQUE,
    libelle VARCHAR(100) NOT NULL
);

CREATE TABLE permission (
    id_permission BIGINT AUTO_INCREMENT PRIMARY KEY,
    code          VARCHAR(100) NOT NULL UNIQUE,
    description   VARCHAR(255)
);

CREATE TABLE role_permission (
    id_role       BIGINT NOT NULL,
    id_permission BIGINT NOT NULL,
    PRIMARY KEY (id_role, id_permission),
    CONSTRAINT fk_role_permission_role       FOREIGN KEY (id_role)       REFERENCES role(id_role)             ON DELETE CASCADE,
    CONSTRAINT fk_role_permission_permission FOREIGN KEY (id_permission) REFERENCES permission(id_permission) ON DELETE CASCADE
);

CREATE TABLE statut_produit (
    id_statut_produit BIGINT AUTO_INCREMENT PRIMARY KEY,
    code              VARCHAR(50) NOT NULL UNIQUE,
    description       VARCHAR(255)
);

CREATE TABLE statut_panier (
    id_statut_panier BIGINT AUTO_INCREMENT PRIMARY KEY,
    code             VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE statut_commande (
    id_statut_commande BIGINT AUTO_INCREMENT PRIMARY KEY,
    code               VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE statut_facture (
    id_statut_facture BIGINT AUTO_INCREMENT PRIMARY KEY,
    code              VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE statut_planning (
    id_statut_planning BIGINT AUTO_INCREMENT PRIMARY KEY,
    code               VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE statut_abonnement (
    id_statut_abonnement BIGINT AUTO_INCREMENT PRIMARY KEY,
    code                 VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE statut_retour (
    id_statut_retour BIGINT AUTO_INCREMENT PRIMARY KEY,
    code             VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE statut_sav (
    id_statut_sav BIGINT AUTO_INCREMENT PRIMARY KEY,
    code          VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE statut_reprise (
    id_statut_reprise BIGINT AUTO_INCREMENT PRIMARY KEY,
    code              VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE statut_avis (
    id_statut_avis BIGINT AUTO_INCREMENT PRIMARY KEY,
    code           VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE type_reduction (
    id_type_reduction BIGINT AUTO_INCREMENT PRIMARY KEY,
    code              VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE type_mouvement (
    id_type_mouvement BIGINT AUTO_INCREMENT PRIMARY KEY,
    code              VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE type_retour (
    id_type_retour BIGINT AUTO_INCREMENT PRIMARY KEY,
    code           VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE mode_compensation_reprise (
    id_mode_compensation_reprise BIGINT AUTO_INCREMENT PRIMARY KEY,
    code                         VARCHAR(50) NOT NULL UNIQUE,
    description                  VARCHAR(255)
);

CREATE TABLE provider_auth (
    id_provider_auth BIGINT AUTO_INCREMENT PRIMARY KEY,
    code             VARCHAR(50) NOT NULL UNIQUE,
    libelle          VARCHAR(100) NOT NULL
);

CREATE TABLE statut_precommande (
    id_statut_precommande BIGINT AUTO_INCREMENT PRIMARY KEY,
    code                  VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE statut_paiement (
    id_statut_paiement BIGINT AUTO_INCREMENT PRIMARY KEY,
    code               VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE mode_paiement (
    id_mode_paiement BIGINT AUTO_INCREMENT PRIMARY KEY,
    code             VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE mode_livraison (
    id_mode_livraison BIGINT AUTO_INCREMENT PRIMARY KEY,
    code              VARCHAR(50) NOT NULL UNIQUE,
    description       VARCHAR(255)
);

CREATE TABLE canal_vente (
    id_canal_vente BIGINT AUTO_INCREMENT PRIMARY KEY,
    code           VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE format_produit (
    id_format_produit BIGINT AUTO_INCREMENT PRIMARY KEY,
    code              VARCHAR(50) NOT NULL UNIQUE,
    description       VARCHAR(255)
);

CREATE TABLE plateforme (
    id_plateforme BIGINT AUTO_INCREMENT PRIMARY KEY,
    code          VARCHAR(50) NOT NULL UNIQUE,
    libelle       VARCHAR(100) NOT NULL
);

CREATE TABLE etat_carte_tcg (
    id_etat_carte_tcg BIGINT AUTO_INCREMENT PRIMARY KEY,
    code              VARCHAR(50) NOT NULL UNIQUE,
    libelle           VARCHAR(100) NOT NULL,
    coefficient_prix  DECIMAL(5,2) NOT NULL DEFAULT 1.00,
    CONSTRAINT chk_etat_carte_coef CHECK (coefficient_prix > 0)
);

CREATE TABLE contexte_vente (
    id_contexte_vente BIGINT AUTO_INCREMENT PRIMARY KEY,
    code              VARCHAR(50) NOT NULL UNIQUE,
    description       VARCHAR(255)
);

CREATE TABLE ratio_points (
    id_ratio          BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_type_categorie BIGINT NOT NULL,
    id_type_fidelite  BIGINT NOT NULL,
    ratio             DECIMAL(5,2) NOT NULL,
    CONSTRAINT fk_ratio_type_categorie FOREIGN KEY (id_type_categorie) REFERENCES type_categorie(id_type_categorie),
    CONSTRAINT fk_ratio_type_fidelite  FOREIGN KEY (id_type_fidelite)  REFERENCES type_fidelite(id_type_fidelite),
    CONSTRAINT uq_ratio UNIQUE (id_type_categorie, id_type_fidelite),
    CONSTRAINT chk_ratio_positive CHECK (ratio > 0)
);

-- ============================================================
-- MAGASIN / UTILISATEURS
-- ============================================================

CREATE TABLE magasin (
    id_magasin        BIGINT AUTO_INCREMENT PRIMARY KEY,
    nom               VARCHAR(100) NOT NULL,
    telephone         VARCHAR(20),
    email             VARCHAR(150),
    actif             BOOLEAN NOT NULL DEFAULT TRUE,
    date_creation     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    date_modification DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- ============================================================
-- ENTREPOT
-- ============================================================

CREATE TABLE entrepot (
    id_entrepot       BIGINT AUTO_INCREMENT PRIMARY KEY,
    nom               VARCHAR(100) NOT NULL,
    code              VARCHAR(50) NOT NULL UNIQUE,
    telephone         VARCHAR(20) NULL,
    email             VARCHAR(150) NULL,
    responsable       VARCHAR(150) NULL,
    actif             BOOLEAN NOT NULL DEFAULT TRUE,
    date_creation     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    date_modification DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE employe (
    id_employe        BIGINT AUTO_INCREMENT PRIMARY KEY,
    nom               VARCHAR(100) NOT NULL,
    prenom            VARCHAR(100) NOT NULL,
    email             VARCHAR(150) NOT NULL UNIQUE,
    telephone         VARCHAR(20) NULL,
    mot_de_passe      VARCHAR(255) NOT NULL,
    id_role           BIGINT NOT NULL,
    id_magasin        BIGINT NOT NULL,
    date_embauche     DATE NULL,
    actif             BOOLEAN NOT NULL DEFAULT TRUE,
    deleted           BOOLEAN NOT NULL DEFAULT FALSE,
    date_creation     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    date_modification DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_employe_role    FOREIGN KEY (id_role)    REFERENCES role(id_role),
    CONSTRAINT fk_employe_magasin FOREIGN KEY (id_magasin) REFERENCES magasin(id_magasin)
);

CREATE TABLE avatar (
    id_avatar   BIGINT AUTO_INCREMENT PRIMARY KEY,
    nom         VARCHAR(100) NOT NULL,
    url         VARCHAR(255) NOT NULL,
    alt         VARCHAR(255) NOT NULL DEFAULT 'Avatar utilisateur',
    decorative  BOOLEAN NOT NULL DEFAULT FALSE,
    actif       BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE client (
    id_client                    BIGINT AUTO_INCREMENT PRIMARY KEY,
    pseudo                       VARCHAR(50) NOT NULL UNIQUE,
    nom                          VARCHAR(100) NOT NULL,
    prenom                       VARCHAR(100) NOT NULL,
    date_naissance               DATE NOT NULL,
    email                        VARCHAR(150) NOT NULL UNIQUE,
    telephone                    VARCHAR(20) NOT NULL UNIQUE,
    mot_de_passe                 VARCHAR(255) NOT NULL,
    id_avatar                    BIGINT NOT NULL DEFAULT 1,
    numero_carte_fidelite        VARCHAR(50) UNIQUE,
    id_type_fidelite             BIGINT NOT NULL,
    actif                        BOOLEAN NOT NULL DEFAULT TRUE,
    deleted                      BOOLEAN NOT NULL DEFAULT FALSE,
    rgpd_consent                 BOOLEAN NOT NULL DEFAULT FALSE,
    rgpd_consent_date            DATETIME NULL,
    rgpd_consent_ip              VARCHAR(45) NULL,
    email_verifie                BOOLEAN NOT NULL DEFAULT FALSE,
    date_verification_email      DATETIME NULL,
    token_verification_email     VARCHAR(255) NULL,
    token_verification_expire_le DATETIME NULL,
    telephone_verifie                    BOOLEAN NOT NULL DEFAULT FALSE,
	date_verification_telephone          DATETIME NULL,
	token_verification_telephone         VARCHAR(255) NULL,
	token_verification_telephone_expire_le DATETIME NULL,
    compte_active                BOOLEAN NOT NULL DEFAULT FALSE,
    cree_par_employe             BOOLEAN NOT NULL DEFAULT FALSE,
    id_employe_createur          BIGINT NULL,
    doit_definir_mot_de_passe    BOOLEAN NOT NULL DEFAULT TRUE,
    demande_suppression          BOOLEAN NOT NULL DEFAULT FALSE,
    date_suppression             DATETIME NULL,
    date_creation                DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    date_modification            DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    date_derniere_connexion		 DATETIME NULL,
    id_magasin_favori            BIGINT NULL,
    CONSTRAINT fk_client_avatar            FOREIGN KEY (id_avatar)            REFERENCES avatar(id_avatar),
    CONSTRAINT fk_client_fidelite          FOREIGN KEY (id_type_fidelite)    REFERENCES type_fidelite(id_type_fidelite),
    CONSTRAINT fk_client_employe_createur  FOREIGN KEY (id_employe_createur) REFERENCES employe(id_employe),
    CONSTRAINT fk_client_magasin_favori    FOREIGN KEY (id_magasin_favori)   REFERENCES magasin(id_magasin)
);

CREATE TABLE planning_employe (
    id_planning        BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_employe         BIGINT NOT NULL,
    id_statut_planning BIGINT NOT NULL,
    date_travail       DATE NOT NULL,
    heure_debut        TIME NOT NULL,
    heure_fin          TIME NOT NULL,
    note_interne       TEXT NULL,
    CONSTRAINT fk_planning_employe FOREIGN KEY (id_employe)         REFERENCES employe(id_employe) ON DELETE CASCADE,
    CONSTRAINT fk_planning_statut  FOREIGN KEY (id_statut_planning) REFERENCES statut_planning(id_statut_planning),
    CONSTRAINT chk_planning_heure CHECK (heure_fin > heure_debut)
);

CREATE TABLE remember_me_token (
    serie         VARCHAR(64) PRIMARY KEY,
    token_value   VARCHAR(64) NOT NULL,
    date_derniere DATETIME NOT NULL,
    username      VARCHAR(150) NOT NULL,
    user_type     VARCHAR(10) NOT NULL DEFAULT 'CLIENT',
    CONSTRAINT chk_remember_me_user_type CHECK (user_type IN ('CLIENT', 'EMPLOYE'))
);

CREATE TABLE push_notification_token (
    id_push_token     BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_client         BIGINT NOT NULL,
    token             VARCHAR(512) NOT NULL UNIQUE,
    platform          VARCHAR(10) NOT NULL,
    actif             BOOLEAN NOT NULL DEFAULT TRUE,
    date_creation     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    date_modification DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_push_token_client FOREIGN KEY (id_client) REFERENCES client(id_client) ON DELETE CASCADE,
    CONSTRAINT chk_push_platform CHECK (platform IN ('ANDROID', 'IOS', 'WEB'))
);

CREATE TABLE adresse (
    id_adresse      BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_client       BIGINT NULL,
    id_magasin      BIGINT NULL,
    id_entrepot     BIGINT NULL,
    id_type_adresse BIGINT NOT NULL,
    rue             VARCHAR(255) NOT NULL,
    complement      VARCHAR(255) NULL,
    ville           VARCHAR(100) NOT NULL,
    code_postal     VARCHAR(15) NOT NULL,
    pays            VARCHAR(100) NOT NULL DEFAULT 'France',
    latitude        DECIMAL(9,6) NULL,
    longitude       DECIMAL(9,6) NULL,
    est_defaut      BOOLEAN NOT NULL DEFAULT FALSE,
    date_creation   DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_adresse_owner CHECK (
        (id_client IS NOT NULL AND id_magasin IS NULL  AND id_entrepot IS NULL) OR
        (id_client IS NULL  AND id_magasin IS NOT NULL AND id_entrepot IS NULL) OR
        (id_client IS NULL  AND id_magasin IS NULL     AND id_entrepot IS NOT NULL)
    ),
    CONSTRAINT fk_adresse_client   FOREIGN KEY (id_client)       REFERENCES client(id_client)     ON DELETE CASCADE,
    CONSTRAINT fk_adresse_magasin  FOREIGN KEY (id_magasin)      REFERENCES magasin(id_magasin)   ON DELETE CASCADE,
    CONSTRAINT fk_adresse_entrepot FOREIGN KEY (id_entrepot)     REFERENCES entrepot(id_entrepot) ON DELETE CASCADE,
    CONSTRAINT fk_adresse_type     FOREIGN KEY (id_type_adresse) REFERENCES type_adresse(id_type_adresse)
);

CREATE TABLE abonnement_client (
    id_abonnement        BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_client            BIGINT NOT NULL,
    id_statut_abonnement BIGINT NOT NULL,
    date_debut           DATE NOT NULL,
    date_fin             DATE NOT NULL,
    montant_paye         DECIMAL(10,2) NOT NULL,
    date_paiement        DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    renouvellement_auto  BOOLEAN NOT NULL DEFAULT TRUE,
    date_resiliation     DATETIME NULL,
    CONSTRAINT fk_abonnement_client FOREIGN KEY (id_client)            REFERENCES client(id_client),
    CONSTRAINT fk_abonnement_statut FOREIGN KEY (id_statut_abonnement) REFERENCES statut_abonnement(id_statut_abonnement),
    CONSTRAINT chk_abonnement_dates CHECK (date_fin > date_debut)
);

CREATE TABLE points_fidelite (
    id_points_fidelite  BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_client           BIGINT NOT NULL UNIQUE,
    solde_points        INT NOT NULL DEFAULT 0,
    total_achats_annuel DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    date_debut_periode  DATE NOT NULL,
    date_modification   DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_points_client  FOREIGN KEY (id_client) REFERENCES client(id_client) ON DELETE CASCADE,
    CONSTRAINT chk_points_solde  CHECK (solde_points >= 0),
    CONSTRAINT chk_points_achats CHECK (total_achats_annuel >= 0)
);

CREATE TABLE audit_log (
    id_audit        BIGINT AUTO_INCREMENT PRIMARY KEY,
    table_name      VARCHAR(100) NOT NULL,
    operation_type  VARCHAR(20) NOT NULL,
    record_id       BIGINT NOT NULL,
    donnees_avant   JSON,
    donnees_apres   JSON,
    user_identifier VARCHAR(100),
    date_operation  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE favori_magasin (
    id_client  BIGINT NOT NULL,
    id_magasin BIGINT NOT NULL,
    date_ajout DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    principal  BOOLEAN NOT NULL DEFAULT FALSE,
    PRIMARY KEY (id_client, id_magasin),
    CONSTRAINT fk_favori_magasin_client  FOREIGN KEY (id_client)  REFERENCES client(id_client)   ON DELETE CASCADE,
    CONSTRAINT fk_favori_magasin_magasin FOREIGN KEY (id_magasin) REFERENCES magasin(id_magasin) ON DELETE CASCADE
);

CREATE TABLE client_auth_provider (
    id_client_auth_provider BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_client               BIGINT NOT NULL,
    id_provider_auth        BIGINT NOT NULL,
    provider_user_id        VARCHAR(255) NOT NULL,
    email_provider          VARCHAR(150) NULL,
    date_liaison            DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    actif                   BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT fk_client_auth_provider_client   FOREIGN KEY (id_client)        REFERENCES client(id_client)           ON DELETE CASCADE,
    CONSTRAINT fk_client_auth_provider_provider FOREIGN KEY (id_provider_auth) REFERENCES provider_auth(id_provider_auth),
    CONSTRAINT uq_client_provider  UNIQUE (id_client, id_provider_auth),
    CONSTRAINT uq_provider_user    UNIQUE (id_provider_auth, provider_user_id)
);

CREATE TABLE reset_password_token (
    id_reset_password_token BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_client               BIGINT NOT NULL,
    token                   VARCHAR(255) NOT NULL UNIQUE,
    expire_le               DATETIME NOT NULL,
    utilise                 BOOLEAN NOT NULL DEFAULT FALSE,
    date_utilisation        DATETIME NULL,
    date_creation           DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_reset_password_token_client FOREIGN KEY (id_client) REFERENCES client(id_client) ON DELETE CASCADE
);

-- ============================================================
-- CATALOGUE
-- ============================================================

CREATE TABLE categorie (
    id_categorie      BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_type_categorie BIGINT NOT NULL,
    nom               VARCHAR(100) NOT NULL UNIQUE,
    description       TEXT,
    actif             BOOLEAN NOT NULL DEFAULT TRUE,
    date_creation     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_categorie_type FOREIGN KEY (id_type_categorie) REFERENCES type_categorie(id_type_categorie)
);

CREATE TABLE type_garantie (
    id_type_garantie BIGINT AUTO_INCREMENT PRIMARY KEY,
    code             VARCHAR(50) NOT NULL UNIQUE,
    description      VARCHAR(255),
    duree_mois       INT NOT NULL,
    prix_extension   DECIMAL(10,2) NULL,
    id_categorie     BIGINT NULL,              -- null = applicable à toutes les catégories
    CONSTRAINT chk_type_garantie_duree CHECK (duree_mois > 0),
    CONSTRAINT fk_type_garantie_categorie FOREIGN KEY (id_categorie) REFERENCES categorie(id_categorie) ON DELETE SET NULL
);
CREATE INDEX idx_type_garantie_categorie ON type_garantie(id_categorie);

CREATE TABLE produit (
    id_produit        BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_categorie      BIGINT NOT NULL,
    nom               VARCHAR(255) NOT NULL,
    slug              VARCHAR(255) NOT NULL UNIQUE,
    description       TEXT,
    resume_court      VARCHAR(500) NULL,
    date_sortie       DATE NULL,
    editeur           VARCHAR(150) NULL,
    constructeur      VARCHAR(150) NULL,
    pegi              INT NULL,
    marque            VARCHAR(150) NULL,
    actif             BOOLEAN NOT NULL DEFAULT TRUE,
    deleted           BOOLEAN NOT NULL DEFAULT FALSE,
    niveau_acces_min  VARCHAR(20) NOT NULL DEFAULT 'NORMAL',
    langue            VARCHAR(10) NOT NULL DEFAULT 'fr',
    mis_en_avant      BOOLEAN NOT NULL DEFAULT FALSE,
    date_creation     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    date_modification DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_produit_categorie FOREIGN KEY (id_categorie) REFERENCES categorie(id_categorie),
    CONSTRAINT chk_produit_pegi    CHECK (pegi IS NULL OR pegi BETWEEN 3 AND 18),
    CONSTRAINT chk_produit_niveau  CHECK (niveau_acces_min IN ('NORMAL', 'PREMIUM', 'ULTIMATE'))
);

CREATE TABLE edition_produit (
    id_edition        BIGINT AUTO_INCREMENT PRIMARY KEY,
    code              VARCHAR(50)  NOT NULL,
    libelle           VARCHAR(100) NOT NULL,
    ordre_affichage   INT          NOT NULL DEFAULT 0,
    actif             BOOLEAN      NOT NULL DEFAULT TRUE,
    CONSTRAINT uq_edition_code UNIQUE (code)
);

CREATE TABLE produit_variant (
    id_variant             BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_produit             BIGINT NOT NULL,
    sku                    VARCHAR(100) NOT NULL UNIQUE,
    ean                    VARCHAR(50) NULL UNIQUE,
    id_plateforme          BIGINT NULL,
    id_format_produit      BIGINT NOT NULL,
    id_statut_produit      BIGINT NOT NULL,
    nom_commercial         VARCHAR(255) NOT NULL,
    reference_fournisseur  VARCHAR(100) NULL,
    id_edition             BIGINT NULL,
    couleur                VARCHAR(100) NULL,
    taille                 VARCHAR(50) NULL,
    capacite_stockage      VARCHAR(100) NULL,
    langue_vente           VARCHAR(10) NOT NULL DEFAULT 'fr',
    scelle                 BOOLEAN NOT NULL DEFAULT FALSE,
    est_demat              BOOLEAN NOT NULL DEFAULT FALSE,
    est_tcg_unitaire       BOOLEAN NOT NULL DEFAULT FALSE,
    est_reprise            BOOLEAN NOT NULL DEFAULT FALSE,
    necessite_numero_serie BOOLEAN NOT NULL DEFAULT FALSE,
    poids_grammes          INT NULL,
    id_taux_tva            BIGINT NULL,
    actif                  BOOLEAN NOT NULL DEFAULT TRUE,
    version                BIGINT NOT NULL DEFAULT 0,
    date_creation          DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    date_modification      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_variant_produit    FOREIGN KEY (id_produit)        REFERENCES produit(id_produit)               ON DELETE CASCADE,
    CONSTRAINT fk_variant_plateforme FOREIGN KEY (id_plateforme)     REFERENCES plateforme(id_plateforme),
    CONSTRAINT fk_variant_format     FOREIGN KEY (id_format_produit) REFERENCES format_produit(id_format_produit),
    CONSTRAINT fk_variant_statut     FOREIGN KEY (id_statut_produit) REFERENCES statut_produit(id_statut_produit),
    CONSTRAINT fk_variant_tva        FOREIGN KEY (id_taux_tva)       REFERENCES taux_tva(id_taux_tva),
    CONSTRAINT fk_variant_edition    FOREIGN KEY (id_edition)        REFERENCES edition_produit(id_edition)
);

CREATE TABLE produit_prix (
    id_prix        BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_variant     BIGINT NOT NULL,
    prix_neuf      DECIMAL(10,2) NULL,
    prix_occasion  DECIMAL(10,2) NULL,
    prix_reprise   DECIMAL(10,2) NULL,
    prix_location  DECIMAL(10,2) NULL,
    date_debut     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    date_fin       DATETIME NULL,
    actif          BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT fk_produit_prix_variant FOREIGN KEY (id_variant) REFERENCES produit_variant(id_variant) ON DELETE CASCADE,
    CONSTRAINT chk_prix_dates CHECK (date_fin IS NULL OR date_fin > date_debut)
);

CREATE TABLE produit_image (
    id_image        BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_variant      BIGINT NOT NULL,
    url             VARCHAR(255) NOT NULL,
    alt             VARCHAR(255) NOT NULL DEFAULT '',
    decorative      BOOLEAN NOT NULL DEFAULT FALSE,
    principale      BOOLEAN NOT NULL DEFAULT FALSE,
    ordre_affichage INT NOT NULL DEFAULT 0,
    date_creation   DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_image_variant FOREIGN KEY (id_variant) REFERENCES produit_variant(id_variant) ON DELETE CASCADE
);

CREATE TABLE produit_video (
    id_video        BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_produit      BIGINT NOT NULL,
    url             VARCHAR(255) NOT NULL,
    titre           VARCHAR(255) NOT NULL,
    ordre_affichage INT NOT NULL DEFAULT 0,
    langue          VARCHAR(10) NOT NULL DEFAULT 'fr',
    sous_titres_url VARCHAR(255) NULL,
    audio_desc_url  VARCHAR(255) NULL,
    transcription   TEXT NULL,
    date_creation   DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_video_produit FOREIGN KEY (id_produit) REFERENCES produit(id_produit) ON DELETE CASCADE
);

CREATE TABLE produit_screenshot (
    id_screenshot   BIGINT          NOT NULL AUTO_INCREMENT,
    id_produit      BIGINT          NOT NULL,
    url             VARCHAR(255)    NOT NULL,
    alt             VARCHAR(255)    NOT NULL DEFAULT '',
    ordre_affichage INT             NOT NULL DEFAULT 0,
    date_creation   DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id_screenshot),
    CONSTRAINT fk_screenshot_produit FOREIGN KEY (id_produit) REFERENCES produit(id_produit) ON DELETE CASCADE
);

CREATE TABLE favori_produit (
    id_client  BIGINT NOT NULL,
    id_produit BIGINT NOT NULL,
    date_ajout DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id_client, id_produit),
    CONSTRAINT fk_favori_produit_client  FOREIGN KEY (id_client)  REFERENCES client(id_client)   ON DELETE CASCADE,
    CONSTRAINT fk_favori_produit_produit FOREIGN KEY (id_produit) REFERENCES produit(id_produit) ON DELETE CASCADE
);

-- ============================================================
-- TCG
-- ============================================================

CREATE TABLE tcg_jeu (
    id_tcg_jeu BIGINT AUTO_INCREMENT PRIMARY KEY,
    code       VARCHAR(50) NOT NULL UNIQUE,
    nom        VARCHAR(100) NOT NULL UNIQUE,
    editeur    VARCHAR(150) NULL,
    actif      BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE tcg_extension (
    id_tcg_extension BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_tcg_jeu       BIGINT NOT NULL,
    code             VARCHAR(50) NOT NULL UNIQUE,
    nom              VARCHAR(150) NOT NULL,
    date_sortie      DATE NULL,
    actif            BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT fk_tcg_extension_jeu FOREIGN KEY (id_tcg_jeu) REFERENCES tcg_jeu(id_tcg_jeu)
);

CREATE TABLE tcg_carte_reference (
    id_tcg_carte_reference BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_tcg_extension       BIGINT NOT NULL,
    id_produit             BIGINT NULL,
    nom_carte              VARCHAR(255) NOT NULL,
    numero_carte           VARCHAR(50) NULL,
    rarete                 VARCHAR(50) NULL,
    type_carte             VARCHAR(100) NULL,
    sous_type              VARCHAR(100) NULL,
    element_couleur        VARCHAR(100) NULL,
    pv_attaque             VARCHAR(50) NULL,
    artiste                VARCHAR(150) NULL,
    illustration_url       VARCHAR(255) NULL,
    UNIQUE (id_tcg_extension, nom_carte, numero_carte),
    CONSTRAINT fk_tcg_carte_reference_extension FOREIGN KEY (id_tcg_extension) REFERENCES tcg_extension(id_tcg_extension),
    CONSTRAINT fk_tcg_carte_reference_produit   FOREIGN KEY (id_produit)        REFERENCES produit(id_produit)
);

CREATE TABLE tcg_carte_inventaire (
    id_tcg_carte_inventaire BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_tcg_carte_reference  BIGINT NOT NULL,
    id_variant              BIGINT NULL,
    id_magasin              BIGINT NOT NULL,
    id_etat_carte_tcg       BIGINT NOT NULL,
    langue                  VARCHAR(10) NOT NULL DEFAULT 'fr',
    foil                    BOOLEAN NOT NULL DEFAULT FALSE,
    reverse_foil            BOOLEAN NOT NULL DEFAULT FALSE,
    alternate_art           BOOLEAN NOT NULL DEFAULT FALSE,
    gradation               VARCHAR(50) NULL,
    numero_serie_interne    VARCHAR(100) NULL UNIQUE,
    prix_achat              DECIMAL(10,2) NULL,
    prix_vente              DECIMAL(10,2) NOT NULL,
    provenance              VARCHAR(30) NOT NULL DEFAULT 'STOCK',
    disponible              BOOLEAN NOT NULL DEFAULT TRUE,
    date_creation           DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_tcg_inv_reference FOREIGN KEY (id_tcg_carte_reference) REFERENCES tcg_carte_reference(id_tcg_carte_reference),
    CONSTRAINT fk_tcg_inv_variant   FOREIGN KEY (id_variant)             REFERENCES produit_variant(id_variant),
    CONSTRAINT fk_tcg_inv_magasin   FOREIGN KEY (id_magasin)             REFERENCES magasin(id_magasin),
    CONSTRAINT fk_tcg_inv_etat      FOREIGN KEY (id_etat_carte_tcg)      REFERENCES etat_carte_tcg(id_etat_carte_tcg),
    CONSTRAINT chk_tcg_prix_vente   CHECK (prix_vente >= 0),
    CONSTRAINT chk_tcg_prix_achat   CHECK (prix_achat IS NULL OR prix_achat >= 0),
    CONSTRAINT chk_tcg_provenance   CHECK (provenance IN ('STOCK', 'REPRISE', 'ACHAT_DIRECT'))
);

-- ============================================================
-- PANIER / COMMANDE / FACTURE
-- ============================================================

CREATE TABLE panier (
    id_panier              BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_client              BIGINT NOT NULL,
    id_statut_panier       BIGINT NOT NULL,
    id_canal_vente         BIGINT NOT NULL,
    code_promo             VARCHAR(50) NULL,
    date_creation          DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    date_derniere_activite DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_panier_client FOREIGN KEY (id_client)        REFERENCES client(id_client) ON DELETE CASCADE,
    CONSTRAINT fk_panier_statut FOREIGN KEY (id_statut_panier) REFERENCES statut_panier(id_statut_panier),
    CONSTRAINT fk_panier_canal  FOREIGN KEY (id_canal_vente)   REFERENCES canal_vente(id_canal_vente)
);

CREATE TABLE ligne_panier (
    id_ligne_panier  BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_panier        BIGINT NOT NULL,
    id_variant       BIGINT NOT NULL,
    quantite         INT NOT NULL,
    prix_unitaire    DECIMAL(10,2) NOT NULL,
    id_type_garantie BIGINT NULL,              -- garantie optionnelle souscrite sur cette ligne
    date_creation    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_ligne_panier_panier   FOREIGN KEY (id_panier)         REFERENCES panier(id_panier)               ON DELETE CASCADE,
    CONSTRAINT fk_ligne_panier_variant  FOREIGN KEY (id_variant)        REFERENCES produit_variant(id_variant),
    CONSTRAINT fk_ligne_panier_garantie FOREIGN KEY (id_type_garantie)  REFERENCES type_garantie(id_type_garantie) ON DELETE SET NULL,
    CONSTRAINT chk_ligne_panier_quantite CHECK (quantite > 0),
    CONSTRAINT chk_ligne_panier_prix     CHECK (prix_unitaire >= 0),
    CONSTRAINT uq_panier_variant UNIQUE (id_panier, id_variant)
);

CREATE TABLE commande (
    id_commande              BIGINT AUTO_INCREMENT PRIMARY KEY,
    reference_commande       VARCHAR(50) NOT NULL UNIQUE,
    id_client                BIGINT NOT NULL,
    id_statut_commande       BIGINT NOT NULL,
    id_canal_vente           BIGINT NOT NULL,
    id_mode_livraison        BIGINT NOT NULL,
    id_adresse_livraison     BIGINT NULL,
    id_magasin_retrait       BIGINT NULL,
    id_entrepot_expedition   BIGINT NULL,
    id_mode_paiement         BIGINT NULL,
    id_bon_achat             BIGINT NULL,
    code_promo               VARCHAR(50) NULL,
    sous_total               DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    montant_remise           DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    frais_livraison          DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    montant_total            DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    commentaire_client       TEXT NULL,
    version                  BIGINT NOT NULL DEFAULT 0,
    date_commande            DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    date_paiement            DATETIME NULL,
    date_preparation         DATETIME NULL,
    date_expedition          DATETIME NULL,
    date_livraison_prevue    DATETIME NULL,
    date_livraison_reelle    DATETIME NULL,
    date_retrait             DATETIME NULL,
    CONSTRAINT fk_commande_client             FOREIGN KEY (id_client)              REFERENCES client(id_client),
    CONSTRAINT fk_commande_statut             FOREIGN KEY (id_statut_commande)     REFERENCES statut_commande(id_statut_commande),
    CONSTRAINT fk_commande_canal              FOREIGN KEY (id_canal_vente)         REFERENCES canal_vente(id_canal_vente),
    CONSTRAINT fk_commande_mode_livraison     FOREIGN KEY (id_mode_livraison)      REFERENCES mode_livraison(id_mode_livraison),
    CONSTRAINT fk_commande_adresse_livraison  FOREIGN KEY (id_adresse_livraison)   REFERENCES adresse(id_adresse),
    CONSTRAINT fk_commande_magasin_retrait    FOREIGN KEY (id_magasin_retrait)     REFERENCES magasin(id_magasin),
    CONSTRAINT fk_commande_entrepot           FOREIGN KEY (id_entrepot_expedition) REFERENCES entrepot(id_entrepot),
    CONSTRAINT fk_commande_mode_paiement      FOREIGN KEY (id_mode_paiement)       REFERENCES mode_paiement(id_mode_paiement),
    CONSTRAINT fk_commande_bon                FOREIGN KEY (id_bon_achat)           REFERENCES bon_achat(id_bon_achat),
    CONSTRAINT chk_commande_total CHECK (montant_total >= 0),
    CONSTRAINT chk_commande_expedition CHECK (
        NOT (id_magasin_retrait IS NOT NULL AND id_entrepot_expedition IS NOT NULL)
    )
);

CREATE TABLE precommande (
    id_precommande             BIGINT AUTO_INCREMENT PRIMARY KEY,
    reference_precommande      VARCHAR(50) NOT NULL UNIQUE,
    id_client                  BIGINT NOT NULL,
    id_statut_precommande      BIGINT NOT NULL,
    id_canal_vente             BIGINT NOT NULL,
    id_mode_paiement           BIGINT NULL,
    acompte_paye               DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    montant_total_estime       DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    date_precommande           DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    date_disponibilite_estimee DATETIME NULL,
    date_conversion_commande   DATETIME NULL,
    commentaire_client         TEXT NULL,
    CONSTRAINT fk_precommande_client        FOREIGN KEY (id_client)             REFERENCES client(id_client),
    CONSTRAINT fk_precommande_statut        FOREIGN KEY (id_statut_precommande) REFERENCES statut_precommande(id_statut_precommande),
    CONSTRAINT fk_precommande_canal         FOREIGN KEY (id_canal_vente)        REFERENCES canal_vente(id_canal_vente),
    CONSTRAINT fk_precommande_mode_paiement FOREIGN KEY (id_mode_paiement)      REFERENCES mode_paiement(id_mode_paiement),
    CONSTRAINT chk_precommande_montants CHECK (acompte_paye >= 0 AND montant_total_estime >= 0)
);

CREATE TABLE precommande_ligne (
    id_precommande_ligne BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_precommande       BIGINT NOT NULL,
    id_variant           BIGINT NOT NULL,
    quantite             INT NOT NULL,
    prix_unitaire_estime DECIMAL(10,2) NOT NULL,
    montant_ligne_estime DECIMAL(10,2) NOT NULL,
    CONSTRAINT fk_precommande_ligne_precommande FOREIGN KEY (id_precommande) REFERENCES precommande(id_precommande) ON DELETE CASCADE,
    CONSTRAINT fk_precommande_ligne_variant     FOREIGN KEY (id_variant)     REFERENCES produit_variant(id_variant),
    CONSTRAINT chk_precommande_ligne_quantite CHECK (quantite > 0),
    CONSTRAINT chk_precommande_ligne_prix     CHECK (prix_unitaire_estime >= 0),
    CONSTRAINT chk_precommande_ligne_montant  CHECK (montant_ligne_estime >= 0)
);

CREATE TABLE ligne_commande (
    id_ligne_commande BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_commande       BIGINT NOT NULL,
    id_variant        BIGINT NOT NULL,
    id_prix           BIGINT NULL,
    quantite          INT NOT NULL,
    prix_unitaire     DECIMAL(10,2) NOT NULL,
    montant_ligne     DECIMAL(10,2) NOT NULL,
    CONSTRAINT fk_ligne_commande_commande FOREIGN KEY (id_commande) REFERENCES commande(id_commande)         ON DELETE CASCADE,
    CONSTRAINT fk_ligne_commande_variant  FOREIGN KEY (id_variant)  REFERENCES produit_variant(id_variant),
    CONSTRAINT fk_ligne_commande_prix     FOREIGN KEY (id_prix)     REFERENCES produit_prix(id_prix),
    CONSTRAINT chk_ligne_commande_quantite CHECK (quantite > 0),
    CONSTRAINT chk_ligne_commande_prix     CHECK (prix_unitaire >= 0),
    CONSTRAINT chk_ligne_commande_montant  CHECK (montant_ligne >= 0)
);

CREATE TABLE paiement_transaction (
    id_paiement_transaction BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_commande             BIGINT NULL,
    id_precommande          BIGINT NULL,
    id_mode_paiement        BIGINT NOT NULL,
    id_statut_paiement      BIGINT NOT NULL,
    provider_reference      VARCHAR(255) NULL,
    montant                 DECIMAL(10,2) NOT NULL,
    devise                  VARCHAR(10) NOT NULL DEFAULT 'EUR',
    date_creation           DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    date_confirmation       DATETIME NULL,
    commentaire             VARCHAR(255) NULL,
    CONSTRAINT fk_paiement_transaction_commande    FOREIGN KEY (id_commande)        REFERENCES commande(id_commande)           ON DELETE CASCADE,
    CONSTRAINT fk_paiement_transaction_precommande FOREIGN KEY (id_precommande)     REFERENCES precommande(id_precommande)     ON DELETE CASCADE,
    CONSTRAINT fk_paiement_transaction_mode        FOREIGN KEY (id_mode_paiement)   REFERENCES mode_paiement(id_mode_paiement),
    CONSTRAINT fk_paiement_transaction_statut      FOREIGN KEY (id_statut_paiement) REFERENCES statut_paiement(id_statut_paiement),
    CONSTRAINT chk_paiement_transaction_owner CHECK (
        (id_commande IS NOT NULL AND id_precommande IS NULL) OR
        (id_commande IS NULL AND id_precommande IS NOT NULL)
    ),
    CONSTRAINT chk_paiement_transaction_montant CHECK (montant >= 0)
);

CREATE TABLE facture (
    id_facture        BIGINT AUTO_INCREMENT PRIMARY KEY,
    reference_facture VARCHAR(50) NOT NULL UNIQUE,
    id_commande       BIGINT NULL,
    id_client         BIGINT NULL,
    id_magasin        BIGINT NOT NULL,
    id_employe        BIGINT NULL,
    id_mode_paiement  BIGINT NOT NULL,
    id_statut_facture BIGINT NOT NULL,
    id_bon_achat      BIGINT NULL,
    id_contexte_vente BIGINT NOT NULL,
    date_facture      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    montant_total     DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    montant_ht_total  DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    montant_tva_total DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    montant_remise    DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    montant_final     DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    nom_client        VARCHAR(100) NULL,
    email_client      VARCHAR(150) NULL,
    telephone_client  VARCHAR(20) NULL,
    version           BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT fk_facture_commande       FOREIGN KEY (id_commande)       REFERENCES commande(id_commande),
    CONSTRAINT fk_facture_client         FOREIGN KEY (id_client)         REFERENCES client(id_client),
    CONSTRAINT fk_facture_magasin        FOREIGN KEY (id_magasin)        REFERENCES magasin(id_magasin),
    CONSTRAINT fk_facture_employe        FOREIGN KEY (id_employe)        REFERENCES employe(id_employe),
    CONSTRAINT fk_facture_mode_paiement  FOREIGN KEY (id_mode_paiement)  REFERENCES mode_paiement(id_mode_paiement),
    CONSTRAINT fk_facture_statut         FOREIGN KEY (id_statut_facture) REFERENCES statut_facture(id_statut_facture),
    CONSTRAINT fk_facture_bon            FOREIGN KEY (id_bon_achat)      REFERENCES bon_achat(id_bon_achat),
    CONSTRAINT fk_facture_contexte_vente FOREIGN KEY (id_contexte_vente) REFERENCES contexte_vente(id_contexte_vente),
    CONSTRAINT chk_facture_final CHECK (montant_final >= 0)
);

-- ============================================================
-- BON_ACHAT et HISTORIQUE_POINTS :
-- ============================================================

CREATE TABLE bon_achat (
    id_bon_achat     BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_client        BIGINT NOT NULL,
    code_bon         VARCHAR(50) NOT NULL UNIQUE,
    valeur           DECIMAL(10,2) NOT NULL,
    points_utilises  INT NOT NULL,
    utilise          BOOLEAN NOT NULL DEFAULT FALSE,
    id_facture       BIGINT NULL,
    date_creation    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    date_expiration  DATETIME NULL,
    date_utilisation DATETIME NULL,
    CONSTRAINT fk_bon_client  FOREIGN KEY (id_client)  REFERENCES client(id_client),
    CONSTRAINT fk_bon_facture FOREIGN KEY (id_facture) REFERENCES facture(id_facture),
    CONSTRAINT chk_bon_valeur CHECK (valeur > 0),
    CONSTRAINT chk_bon_points CHECK (points_utilises > 0),
    CONSTRAINT chk_bon_dates  CHECK (date_expiration IS NULL OR date_expiration > date_creation)
);

CREATE TABLE historique_points (
    id_historique  BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_client      BIGINT NOT NULL,
    id_facture     BIGINT NULL,
    type_operation VARCHAR(30) NOT NULL,
    points         INT NOT NULL,
    commentaire    VARCHAR(255),
    date_operation DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_historique_points_client  FOREIGN KEY (id_client)  REFERENCES client(id_client),
    CONSTRAINT fk_historique_points_facture FOREIGN KEY (id_facture) REFERENCES facture(id_facture)
);

CREATE TABLE ligne_facture (
    id_ligne_facture  BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_facture        BIGINT NOT NULL,
    id_ligne_commande BIGINT NULL,
    id_variant        BIGINT NOT NULL,
    id_prix           BIGINT NULL,
    id_taux_tva       BIGINT NULL,
    quantite          INT NOT NULL,
    prix_unitaire     DECIMAL(10,2) NOT NULL,
    taux_tva_applique DECIMAL(5,2)  NOT NULL DEFAULT 20.00,
    montant_ligne     DECIMAL(10,2) NOT NULL,
    montant_ht_ligne  DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    montant_tva_ligne DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    garantie_label    VARCHAR(200)  NULL,
    garantie_prix     DECIMAL(10,2) NULL,
    CONSTRAINT fk_ligne_facture_facture        FOREIGN KEY (id_facture)        REFERENCES facture(id_facture)             ON DELETE CASCADE,
    CONSTRAINT fk_ligne_facture_ligne_commande FOREIGN KEY (id_ligne_commande) REFERENCES ligne_commande(id_ligne_commande),
    CONSTRAINT fk_ligne_facture_variant        FOREIGN KEY (id_variant)        REFERENCES produit_variant(id_variant),
    CONSTRAINT fk_ligne_facture_prix           FOREIGN KEY (id_prix)           REFERENCES produit_prix(id_prix),
    CONSTRAINT fk_ligne_facture_tva            FOREIGN KEY (id_taux_tva)       REFERENCES taux_tva(id_taux_tva),
    CONSTRAINT chk_ligne_facture_quantite CHECK (quantite > 0),
    CONSTRAINT chk_ligne_facture_prix     CHECK (prix_unitaire >= 0),
    CONSTRAINT chk_ligne_facture_montant  CHECK (montant_ligne >= 0),
    CONSTRAINT chk_ligne_facture_ht       CHECK (montant_ht_ligne >= 0),
    CONSTRAINT chk_ligne_facture_tva_m    CHECK (montant_tva_ligne >= 0)
);

-- ============================================================
-- OCCASION / REPRISE
-- ============================================================

CREATE TABLE reprise (
    id_reprise                   BIGINT AUTO_INCREMENT PRIMARY KEY,
    reference_reprise            VARCHAR(50) NOT NULL UNIQUE,
    id_client                    BIGINT NULL,
    id_employe                   BIGINT NOT NULL,
    id_magasin                   BIGINT NOT NULL,
    id_statut_reprise            BIGINT NOT NULL,
    id_mode_compensation_reprise BIGINT NOT NULL,
    montant_total_estime         DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    montant_total_valide         DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    commentaire                  TEXT NULL,
    date_creation                DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    date_validation              DATETIME NULL,
    CONSTRAINT fk_reprise_client            FOREIGN KEY (id_client)                    REFERENCES client(id_client),
    CONSTRAINT fk_reprise_employe           FOREIGN KEY (id_employe)                   REFERENCES employe(id_employe),
    CONSTRAINT fk_reprise_magasin           FOREIGN KEY (id_magasin)                   REFERENCES magasin(id_magasin),
    CONSTRAINT fk_reprise_statut            FOREIGN KEY (id_statut_reprise)            REFERENCES statut_reprise(id_statut_reprise),
    CONSTRAINT fk_reprise_mode_compensation FOREIGN KEY (id_mode_compensation_reprise) REFERENCES mode_compensation_reprise(id_mode_compensation_reprise),
    CONSTRAINT chk_reprise_montants CHECK (montant_total_estime >= 0 AND montant_total_valide >= 0)
);

CREATE TABLE reprise_ligne (
    id_reprise_ligne        BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_reprise              BIGINT NOT NULL,
    id_variant              BIGINT NULL,
    id_tcg_carte_reference  BIGINT NULL,
    description_libre       VARCHAR(255) NULL,
    quantite                INT NOT NULL DEFAULT 1,
    etat_general            VARCHAR(50) NULL,
    prix_estime_unitaire    DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    prix_valide_unitaire    DECIMAL(10,2) NULL,
    cree_stock_occasion     BOOLEAN NOT NULL DEFAULT FALSE,
    numero_serie            VARCHAR(100) NULL,
    commentaires            TEXT NULL,
    CONSTRAINT fk_reprise_ligne_reprise       FOREIGN KEY (id_reprise)             REFERENCES reprise(id_reprise)                     ON DELETE CASCADE,
    CONSTRAINT fk_reprise_ligne_variant       FOREIGN KEY (id_variant)             REFERENCES produit_variant(id_variant),
    CONSTRAINT fk_reprise_ligne_tcg_reference FOREIGN KEY (id_tcg_carte_reference) REFERENCES tcg_carte_reference(id_tcg_carte_reference),
    CONSTRAINT chk_reprise_ligne_quantite     CHECK (quantite > 0),
    CONSTRAINT chk_reprise_ligne_prix_estime  CHECK (prix_estime_unitaire >= 0),
    CONSTRAINT chk_reprise_ligne_prix_valide  CHECK (prix_valide_unitaire IS NULL OR prix_valide_unitaire >= 0),
    CONSTRAINT chk_reprise_ligne_cible CHECK (
        (id_variant IS NOT NULL AND id_tcg_carte_reference IS NULL) OR
        (id_variant IS NULL AND id_tcg_carte_reference IS NOT NULL) OR
        (id_variant IS NULL AND id_tcg_carte_reference IS NULL AND description_libre IS NOT NULL)
    )
);

-- ============================================================
-- STOCK
-- ============================================================

CREATE TABLE stock_magasin (
    id_stock_magasin    BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_variant          BIGINT NOT NULL,
    id_magasin          BIGINT NOT NULL,
    quantite_neuf       INT NOT NULL DEFAULT 0,
    quantite_occasion   INT NOT NULL DEFAULT 0,
    quantite_reprise    INT NOT NULL DEFAULT 0,
    quantite_reservee   INT NOT NULL DEFAULT 0,
    quantite_disponible INT NOT NULL DEFAULT 0,
    version             BIGINT NOT NULL DEFAULT 0,
    date_modification   DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_stock_variant FOREIGN KEY (id_variant)  REFERENCES produit_variant(id_variant),
    CONSTRAINT fk_stock_magasin FOREIGN KEY (id_magasin)  REFERENCES magasin(id_magasin),
    CONSTRAINT uq_stock_variant_magasin UNIQUE (id_variant, id_magasin),
    CONSTRAINT chk_stock_neuf       CHECK (quantite_neuf >= 0),
    CONSTRAINT chk_stock_occasion   CHECK (quantite_occasion >= 0),
    CONSTRAINT chk_stock_reprise    CHECK (quantite_reprise >= 0),
    CONSTRAINT chk_stock_reservee   CHECK (quantite_reservee >= 0),
    CONSTRAINT chk_stock_disponible CHECK (quantite_disponible >= 0)
);

CREATE TABLE stock_entrepot (
    id_stock_entrepot   BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_variant          BIGINT NOT NULL,
    id_entrepot         BIGINT NOT NULL,
    quantite_neuf       INT NOT NULL DEFAULT 0,
    quantite_reservee   INT NOT NULL DEFAULT 0,
    quantite_disponible INT NOT NULL DEFAULT 0,
    version             BIGINT NOT NULL DEFAULT 0,
    date_modification   DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_stock_entrepot_variant   FOREIGN KEY (id_variant)  REFERENCES produit_variant(id_variant),
    CONSTRAINT fk_stock_entrepot_entrepot  FOREIGN KEY (id_entrepot) REFERENCES entrepot(id_entrepot),
    CONSTRAINT uq_stock_entrepot_variant   UNIQUE (id_variant, id_entrepot),
    CONSTRAINT chk_stock_entrepot_neuf       CHECK (quantite_neuf >= 0),
    CONSTRAINT chk_stock_entrepot_reservee   CHECK (quantite_reservee >= 0),
    CONSTRAINT chk_stock_entrepot_disponible CHECK (quantite_disponible >= 0)
);

CREATE TABLE reservation_stock (
    id_reservation_stock BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_commande          BIGINT NULL,
    id_panier            BIGINT NULL,
    id_variant           BIGINT NOT NULL,
    id_magasin           BIGINT NULL,
    id_entrepot          BIGINT NULL,
    quantite             INT NOT NULL,
    expire_le            DATETIME NULL,
    date_creation        DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_reservation_commande FOREIGN KEY (id_commande) REFERENCES commande(id_commande)   ON DELETE CASCADE,
    CONSTRAINT fk_reservation_panier   FOREIGN KEY (id_panier)   REFERENCES panier(id_panier)       ON DELETE CASCADE,
    CONSTRAINT fk_reservation_variant  FOREIGN KEY (id_variant)  REFERENCES produit_variant(id_variant),
    CONSTRAINT fk_reservation_magasin  FOREIGN KEY (id_magasin)  REFERENCES magasin(id_magasin),
    CONSTRAINT fk_reservation_entrepot FOREIGN KEY (id_entrepot) REFERENCES entrepot(id_entrepot),
    CONSTRAINT chk_reservation_quantite CHECK (quantite > 0),
    CONSTRAINT chk_reservation_owner CHECK (
        (id_commande IS NOT NULL AND id_panier IS NULL) OR
        (id_commande IS NULL     AND id_panier IS NOT NULL)
    ),
    CONSTRAINT chk_reservation_lieu CHECK (
        (id_magasin IS NOT NULL AND id_entrepot IS NULL) OR
        (id_magasin IS NULL     AND id_entrepot IS NOT NULL)
    )
);

CREATE TABLE mouvement_stock (
    id_mouvement      BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_variant        BIGINT NOT NULL,
    id_magasin        BIGINT NULL,
    id_entrepot       BIGINT NULL,
    id_type_mouvement BIGINT NOT NULL,
    quantite          INT NOT NULL,
    source_stock      VARCHAR(20) NOT NULL DEFAULT 'NEUF',
    date_mouvement    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    commentaire       TEXT NULL,
    CONSTRAINT fk_mouvement_variant  FOREIGN KEY (id_variant)        REFERENCES produit_variant(id_variant),
    CONSTRAINT fk_mouvement_magasin  FOREIGN KEY (id_magasin)        REFERENCES magasin(id_magasin),
    CONSTRAINT fk_mouvement_entrepot FOREIGN KEY (id_entrepot)       REFERENCES entrepot(id_entrepot),
    CONSTRAINT fk_mouvement_type     FOREIGN KEY (id_type_mouvement) REFERENCES type_mouvement(id_type_mouvement),
    CONSTRAINT chk_mouvement_source_stock CHECK (source_stock IN ('NEUF', 'OCCASION', 'REPRISE', 'TCG_UNITAIRE')),
    CONSTRAINT chk_mouvement_lieu CHECK (
        (id_magasin IS NOT NULL AND id_entrepot IS NULL) OR
        (id_magasin IS NULL     AND id_entrepot IS NOT NULL)
    )
);

-- ============================================================
-- AVIS / MODERATION
-- ============================================================

CREATE TABLE avis_produit (
    id_avis               BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_client             BIGINT NOT NULL,
    id_produit            BIGINT NOT NULL,
    id_statut_avis        BIGINT NOT NULL,
    note                  TINYINT NOT NULL,
    commentaire           TEXT NULL,
    id_employe_moderateur BIGINT NULL,
    motif_moderation      VARCHAR(255) NULL,
    date_creation         DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    date_modification     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    date_moderation       DATETIME NULL,
    CONSTRAINT fk_avis_client      FOREIGN KEY (id_client)             REFERENCES client(id_client)     ON DELETE CASCADE,
    CONSTRAINT fk_avis_produit     FOREIGN KEY (id_produit)            REFERENCES produit(id_produit)   ON DELETE CASCADE,
    CONSTRAINT fk_avis_statut      FOREIGN KEY (id_statut_avis)        REFERENCES statut_avis(id_statut_avis),
    CONSTRAINT fk_avis_moderateur  FOREIGN KEY (id_employe_moderateur) REFERENCES employe(id_employe),
    CONSTRAINT uq_avis_client_produit UNIQUE (id_client, id_produit),
    CONSTRAINT chk_avis_note CHECK (note BETWEEN 1 AND 5)
);

-- ============================================================
-- PROMOTIONS
-- ============================================================

CREATE TABLE promotion (
    id_promotion                  BIGINT AUTO_INCREMENT PRIMARY KEY,
    code_promo                    VARCHAR(50) NOT NULL UNIQUE,
    description                   TEXT,
    id_type_reduction             BIGINT NOT NULL,
    valeur                        DECIMAL(10,2) NOT NULL,
    date_debut                    DATETIME NOT NULL,
    date_fin                      DATETIME NOT NULL,
    montant_minimum_commande      DECIMAL(10,2) NULL DEFAULT NULL,
    nb_utilisations_max           INT  NULL DEFAULT NULL,
    nb_utilisations_max_client    INT  NULL DEFAULT NULL,
    nb_utilisations_actuel        INT  NOT NULL DEFAULT 0,
    cumulable                     BOOLEAN NOT NULL DEFAULT FALSE,
    actif                         BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT fk_promotion_type    FOREIGN KEY (id_type_reduction) REFERENCES type_reduction(id_type_reduction),
    CONSTRAINT chk_promotion_dates  CHECK (date_fin > date_debut),
    CONSTRAINT chk_promotion_valeur CHECK (valeur > 0),
    CONSTRAINT chk_promotion_utilisations CHECK (nb_utilisations_actuel >= 0)
);

CREATE TABLE promotion_usage (
    id_promotion_usage  BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_promotion        BIGINT        NOT NULL,
    id_client           BIGINT        NULL,
    id_commande         BIGINT        NULL,
    id_facture          BIGINT        NULL,
    montant_commande_ht DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    montant_remise      DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    date_utilisation    DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_promo_usage_promotion FOREIGN KEY (id_promotion) REFERENCES promotion(id_promotion),
    CONSTRAINT fk_promo_usage_client    FOREIGN KEY (id_client)    REFERENCES client(id_client),
    CONSTRAINT fk_promo_usage_commande  FOREIGN KEY (id_commande)  REFERENCES commande(id_commande),
    CONSTRAINT fk_promo_usage_facture   FOREIGN KEY (id_facture)   REFERENCES facture(id_facture),
    CONSTRAINT chk_promo_usage_montant  CHECK (montant_remise >= 0)
);

CREATE TABLE promotion_variant (
    id_promotion BIGINT NOT NULL,
    id_variant   BIGINT NOT NULL,
    PRIMARY KEY (id_promotion, id_variant),
    CONSTRAINT fk_promotion_variant_promotion FOREIGN KEY (id_promotion) REFERENCES promotion(id_promotion)     ON DELETE CASCADE,
    CONSTRAINT fk_promotion_variant_variant   FOREIGN KEY (id_variant)   REFERENCES produit_variant(id_variant) ON DELETE CASCADE
);

CREATE TABLE promotion_categorie (
    id_promotion BIGINT NOT NULL,
    id_categorie BIGINT NOT NULL,
    PRIMARY KEY (id_promotion, id_categorie),
    CONSTRAINT fk_promotion_categorie_promotion FOREIGN KEY (id_promotion) REFERENCES promotion(id_promotion) ON DELETE CASCADE,
    CONSTRAINT fk_promotion_categorie_categorie FOREIGN KEY (id_categorie) REFERENCES categorie(id_categorie) ON DELETE CASCADE
);

-- ============================================================
-- DEMATERIALISE / BIBLIOTHEQUE
-- ============================================================

CREATE TABLE cle_produit (
    id_cle_produit   BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_variant       BIGINT NOT NULL,
    cle_activation   VARCHAR(255) NOT NULL UNIQUE,
    utilisee         BOOLEAN NOT NULL DEFAULT FALSE,
    date_creation    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    date_utilisation DATETIME NULL,
    CONSTRAINT fk_cle_variant FOREIGN KEY (id_variant) REFERENCES produit_variant(id_variant) ON DELETE CASCADE
);

CREATE TABLE bibliotheque_client (
    id_bibliotheque  BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_client        BIGINT NOT NULL,
    id_variant       BIGINT NOT NULL,
    id_facture       BIGINT NOT NULL,
    id_cle_produit   BIGINT NULL,
    date_attribution DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_bibliotheque_client  FOREIGN KEY (id_client)      REFERENCES client(id_client)           ON DELETE CASCADE,
    CONSTRAINT fk_bibliotheque_variant FOREIGN KEY (id_variant)     REFERENCES produit_variant(id_variant),
    CONSTRAINT fk_bibliotheque_facture FOREIGN KEY (id_facture)     REFERENCES facture(id_facture),
    CONSTRAINT fk_bibliotheque_cle     FOREIGN KEY (id_cle_produit) REFERENCES cle_produit(id_cle_produit),
    CONSTRAINT uq_bibliotheque_client_variant UNIQUE (id_client, id_variant, id_facture)
);

-- ============================================================
-- UNITES VENDUES / GARANTIE / SAV / RETOURS
-- ============================================================

CREATE TABLE vente_unite (
    id_vente_unite   BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_ligne_facture BIGINT NOT NULL,
    numero_serie     VARCHAR(100) NULL UNIQUE,
    etat_unite       VARCHAR(50) NULL,
    source_stock     VARCHAR(20) NOT NULL DEFAULT 'NEUF',
    date_creation    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_vente_unite_ligne_facture FOREIGN KEY (id_ligne_facture) REFERENCES ligne_facture(id_ligne_facture) ON DELETE CASCADE,
    CONSTRAINT chk_vente_unite_source_stock CHECK (source_stock IN ('NEUF', 'OCCASION', 'REPRISE', 'TCG_UNITAIRE'))
);

CREATE TABLE garantie (
    id_garantie      BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_vente_unite   BIGINT NOT NULL UNIQUE,
    id_type_garantie BIGINT NOT NULL,
    date_debut       DATE NOT NULL,
    date_fin         DATE NOT NULL,
    est_etendue      BOOLEAN NOT NULL DEFAULT FALSE,
    date_extension   DATE NULL,
    CONSTRAINT fk_garantie_vente_unite FOREIGN KEY (id_vente_unite)   REFERENCES vente_unite(id_vente_unite) ON DELETE CASCADE,
    CONSTRAINT fk_garantie_type        FOREIGN KEY (id_type_garantie) REFERENCES type_garantie(id_type_garantie),
    CONSTRAINT chk_garantie_dates CHECK (date_fin > date_debut)
);

CREATE TABLE extension_garantie (
    id_extension     BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_garantie      BIGINT NOT NULL,
    id_type_garantie BIGINT NOT NULL,
    date_achat       DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    date_fin_etendue DATE NOT NULL,
    CONSTRAINT fk_extension_garantie FOREIGN KEY (id_garantie)      REFERENCES garantie(id_garantie)           ON DELETE CASCADE,
    CONSTRAINT fk_extension_type     FOREIGN KEY (id_type_garantie) REFERENCES type_garantie(id_type_garantie),
    CONSTRAINT chk_extension_dates CHECK (date_fin_etendue > DATE(date_achat))
);

CREATE TABLE retour_produit (
    id_retour         BIGINT AUTO_INCREMENT PRIMARY KEY,
    reference_retour  VARCHAR(50) NOT NULL UNIQUE,
    id_facture        BIGINT NOT NULL,
    id_client         BIGINT NULL,
    id_statut_retour  BIGINT NOT NULL,
    id_type_retour    BIGINT NOT NULL,
    motif_retour      VARCHAR(255) NULL,
    date_demande      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    date_traitement   DATETIME NULL,
    montant_rembourse DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    CONSTRAINT fk_retour_facture FOREIGN KEY (id_facture)       REFERENCES facture(id_facture),
    CONSTRAINT fk_retour_client  FOREIGN KEY (id_client)        REFERENCES client(id_client),
    CONSTRAINT fk_retour_statut  FOREIGN KEY (id_statut_retour) REFERENCES statut_retour(id_statut_retour),
    CONSTRAINT fk_retour_type    FOREIGN KEY (id_type_retour)   REFERENCES type_retour(id_type_retour),
    CONSTRAINT chk_retour_montant CHECK (montant_rembourse >= 0)
);

CREATE TABLE retour_ligne (
    id_retour_ligne  BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_retour        BIGINT NOT NULL,
    id_ligne_facture BIGINT NOT NULL,
    quantite         INT NOT NULL,
    motif            VARCHAR(255) NULL,
    CONSTRAINT fk_retour_ligne_retour  FOREIGN KEY (id_retour)        REFERENCES retour_produit(id_retour)       ON DELETE CASCADE,
    CONSTRAINT fk_retour_ligne_facture FOREIGN KEY (id_ligne_facture) REFERENCES ligne_facture(id_ligne_facture),
    CONSTRAINT chk_retour_ligne_quantite CHECK (quantite > 0)
);

CREATE TABLE dossier_sav (
    id_dossier_sav    BIGINT AUTO_INCREMENT PRIMARY KEY,
    reference_sav     VARCHAR(50) NOT NULL UNIQUE,
    id_vente_unite    BIGINT NOT NULL,
    id_garantie       BIGINT NULL,
    id_statut_sav     BIGINT NOT NULL,
    id_employe        BIGINT NULL,
    panne_declaree    TEXT NULL,
    diagnostic        TEXT NULL,
    solution_apportee TEXT NULL,
    date_ouverture    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    date_cloture      DATETIME NULL,
    CONSTRAINT fk_sav_vente_unite FOREIGN KEY (id_vente_unite) REFERENCES vente_unite(id_vente_unite),
    CONSTRAINT fk_sav_garantie    FOREIGN KEY (id_garantie)    REFERENCES garantie(id_garantie),
    CONSTRAINT fk_sav_statut      FOREIGN KEY (id_statut_sav)  REFERENCES statut_sav(id_statut_sav),
    CONSTRAINT fk_sav_employe     FOREIGN KEY (id_employe)     REFERENCES employe(id_employe)
);

-- ============================================================
-- SECURITE / LOGS DE CONNEXION & SESSIONS (V16)
-- ============================================================

CREATE TABLE connexion_log (
    id_connexion_log BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_type        VARCHAR(10)  NOT NULL,
    user_id          BIGINT       NULL,
    email_tente      VARCHAR(150) NULL,
    ip               VARCHAR(45)  NOT NULL,
    user_agent       VARCHAR(512) NULL,
    succes           BOOLEAN      NOT NULL DEFAULT FALSE,
    motif_echec      VARCHAR(100) NULL,
    provider_auth    VARCHAR(50)  NULL,
    date_connexion   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_connexion_log_user_type CHECK (user_type IN ('CLIENT', 'EMPLOYE'))
);

CREATE TABLE session_active (
    id_session              BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_type               VARCHAR(10)  NOT NULL,
    user_id                 BIGINT       NOT NULL,
    token_session           VARCHAR(512) NOT NULL UNIQUE,
    ip_creation             VARCHAR(45)  NOT NULL,
    ip_derniere_activite    VARCHAR(45)  NULL,
    user_agent              VARCHAR(512) NULL,
    actif                   BOOLEAN      NOT NULL DEFAULT TRUE,
    date_creation           DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    date_expiration         DATETIME     NOT NULL,
    date_derniere_activite  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    motif_invalidation      VARCHAR(100) NULL,
    CONSTRAINT chk_session_user_type CHECK (user_type IN ('CLIENT', 'EMPLOYE')),
    CONSTRAINT chk_session_dates     CHECK (date_expiration > date_creation)
);

CREATE TABLE tentative_connexion_echec (
    id_tentative       BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_type          VARCHAR(10)  NOT NULL,
    user_id            BIGINT       NULL,
    email_tente        VARCHAR(150) NULL,
    ip                 VARCHAR(45)  NOT NULL,
    nb_tentatives      INT          NOT NULL DEFAULT 1,
    premiere_tentative DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    derniere_tentative DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    bloque_jusqu_au    DATETIME     NULL,
    CONSTRAINT chk_tentative_user_type CHECK (user_type IN ('CLIENT', 'EMPLOYE')),
    CONSTRAINT uq_tentative_user UNIQUE (user_type, email_tente)
);

-- ============================================================
-- INDEX
-- ============================================================

CREATE INDEX idx_client_email                    ON client(email);
CREATE INDEX idx_client_fidelite                 ON client(id_type_fidelite);
CREATE INDEX idx_client_deleted                  ON client(deleted);
CREATE INDEX idx_client_demande_suppression      ON client(demande_suppression);
CREATE INDEX idx_client_email_verifie            ON client(email_verifie);
CREATE INDEX idx_client_token_verification_email ON client(token_verification_email);
CREATE INDEX idx_client_compte_active            ON client(compte_active);
CREATE INDEX idx_client_cree_par_employe         ON client(cree_par_employe);
CREATE INDEX idx_client_employe_createur         ON client(id_employe_createur);
CREATE INDEX idx_client_doit_definir_mdp         ON client(doit_definir_mot_de_passe);
CREATE INDEX idx_reset_password_token_client     ON reset_password_token(id_client);
CREATE INDEX idx_reset_password_token_expire_le  ON reset_password_token(expire_le);
CREATE INDEX idx_reset_password_token_utilise    ON reset_password_token(utilise);
CREATE INDEX idx_client_auth_provider_client     ON client_auth_provider(id_client);
CREATE INDEX idx_client_auth_provider_provider   ON client_auth_provider(id_provider_auth);
CREATE INDEX idx_favori_magasin_magasin          ON favori_magasin(id_magasin);
CREATE INDEX idx_favori_produit_produit          ON favori_produit(id_produit);
CREATE INDEX idx_employe_magasin                 ON employe(id_magasin);
CREATE INDEX idx_employe_role                    ON employe(id_role);
CREATE INDEX idx_employe_deleted                 ON employe(deleted);
CREATE INDEX idx_push_token_client               ON push_notification_token(id_client);
CREATE INDEX idx_push_token_actif                ON push_notification_token(actif);
CREATE INDEX idx_produit_categorie               ON produit(id_categorie);
CREATE INDEX idx_produit_nom                     ON produit(nom);
CREATE INDEX idx_produit_actif                   ON produit(actif, deleted);
CREATE INDEX idx_produit_niveau                  ON produit(niveau_acces_min);
CREATE INDEX idx_variant_produit                 ON produit_variant(id_produit);
CREATE INDEX idx_variant_edition                 ON produit_variant(id_edition);
CREATE INDEX idx_variant_plateforme              ON produit_variant(id_plateforme);
CREATE INDEX idx_variant_format                  ON produit_variant(id_format_produit);
CREATE INDEX idx_variant_statut                  ON produit_variant(id_statut_produit);
CREATE INDEX idx_variant_serie                   ON produit_variant(necessite_numero_serie);
CREATE INDEX idx_variant_demat                   ON produit_variant(est_demat);
CREATE INDEX idx_variant_tcg                     ON produit_variant(est_tcg_unitaire);
CREATE INDEX idx_variant_reprise                 ON produit_variant(est_reprise);
CREATE INDEX idx_prix_variant                    ON produit_prix(id_variant);
CREATE INDEX idx_prix_dates                      ON produit_prix(date_debut, date_fin);
CREATE INDEX idx_image_variant                   ON produit_image(id_variant);
CREATE INDEX idx_video_produit                   ON produit_video(id_produit);
CREATE INDEX idx_screenshot_produit              ON produit_screenshot(id_produit, ordre_affichage);
CREATE INDEX idx_tcg_extension_jeu               ON tcg_extension(id_tcg_jeu);
CREATE INDEX idx_tcg_reference_extension         ON tcg_carte_reference(id_tcg_extension);
CREATE INDEX idx_tcg_reference_nom               ON tcg_carte_reference(nom_carte);
CREATE INDEX idx_tcg_inventaire_reference        ON tcg_carte_inventaire(id_tcg_carte_reference);
CREATE INDEX idx_tcg_inventaire_variant          ON tcg_carte_inventaire(id_variant);
CREATE INDEX idx_tcg_inventaire_magasin          ON tcg_carte_inventaire(id_magasin);
CREATE INDEX idx_tcg_inventaire_disponible       ON tcg_carte_inventaire(disponible);
CREATE INDEX idx_panier_client                   ON panier(id_client);
CREATE INDEX idx_ligne_panier_variant            ON ligne_panier(id_variant);
CREATE INDEX idx_precommande_client              ON precommande(id_client);
CREATE INDEX idx_precommande_statut              ON precommande(id_statut_precommande);
CREATE INDEX idx_precommande_date                ON precommande(date_precommande);
CREATE INDEX idx_precommande_ligne_precommande   ON precommande_ligne(id_precommande);
CREATE INDEX idx_precommande_ligne_variant       ON precommande_ligne(id_variant);
CREATE INDEX idx_paiement_transaction_commande    ON paiement_transaction(id_commande);
CREATE INDEX idx_paiement_transaction_precommande ON paiement_transaction(id_precommande);
CREATE INDEX idx_paiement_transaction_statut      ON paiement_transaction(id_statut_paiement);
CREATE INDEX idx_commande_client                 ON commande(id_client);
CREATE INDEX idx_commande_statut                 ON commande(id_statut_commande);
CREATE INDEX idx_commande_date                   ON commande(date_commande);
CREATE INDEX idx_commande_magasin_retrait        ON commande(id_magasin_retrait);
CREATE INDEX idx_commande_entrepot_expedition    ON commande(id_entrepot_expedition);
CREATE INDEX idx_ligne_commande_commande         ON ligne_commande(id_commande);
CREATE INDEX idx_ligne_commande_variant          ON ligne_commande(id_variant);
CREATE INDEX idx_facture_client                  ON facture(id_client);
CREATE INDEX idx_facture_commande                ON facture(id_commande);
CREATE INDEX idx_facture_magasin                 ON facture(id_magasin);
CREATE INDEX idx_facture_employe                 ON facture(id_employe);
CREATE INDEX idx_facture_date                    ON facture(date_facture);
CREATE INDEX idx_facture_contexte                ON facture(id_contexte_vente);
CREATE INDEX idx_ligne_facture_facture           ON ligne_facture(id_facture);
CREATE INDEX idx_ligne_facture_variant           ON ligne_facture(id_variant);
CREATE INDEX idx_reprise_client                  ON reprise(id_client);
CREATE INDEX idx_reprise_employe                 ON reprise(id_employe);
CREATE INDEX idx_reprise_magasin                 ON reprise(id_magasin);
CREATE INDEX idx_reprise_statut                  ON reprise(id_statut_reprise);
CREATE INDEX idx_reprise_ligne_reprise           ON reprise_ligne(id_reprise);
CREATE INDEX idx_reprise_ligne_variant           ON reprise_ligne(id_variant);
CREATE INDEX idx_stock_magasin_magasin           ON stock_magasin(id_magasin);
CREATE INDEX idx_stock_magasin_variant           ON stock_magasin(id_variant);
CREATE INDEX idx_stock_entrepot_entrepot         ON stock_entrepot(id_entrepot);
CREATE INDEX idx_stock_entrepot_variant          ON stock_entrepot(id_variant);
CREATE INDEX idx_reservation_stock_variant       ON reservation_stock(id_variant);
CREATE INDEX idx_reservation_stock_magasin       ON reservation_stock(id_magasin);
CREATE INDEX idx_reservation_stock_entrepot      ON reservation_stock(id_entrepot);
CREATE INDEX idx_mouvement_stock_variant         ON mouvement_stock(id_variant);
CREATE INDEX idx_mouvement_stock_magasin         ON mouvement_stock(id_magasin);
CREATE INDEX idx_mouvement_stock_entrepot        ON mouvement_stock(id_entrepot);
CREATE INDEX idx_mouvement_stock_date            ON mouvement_stock(date_mouvement);
CREATE INDEX idx_avis_produit                    ON avis_produit(id_produit);
CREATE INDEX idx_avis_client                     ON avis_produit(id_client);
CREATE INDEX idx_avis_statut                     ON avis_produit(id_statut_avis);
CREATE INDEX idx_avis_note                       ON avis_produit(note);
CREATE INDEX idx_garantie_date_fin               ON garantie(date_fin);
CREATE INDEX idx_extension_garantie              ON extension_garantie(id_garantie);
CREATE INDEX idx_vente_unite_ligne_facture        ON vente_unite(id_ligne_facture);
CREATE INDEX idx_bibliotheque_client             ON bibliotheque_client(id_client);
CREATE INDEX idx_bibliotheque_variant            ON bibliotheque_client(id_variant);
CREATE INDEX idx_retour_facture                  ON retour_produit(id_facture);
CREATE INDEX idx_retour_client                   ON retour_produit(id_client);
CREATE INDEX idx_sav_vente_unite                 ON dossier_sav(id_vente_unite);
CREATE INDEX idx_sav_statut                      ON dossier_sav(id_statut_sav);
CREATE INDEX idx_historique_points_client        ON historique_points(id_client);
CREATE INDEX idx_historique_points_facture       ON historique_points(id_facture);
CREATE INDEX idx_connexion_log_user    ON connexion_log(user_type, user_id);
CREATE INDEX idx_connexion_log_email   ON connexion_log(email_tente);
CREATE INDEX idx_connexion_log_ip      ON connexion_log(ip);
CREATE INDEX idx_connexion_log_date    ON connexion_log(date_connexion);
CREATE INDEX idx_connexion_log_succes  ON connexion_log(succes);
CREATE INDEX idx_session_user          ON session_active(user_type, user_id);
CREATE INDEX idx_session_actif         ON session_active(actif);
CREATE INDEX idx_session_expiration    ON session_active(date_expiration);
CREATE INDEX idx_tentative_email       ON tentative_connexion_echec(email_tente);
CREATE INDEX idx_tentative_ip          ON tentative_connexion_echec(ip);
CREATE INDEX idx_tentative_bloque      ON tentative_connexion_echec(bloque_jusqu_au);
CREATE INDEX idx_promo_usage_promotion ON promotion_usage(id_promotion);
CREATE INDEX idx_promo_usage_client    ON promotion_usage(id_client);
CREATE INDEX idx_promo_usage_commande  ON promotion_usage(id_commande);
CREATE INDEX idx_promo_usage_date      ON promotion_usage(date_utilisation);
CREATE INDEX idx_taux_tva_actif        ON taux_tva(actif);
CREATE INDEX idx_ligne_facture_tva     ON ligne_facture(id_taux_tva);

-- ============================================================
-- VUES
-- ============================================================

CREATE VIEW v_client_actif AS
SELECT * FROM client WHERE deleted = FALSE AND actif = TRUE;

CREATE VIEW v_client_email_non_verifie AS
SELECT id_client, pseudo, email, date_creation, token_verification_expire_le
FROM client
WHERE deleted = FALSE AND actif = TRUE AND email_verifie = FALSE;

CREATE VIEW v_client_activation_en_attente AS
SELECT
    c.id_client,
    c.pseudo,
    c.nom,
    c.prenom,
    c.email,
    c.compte_active,
    c.email_verifie,
    c.cree_par_employe,
    c.id_employe_createur,
    c.doit_definir_mot_de_passe,
    c.token_verification_email,
    c.token_verification_expire_le
FROM client c
WHERE c.deleted = FALSE
  AND c.actif = TRUE
  AND (
      c.compte_active = FALSE
      OR c.doit_definir_mot_de_passe = TRUE
      OR c.email_verifie = FALSE
  );

CREATE VIEW v_reset_password_token_actif AS
SELECT
    rpt.id_reset_password_token,
    rpt.id_client,
    c.pseudo,
    c.email,
    rpt.token,
    rpt.expire_le,
    rpt.date_creation
FROM reset_password_token rpt
JOIN client c ON c.id_client = rpt.id_client
WHERE rpt.utilise = FALSE
  AND rpt.expire_le > NOW();

CREATE VIEW v_employe_actif AS
SELECT * FROM employe WHERE deleted = FALSE AND actif = TRUE;

CREATE VIEW v_produit_actif AS
SELECT * FROM produit WHERE deleted = FALSE AND actif = TRUE;

CREATE VIEW v_catalogue_public AS
SELECT
    p.id_produit,
    pv.id_variant,
    p.nom AS produit,
    pv.nom_commercial,
    p.slug,
    c.nom AS categorie,
    tc.code AS type_categorie,
    pl.libelle AS plateforme,
    fp.code AS format_produit,
    sp.code AS statut_commercial,
    p.pegi,
    p.niveau_acces_min,
    pv.est_demat,
    pv.est_tcg_unitaire,
    pv.scelle,
    pv.necessite_numero_serie,
    img.url AS image_principale_url,
    img.alt AS image_principale_alt
FROM produit p
JOIN categorie c        ON c.id_categorie      = p.id_categorie
JOIN type_categorie tc  ON tc.id_type_categorie = c.id_type_categorie
JOIN produit_variant pv ON pv.id_produit        = p.id_produit
LEFT JOIN plateforme pl ON pl.id_plateforme     = pv.id_plateforme
JOIN format_produit fp  ON fp.id_format_produit = pv.id_format_produit
JOIN statut_produit sp  ON sp.id_statut_produit = pv.id_statut_produit
LEFT JOIN produit_image img ON img.id_variant   = pv.id_variant AND img.principale = TRUE
WHERE p.deleted = FALSE AND p.actif = TRUE AND pv.actif = TRUE;

CREATE VIEW v_prix_actuel AS
SELECT
    pp.id_prix,
    pp.id_variant,
    pp.prix_neuf,
    pp.prix_occasion,
    pp.prix_reprise,
    pp.prix_location,
    pp.date_debut,
    pp.date_fin
FROM produit_prix pp
WHERE pp.actif = TRUE
  AND pp.date_debut <= NOW()
  AND (pp.date_fin IS NULL OR pp.date_fin > NOW());

CREATE VIEW v_stock_disponible AS
SELECT
    sm.id_stock_magasin,
    m.nom AS magasin,
    pv.sku,
    pv.nom_commercial,
    sm.quantite_neuf,
    sm.quantite_occasion,
    sm.quantite_reprise,
    sm.quantite_reservee,
    sm.quantite_disponible
FROM stock_magasin sm
JOIN magasin m          ON m.id_magasin = sm.id_magasin
JOIN produit_variant pv ON pv.id_variant = sm.id_variant;

CREATE VIEW v_stock_entrepot_disponible AS
SELECT
    se.id_stock_entrepot,
    e.nom       AS entrepot,
    e.code      AS code_entrepot,
    pv.sku,
    pv.nom_commercial,
    se.quantite_neuf,
    se.quantite_reservee,
    se.quantite_disponible
FROM stock_entrepot se
JOIN entrepot e         ON e.id_entrepot = se.id_entrepot
JOIN produit_variant pv ON pv.id_variant  = se.id_variant;

CREATE VIEW v_commandes_expedition AS
SELECT
    co.id_commande,
    co.reference_commande,
    co.date_commande,
    co.date_expedition,
    sc.code  AS statut_commande,
    cv.code  AS canal_vente,
    ml.code  AS mode_livraison,
    c.pseudo AS client,
    CASE
        WHEN co.id_entrepot_expedition IS NOT NULL THEN 'ENTREPOT'
        WHEN co.id_magasin_retrait     IS NOT NULL THEN 'MAGASIN'
        ELSE 'NON_DEFINI'
    END AS type_source_expedition,
    e.nom AS entrepot_expedition,
    m.nom AS magasin_retrait
FROM commande co
JOIN client c           ON c.id_client          = co.id_client
JOIN statut_commande sc ON sc.id_statut_commande = co.id_statut_commande
JOIN canal_vente cv     ON cv.id_canal_vente     = co.id_canal_vente
JOIN mode_livraison ml  ON ml.id_mode_livraison  = co.id_mode_livraison
LEFT JOIN entrepot e    ON e.id_entrepot         = co.id_entrepot_expedition
LEFT JOIN magasin m     ON m.id_magasin          = co.id_magasin_retrait;

CREATE VIEW v_avis_publics AS
SELECT
    a.id_avis,
    a.note,
    a.commentaire,
    a.date_creation,
    c.pseudo AS auteur,
    p.id_produit,
    p.nom AS produit
FROM avis_produit a
JOIN client c       ON c.id_client       = a.id_client
JOIN produit p      ON p.id_produit      = a.id_produit
JOIN statut_avis sa ON sa.id_statut_avis = a.id_statut_avis
WHERE sa.code = 'APPROUVE'
  AND c.deleted = FALSE
  AND p.deleted = FALSE;

CREATE VIEW v_note_moyenne_produit AS
SELECT
    p.id_produit,
    p.nom AS produit,
    COUNT(a.id_avis) AS nb_avis,
    ROUND(AVG(a.note), 1) AS note_moyenne
FROM produit p
LEFT JOIN avis_produit a ON a.id_produit = p.id_produit
LEFT JOIN statut_avis sa ON sa.id_statut_avis = a.id_statut_avis
WHERE p.deleted = FALSE
  AND (a.id_avis IS NULL OR sa.code = 'APPROUVE')
GROUP BY p.id_produit, p.nom;

CREATE VIEW v_facture_detail AS
SELECT
    f.id_facture,
    f.reference_facture,
    f.date_facture,
    f.montant_total,
    f.montant_remise,
    f.montant_final,
    cv_ctx.code AS contexte_vente,
    COALESCE(c.pseudo, f.nom_client)          AS nom_client_affiche,
    COALESCE(c.email, f.email_client)         AS email_client_affiche,
    COALESCE(c.telephone, f.telephone_client) AS telephone_client_affiche,
    m.nom AS magasin,
    mp.code AS mode_paiement,
    sf.code AS statut_facture,
    CONCAT(e.prenom, ' ', e.nom) AS vendeur
FROM facture f
LEFT JOIN client c         ON c.id_client            = f.id_client
JOIN magasin m             ON m.id_magasin            = f.id_magasin
JOIN mode_paiement mp      ON mp.id_mode_paiement     = f.id_mode_paiement
JOIN statut_facture sf     ON sf.id_statut_facture    = f.id_statut_facture
JOIN contexte_vente cv_ctx ON cv_ctx.id_contexte_vente = f.id_contexte_vente
LEFT JOIN employe e        ON e.id_employe            = f.id_employe;

CREATE VIEW v_favoris_client AS
SELECT
    c.id_client,
    c.pseudo,
    fm.id_magasin,
    m.nom AS magasin_favori,
    fm.principal,
    fp.id_produit,
    p.nom AS produit_favori
FROM client c
LEFT JOIN favori_magasin fm ON fm.id_client  = c.id_client
LEFT JOIN magasin m         ON m.id_magasin  = fm.id_magasin
LEFT JOIN favori_produit fp ON fp.id_client  = c.id_client
LEFT JOIN produit p         ON p.id_produit  = fp.id_produit;

CREATE VIEW v_auth_client AS
SELECT
    c.id_client,
    c.pseudo,
    c.email,
    pa.code AS provider_auth,
    cap.provider_user_id,
    cap.email_provider,
    cap.actif
FROM client c
JOIN client_auth_provider cap ON cap.id_client       = c.id_client
JOIN provider_auth pa         ON pa.id_provider_auth = cap.id_provider_auth;

CREATE VIEW v_precommandes_detail AS
SELECT
    pr.id_precommande,
    pr.reference_precommande,
    c.pseudo,
    sp.code AS statut_precommande,
    cv.code AS canal_vente,
    mp.code AS mode_paiement,
    pr.acompte_paye,
    pr.montant_total_estime,
    pr.date_precommande,
    pr.date_disponibilite_estimee
FROM precommande pr
JOIN client c              ON c.id_client              = pr.id_client
JOIN statut_precommande sp ON sp.id_statut_precommande = pr.id_statut_precommande
JOIN canal_vente cv        ON cv.id_canal_vente         = pr.id_canal_vente
LEFT JOIN mode_paiement mp ON mp.id_mode_paiement       = pr.id_mode_paiement;

CREATE VIEW v_paiements_ligne AS
SELECT
    pt.id_paiement_transaction,
    pt.id_commande,
    pt.id_precommande,
    mp.code AS mode_paiement,
    sp.code AS statut_paiement,
    pt.provider_reference,
    pt.montant,
    pt.devise,
    pt.date_creation,
    pt.date_confirmation
FROM paiement_transaction pt
JOIN mode_paiement mp   ON mp.id_mode_paiement   = pt.id_mode_paiement
JOIN statut_paiement sp ON sp.id_statut_paiement = pt.id_statut_paiement;

CREATE VIEW v_commandes_detail AS
SELECT
    co.id_commande,
    co.reference_commande,
    co.date_commande,
    c.id_client,
    c.pseudo,
    sc.code AS statut_commande,
    ml.code AS mode_livraison,
    cv.code AS canal_vente,
    co.sous_total,
    co.montant_remise,
    co.frais_livraison,
    co.montant_total
FROM commande co
JOIN client c           ON c.id_client          = co.id_client
JOIN statut_commande sc ON sc.id_statut_commande = co.id_statut_commande
JOIN mode_livraison ml  ON ml.id_mode_livraison  = co.id_mode_livraison
JOIN canal_vente cv     ON cv.id_canal_vente     = co.id_canal_vente;

CREATE VIEW v_garanties_actives AS
SELECT
    g.id_garantie,
    vu.numero_serie,
    g.date_debut,
    g.date_fin,
    g.est_etendue,
    p.nom AS produit,
    pv.nom_commercial,
    f.id_facture,
    f.reference_facture,
    COALESCE(c.pseudo, f.nom_client)  AS client,
    COALESCE(c.email, f.email_client) AS client_email
FROM garantie g
JOIN vente_unite vu     ON vu.id_vente_unite    = g.id_vente_unite
JOIN ligne_facture lf   ON lf.id_ligne_facture  = vu.id_ligne_facture
JOIN produit_variant pv ON pv.id_variant        = lf.id_variant
JOIN produit p          ON p.id_produit         = pv.id_produit
JOIN facture f          ON f.id_facture         = lf.id_facture
JOIN client c           ON c.id_client          = f.id_client
WHERE g.date_fin >= CURDATE();

CREATE VIEW v_bibliotheque_client AS
SELECT
    bc.id_bibliotheque,
    bc.id_client,
    c.pseudo,
    pv.id_variant,
    pv.nom_commercial,
    p.nom AS produit,
    pl.libelle AS plateforme,
    bc.date_attribution,
    cp.cle_activation
FROM bibliotheque_client bc
JOIN client c           ON c.id_client       = bc.id_client
JOIN produit_variant pv ON pv.id_variant     = bc.id_variant
JOIN produit p          ON p.id_produit      = pv.id_produit
LEFT JOIN plateforme pl ON pl.id_plateforme  = pv.id_plateforme
LEFT JOIN cle_produit cp ON cp.id_cle_produit = bc.id_cle_produit;

CREATE VIEW v_reprises_detail AS
SELECT
    r.id_reprise,
    r.reference_reprise,
    r.date_creation,
    sr.code AS statut_reprise,
    m.nom AS magasin,
    CONCAT(e.prenom, ' ', e.nom) AS employe,
    c.pseudo,
    r.montant_total_estime,
    r.montant_total_valide
FROM reprise r
LEFT JOIN client c     ON c.id_client          = r.id_client
JOIN employe e         ON e.id_employe         = r.id_employe
JOIN magasin m         ON m.id_magasin         = r.id_magasin
JOIN statut_reprise sr ON sr.id_statut_reprise = r.id_statut_reprise;

CREATE VIEW v_tcg_inventaire_disponible AS
SELECT
    tci.id_tcg_carte_inventaire,
    tj.nom AS jeu_tcg,
    te.nom AS extension_tcg,
    tcr.nom_carte,
    tcr.numero_carte,
    tcr.rarete,
    ect.libelle AS etat_carte,
    tci.langue,
    tci.foil,
    tci.reverse_foil,
    tci.alternate_art,
    tci.prix_vente,
    m.nom AS magasin
FROM tcg_carte_inventaire tci
JOIN tcg_carte_reference tcr ON tcr.id_tcg_carte_reference = tci.id_tcg_carte_reference
JOIN tcg_extension te        ON te.id_tcg_extension        = tcr.id_tcg_extension
JOIN tcg_jeu tj              ON tj.id_tcg_jeu              = te.id_tcg_jeu
JOIN etat_carte_tcg ect      ON ect.id_etat_carte_tcg      = tci.id_etat_carte_tcg
JOIN magasin m               ON m.id_magasin               = tci.id_magasin
WHERE tci.disponible = TRUE;

-- ============================================================
-- TRIGGERS
-- ============================================================

DELIMITER $$

CREATE TRIGGER trg_client_email_verification_init
BEFORE INSERT ON client
FOR EACH ROW
BEGIN
    IF NEW.token_verification_email IS NULL OR NEW.token_verification_email = '' THEN
        SET NEW.token_verification_email = UUID();
    END IF;
    IF NEW.token_verification_expire_le IS NULL THEN
        SET NEW.token_verification_expire_le = DATE_ADD(NOW(), INTERVAL 24 HOUR);
    END IF;
    IF NEW.cree_par_employe IS NULL THEN
        SET NEW.cree_par_employe = FALSE;
    END IF;
    IF NEW.compte_active IS NULL THEN
        SET NEW.compte_active = FALSE;
    END IF;
    IF NEW.doit_definir_mot_de_passe IS NULL THEN
        SET NEW.doit_definir_mot_de_passe = TRUE;
    END IF;
    IF NEW.email_verifie IS NULL THEN
        SET NEW.email_verifie = FALSE;
    END IF;
    IF NEW.email_verifie = FALSE THEN
        SET NEW.date_verification_email = NULL;
    END IF;
END$$

CREATE TRIGGER trg_client_points_init
AFTER INSERT ON client
FOR EACH ROW
BEGIN
    INSERT INTO points_fidelite (id_client, solde_points, total_achats_annuel, date_debut_periode)
    VALUES (NEW.id_client, 0, 0.00, CURDATE());
END$$

CREATE TRIGGER trg_avis_achat_verifie
BEFORE INSERT ON avis_produit
FOR EACH ROW
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM ligne_facture lf
        JOIN facture f          ON f.id_facture  = lf.id_facture
        JOIN produit_variant pv ON pv.id_variant = lf.id_variant
        WHERE f.id_client   = NEW.id_client
          AND pv.id_produit = NEW.id_produit
    ) THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Le client doit avoir achete le produit avant de laisser un avis';
    END IF;
END$$

CREATE TRIGGER trg_avis_default_statut
BEFORE INSERT ON avis_produit
FOR EACH ROW
BEGIN
    IF NEW.id_statut_avis IS NULL OR NEW.id_statut_avis = 0 THEN
        SET NEW.id_statut_avis = (SELECT id_statut_avis FROM statut_avis WHERE code = 'EN_ATTENTE' LIMIT 1);
    END IF;
END$$

CREATE TRIGGER trg_variant_numero_serie_auto_insert
BEFORE INSERT ON produit_variant
FOR EACH ROW
BEGIN
    DECLARE v_code_type VARCHAR(50);
    SELECT tc.code INTO v_code_type
    FROM produit p
    JOIN categorie c       ON c.id_categorie      = p.id_categorie
    JOIN type_categorie tc ON tc.id_type_categorie = c.id_type_categorie
    WHERE p.id_produit = NEW.id_produit;

    IF v_code_type IN ('CONSOLE', 'ACCESSOIRE') AND NEW.est_demat = FALSE THEN
        SET NEW.necessite_numero_serie = TRUE;
    END IF;
END$$

CREATE TRIGGER trg_variant_numero_serie_auto_update
BEFORE UPDATE ON produit_variant
FOR EACH ROW
BEGIN
    DECLARE v_code_type VARCHAR(50);
    SELECT tc.code INTO v_code_type
    FROM produit p
    JOIN categorie c       ON c.id_categorie      = p.id_categorie
    JOIN type_categorie tc ON tc.id_type_categorie = c.id_type_categorie
    WHERE p.id_produit = NEW.id_produit;

    IF v_code_type IN ('CONSOLE', 'ACCESSOIRE') AND NEW.est_demat = FALSE THEN
        SET NEW.necessite_numero_serie = TRUE;
    END IF;
END$$

CREATE TRIGGER trg_precommande_ligne_calc_insert
BEFORE INSERT ON precommande_ligne
FOR EACH ROW
BEGIN
    SET NEW.montant_ligne_estime = NEW.quantite * NEW.prix_unitaire_estime;
END$$

CREATE TRIGGER trg_ligne_commande_calc_insert
BEFORE INSERT ON ligne_commande
FOR EACH ROW
BEGIN
    SET NEW.montant_ligne = NEW.quantite * NEW.prix_unitaire;
END$$

CREATE TRIGGER trg_ligne_facture_calc_insert
BEFORE INSERT ON ligne_facture
FOR EACH ROW
BEGIN
    DECLARE v_taux DECIMAL(5,2) DEFAULT 20.00;

    SELECT COALESCE(
        (SELECT t.taux FROM taux_tva t
         JOIN produit_variant pv ON pv.id_taux_tva = t.id_taux_tva
         WHERE pv.id_variant = NEW.id_variant AND t.actif = TRUE),
        (SELECT t.taux FROM taux_tva t
         JOIN type_categorie tc ON tc.id_taux_tva_defaut = t.id_taux_tva
         JOIN categorie c       ON c.id_type_categorie   = tc.id_type_categorie
         JOIN produit p         ON p.id_categorie        = c.id_categorie
         JOIN produit_variant pv ON pv.id_produit        = p.id_produit
         WHERE pv.id_variant = NEW.id_variant AND t.actif = TRUE LIMIT 1),
        20.00
    ) INTO v_taux;

    SET NEW.taux_tva_applique = v_taux;
    SET NEW.montant_ligne     = NEW.quantite * NEW.prix_unitaire;
    SET NEW.montant_ht_ligne  = ROUND(NEW.montant_ligne / (1 + v_taux / 100), 2);
    SET NEW.montant_tva_ligne = NEW.montant_ligne - NEW.montant_ht_ligne;

    IF NEW.id_taux_tva IS NULL THEN
        SET NEW.id_taux_tva = (SELECT id_taux_tva FROM taux_tva WHERE taux = v_taux AND actif = TRUE LIMIT 1);
    END IF;
END$$

CREATE TRIGGER trg_facture_total_after_insert
AFTER INSERT ON ligne_facture
FOR EACH ROW
BEGIN
    UPDATE facture
    SET montant_total     = (SELECT IFNULL(SUM(montant_ligne + IFNULL(garantie_prix,0)),0) FROM ligne_facture WHERE id_facture = NEW.id_facture),
        montant_ht_total  = (SELECT IFNULL(SUM(montant_ht_ligne), 0)                       FROM ligne_facture WHERE id_facture = NEW.id_facture),
        montant_tva_total = (SELECT IFNULL(SUM(montant_tva_ligne),0)                       FROM ligne_facture WHERE id_facture = NEW.id_facture),
        montant_final     = GREATEST(0,(SELECT IFNULL(SUM(montant_ligne + IFNULL(garantie_prix,0)),0) FROM ligne_facture WHERE id_facture = NEW.id_facture) - montant_remise)
    WHERE id_facture = NEW.id_facture;
END$$

CREATE TRIGGER trg_facture_total_after_update
AFTER UPDATE ON ligne_facture
FOR EACH ROW
BEGIN
    UPDATE facture
    SET montant_total     = (SELECT IFNULL(SUM(montant_ligne + IFNULL(garantie_prix,0)),0) FROM ligne_facture WHERE id_facture = NEW.id_facture),
        montant_ht_total  = (SELECT IFNULL(SUM(montant_ht_ligne), 0)                       FROM ligne_facture WHERE id_facture = NEW.id_facture),
        montant_tva_total = (SELECT IFNULL(SUM(montant_tva_ligne),0)                       FROM ligne_facture WHERE id_facture = NEW.id_facture),
        montant_final     = GREATEST(0,(SELECT IFNULL(SUM(montant_ligne + IFNULL(garantie_prix,0)),0) FROM ligne_facture WHERE id_facture = NEW.id_facture) - montant_remise)
    WHERE id_facture = NEW.id_facture;
END$$

CREATE TRIGGER trg_facture_total_after_delete
AFTER DELETE ON ligne_facture
FOR EACH ROW
BEGIN
    UPDATE facture
    SET montant_total     = (SELECT IFNULL(SUM(montant_ligne + IFNULL(garantie_prix,0)),0) FROM ligne_facture WHERE id_facture = OLD.id_facture),
        montant_ht_total  = (SELECT IFNULL(SUM(montant_ht_ligne), 0)                       FROM ligne_facture WHERE id_facture = OLD.id_facture),
        montant_tva_total = (SELECT IFNULL(SUM(montant_tva_ligne),0)                       FROM ligne_facture WHERE id_facture = OLD.id_facture),
        montant_final     = GREATEST(0,(SELECT IFNULL(SUM(montant_ligne + IFNULL(garantie_prix,0)),0) FROM ligne_facture WHERE id_facture = OLD.id_facture) - montant_remise)
    WHERE id_facture = OLD.id_facture;
END$$

CREATE TRIGGER trg_stock_disponible_before_insert
BEFORE INSERT ON stock_magasin
FOR EACH ROW
BEGIN
    SET NEW.quantite_disponible = NEW.quantite_neuf + NEW.quantite_occasion + NEW.quantite_reprise - NEW.quantite_reservee;
    IF NEW.quantite_disponible < 0 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Le stock disponible ne peut pas etre negatif';
    END IF;
END$$

CREATE TRIGGER trg_stock_disponible_before_update
BEFORE UPDATE ON stock_magasin
FOR EACH ROW
BEGIN
    SET NEW.quantite_disponible = NEW.quantite_neuf + NEW.quantite_occasion + NEW.quantite_reprise - NEW.quantite_reservee;
    IF NEW.quantite_disponible < 0 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Le stock disponible ne peut pas etre negatif';
    END IF;
END$$

CREATE TRIGGER trg_stock_entrepot_disponible_before_insert
BEFORE INSERT ON stock_entrepot
FOR EACH ROW
BEGIN
    SET NEW.quantite_disponible = NEW.quantite_neuf - NEW.quantite_reservee;
    IF NEW.quantite_disponible < 0 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Le stock entrepot disponible ne peut pas etre negatif';
    END IF;
END$$

CREATE TRIGGER trg_stock_entrepot_disponible_before_update
BEFORE UPDATE ON stock_entrepot
FOR EACH ROW
BEGIN
    SET NEW.quantite_disponible = NEW.quantite_neuf - NEW.quantite_reservee;
    IF NEW.quantite_disponible < 0 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Le stock entrepot disponible ne peut pas etre negatif';
    END IF;
END$$

CREATE TRIGGER trg_extension_garantie_update
AFTER INSERT ON extension_garantie
FOR EACH ROW
BEGIN
    UPDATE garantie
    SET date_fin       = NEW.date_fin_etendue,
        est_etendue    = TRUE,
        date_extension = DATE(NEW.date_achat)
    WHERE id_garantie = NEW.id_garantie;
END$$

CREATE TRIGGER trg_audit_client_update
AFTER UPDATE ON client
FOR EACH ROW
BEGIN
    INSERT INTO audit_log (table_name, operation_type, record_id, donnees_avant, donnees_apres)
    VALUES (
        'client', 'UPDATE', OLD.id_client,
        JSON_OBJECT('email', OLD.email, 'pseudo', OLD.pseudo, 'niveau', OLD.id_type_fidelite,
                    'rgpd_consent', OLD.rgpd_consent, 'deleted', OLD.deleted, 'actif', OLD.actif),
        JSON_OBJECT('email', NEW.email, 'pseudo', NEW.pseudo, 'niveau', NEW.id_type_fidelite,
                    'rgpd_consent', NEW.rgpd_consent, 'deleted', NEW.deleted, 'actif', NEW.actif)
    );
END$$

DELIMITER ;

-- ============================================================
-- PROCEDURES STOCKEES
-- ============================================================

DELIMITER $$

CREATE PROCEDURE sp_verifier_acces_produit (
    IN  p_id_client  BIGINT,
    IN  p_id_produit BIGINT,
    OUT p_acces      TINYINT
)
BEGIN
    DECLARE v_niveau_client  VARCHAR(20);
    DECLARE v_niveau_produit VARCHAR(20);
    DECLARE v_abo_actif      INT DEFAULT 0;

    SELECT tf.code INTO v_niveau_client
    FROM client c
    JOIN type_fidelite tf ON tf.id_type_fidelite = c.id_type_fidelite
    WHERE c.id_client = p_id_client;

    SELECT niveau_acces_min INTO v_niveau_produit
    FROM produit WHERE id_produit = p_id_produit;

    IF v_niveau_client = 'ULTIMATE' THEN
        SELECT COUNT(*) INTO v_abo_actif
        FROM abonnement_client a
        JOIN statut_abonnement sa ON sa.id_statut_abonnement = a.id_statut_abonnement
        WHERE a.id_client = p_id_client AND sa.code = 'ACTIF' AND a.date_fin >= CURDATE();
    END IF;

    SET p_acces = CASE
        WHEN v_niveau_produit = 'NORMAL' THEN 1
        WHEN v_niveau_produit = 'PREMIUM'
             AND v_niveau_client IN ('PREMIUM', 'ULTIMATE')
             AND (v_niveau_client <> 'ULTIMATE' OR v_abo_actif > 0) THEN 1
        WHEN v_niveau_produit = 'ULTIMATE'
             AND v_niveau_client = 'ULTIMATE'
             AND v_abo_actif > 0 THEN 1
        ELSE 0
    END;
END$$

CREATE PROCEDURE sp_enregistrer_garantie (
    IN p_id_vente_unite   BIGINT,
    IN p_id_type_garantie BIGINT
)
BEGIN
    DECLARE v_date_debut DATE;
    DECLARE v_date_fin   DATE;
    DECLARE v_duree_mois INT;

    SET v_date_debut = CURDATE();
    SELECT duree_mois INTO v_duree_mois FROM type_garantie WHERE id_type_garantie = p_id_type_garantie;
    SET v_date_fin = DATE_ADD(v_date_debut, INTERVAL v_duree_mois MONTH);

    INSERT INTO garantie (id_vente_unite, id_type_garantie, date_debut, date_fin)
    VALUES (p_id_vente_unite, p_id_type_garantie, v_date_debut, v_date_fin);
END$$

CREATE PROCEDURE sp_etendre_garantie (
    IN p_id_garantie      BIGINT,
    IN p_id_type_garantie BIGINT
)
BEGIN
    DECLARE v_duree_mois       INT;
    DECLARE v_date_fin_etendue DATE;

    SELECT duree_mois INTO v_duree_mois FROM type_garantie WHERE id_type_garantie = p_id_type_garantie;
    SELECT DATE_ADD(date_fin, INTERVAL v_duree_mois MONTH) INTO v_date_fin_etendue
    FROM garantie WHERE id_garantie = p_id_garantie;

    INSERT INTO extension_garantie (id_garantie, id_type_garantie, date_fin_etendue)
    VALUES (p_id_garantie, p_id_type_garantie, v_date_fin_etendue);
END$$

CREATE PROCEDURE sp_verifier_garantie (
    IN  p_numero_serie VARCHAR(100),
    OUT p_valide       BOOLEAN,
    OUT p_message      VARCHAR(255)
)
BEGIN
    DECLARE v_count INT DEFAULT 0;

    SELECT COUNT(*) INTO v_count
    FROM garantie g
    JOIN vente_unite vu ON vu.id_vente_unite = g.id_vente_unite
    WHERE vu.numero_serie = p_numero_serie AND g.date_fin >= CURDATE();

    IF v_count > 0 THEN
        SET p_valide  = TRUE;
        SET p_message = 'Numero de serie valide. Garantie active.';
    ELSE
        SET p_valide  = FALSE;
        SET p_message = 'Numero de serie invalide ou garantie expiree.';
    END IF;
END$$

DELIMITER ;

-- ============================================================
-- PROCEDURES STOCKEES V16 : CONNEXION & PROMOTIONS
-- ============================================================

DELIMITER $$

CREATE PROCEDURE sp_enregistrer_connexion (
    IN p_user_type    VARCHAR(10),
    IN p_user_id      BIGINT,
    IN p_email_tente  VARCHAR(150),
    IN p_ip           VARCHAR(45),
    IN p_user_agent   VARCHAR(512),
    IN p_succes       BOOLEAN,
    IN p_motif_echec  VARCHAR(100),
    IN p_provider     VARCHAR(50)
)
BEGIN
    DECLARE v_nb_tentatives INT DEFAULT 0;

    INSERT INTO connexion_log (user_type, user_id, email_tente, ip, user_agent, succes, motif_echec, provider_auth)
    VALUES (p_user_type, p_user_id, p_email_tente, p_ip, p_user_agent, p_succes, p_motif_echec, p_provider);

    IF p_succes = TRUE THEN
        DELETE FROM tentative_connexion_echec
        WHERE user_type = p_user_type AND email_tente = p_email_tente;
    ELSE
        INSERT INTO tentative_connexion_echec (user_type, user_id, email_tente, ip, nb_tentatives)
        VALUES (p_user_type, p_user_id, p_email_tente, p_ip, 1)
        ON DUPLICATE KEY UPDATE
            nb_tentatives      = nb_tentatives + 1,
            derniere_tentative = NOW(),
            user_id            = COALESCE(user_id, p_user_id);

        SELECT nb_tentatives INTO v_nb_tentatives
        FROM tentative_connexion_echec
        WHERE user_type = p_user_type AND email_tente = p_email_tente;

        IF v_nb_tentatives >= 20 THEN
            UPDATE tentative_connexion_echec
            SET bloque_jusqu_au = DATE_ADD(NOW(), INTERVAL 24 HOUR)
            WHERE user_type = p_user_type AND email_tente = p_email_tente;
        ELSEIF v_nb_tentatives >= 10 THEN
            UPDATE tentative_connexion_echec
            SET bloque_jusqu_au = DATE_ADD(NOW(), INTERVAL 1 HOUR)
            WHERE user_type = p_user_type AND email_tente = p_email_tente;
        ELSEIF v_nb_tentatives >= 5 THEN
            UPDATE tentative_connexion_echec
            SET bloque_jusqu_au = DATE_ADD(NOW(), INTERVAL 15 MINUTE)
            WHERE user_type = p_user_type AND email_tente = p_email_tente;
        END IF;
    END IF;
END$$

CREATE PROCEDURE sp_verifier_blocage_connexion (
    IN  p_user_type       VARCHAR(10),
    IN  p_email           VARCHAR(150),
    OUT p_bloque          BOOLEAN,
    OUT p_bloque_jusqu_au DATETIME
)
BEGIN
    SET p_bloque          = FALSE;
    SET p_bloque_jusqu_au = NULL;

    SELECT bloque_jusqu_au INTO p_bloque_jusqu_au
    FROM tentative_connexion_echec
    WHERE user_type = p_user_type AND email_tente = p_email
      AND bloque_jusqu_au > NOW()
    LIMIT 1;

    IF p_bloque_jusqu_au IS NOT NULL THEN
        SET p_bloque = TRUE;
    END IF;
END$$

CREATE PROCEDURE sp_valider_code_promo (
    IN  p_code_promo        VARCHAR(50),
    IN  p_id_client         BIGINT,
    IN  p_montant_commande  DECIMAL(10,2),
    OUT p_valide            BOOLEAN,
    OUT p_montant_remise    DECIMAL(10,2),
    OUT p_message           VARCHAR(255)
)
sp_valider_code_promo: BEGIN
    DECLARE v_id_promotion        BIGINT;
    DECLARE v_type_reduction      VARCHAR(50);
    DECLARE v_valeur              DECIMAL(10,2);
    DECLARE v_date_debut          DATETIME;
    DECLARE v_date_fin            DATETIME;
    DECLARE v_actif               BOOLEAN;
    DECLARE v_nb_max              INT;
    DECLARE v_nb_max_client       INT;
    DECLARE v_nb_actuel           INT;
    DECLARE v_montant_min         DECIMAL(10,2);
    DECLARE v_nb_client_utilise   INT DEFAULT 0;

    SET p_valide         = FALSE;
    SET p_montant_remise = 0.00;
    SET p_message        = '';

    SELECT p.id_promotion, tr.code, p.valeur, p.date_debut, p.date_fin, p.actif,
           p.nb_utilisations_max, p.nb_utilisations_max_client, p.nb_utilisations_actuel,
           p.montant_minimum_commande
    INTO v_id_promotion, v_type_reduction, v_valeur, v_date_debut, v_date_fin, v_actif,
         v_nb_max, v_nb_max_client, v_nb_actuel, v_montant_min
    FROM promotion p
    JOIN type_reduction tr ON tr.id_type_reduction = p.id_type_reduction
    WHERE p.code_promo = p_code_promo
    LIMIT 1;

    IF v_id_promotion IS NULL THEN
        SET p_message = 'Code promo invalide.';
        LEAVE sp_valider_code_promo;
    END IF;

    IF v_actif = FALSE THEN
        SET p_message = 'Ce code promo est désactivé.';
        LEAVE sp_valider_code_promo;
    END IF;

    IF NOW() < v_date_debut OR NOW() > v_date_fin THEN
        SET p_message = 'Ce code promo n\'est pas valide à cette date.';
        LEAVE sp_valider_code_promo;
    END IF;

    IF v_montant_min IS NOT NULL AND p_montant_commande < v_montant_min THEN
        SET p_message = CONCAT('Montant minimum de commande requis : ', v_montant_min, ' €.');
        LEAVE sp_valider_code_promo;
    END IF;

    IF v_nb_max IS NOT NULL AND v_nb_actuel >= v_nb_max THEN
        SET p_message = 'Ce code promo a atteint sa limite d\'utilisation.';
        LEAVE sp_valider_code_promo;
    END IF;

    IF v_nb_max_client IS NOT NULL AND p_id_client IS NOT NULL THEN
        SELECT COUNT(*) INTO v_nb_client_utilise
        FROM promotion_usage
        WHERE id_promotion = v_id_promotion AND id_client = p_id_client;

        IF v_nb_client_utilise >= v_nb_max_client THEN
            SET p_message = 'Vous avez déjà utilisé ce code promo le nombre de fois autorisé.';
            LEAVE sp_valider_code_promo;
        END IF;
    END IF;

    IF v_type_reduction = 'POURCENTAGE' THEN
        SET p_montant_remise = ROUND(p_montant_commande * v_valeur / 100, 2);
    ELSEIF v_type_reduction = 'MONTANT_FIXE' THEN
        SET p_montant_remise = LEAST(v_valeur, p_montant_commande);
    END IF;

    SET p_valide  = TRUE;
    SET p_message = CONCAT('Code promo valide. Remise appliquée : ', p_montant_remise, ' €.');
END$$

CREATE PROCEDURE sp_appliquer_code_promo (
    IN  p_code_promo       VARCHAR(50),
    IN  p_id_client        BIGINT,
    IN  p_id_commande      BIGINT,
    IN  p_montant_commande DECIMAL(10,2),
    OUT p_succes           BOOLEAN,
    OUT p_montant_remise   DECIMAL(10,2),
    OUT p_message          VARCHAR(255)
)
sp_appliquer_code_promo: BEGIN
    DECLARE v_id_promotion BIGINT;
    DECLARE v_valide       BOOLEAN;

    SET p_succes         = FALSE;
    SET p_montant_remise = 0.00;

    CALL sp_valider_code_promo(p_code_promo, p_id_client, p_montant_commande, v_valide, p_montant_remise, p_message);

    IF v_valide = FALSE THEN
        LEAVE sp_appliquer_code_promo;
    END IF;

    SELECT id_promotion INTO v_id_promotion FROM promotion WHERE code_promo = p_code_promo LIMIT 1;

    INSERT INTO promotion_usage (id_promotion, id_client, id_commande, montant_commande_ht, montant_remise)
    VALUES (v_id_promotion, p_id_client, p_id_commande, p_montant_commande, p_montant_remise);

    UPDATE promotion SET nb_utilisations_actuel = nb_utilisations_actuel + 1
    WHERE id_promotion = v_id_promotion;

    UPDATE commande
    SET montant_remise = p_montant_remise,
        montant_total  = sous_total + frais_livraison - p_montant_remise,
        code_promo     = p_code_promo
    WHERE id_commande = p_id_commande;

    SET p_succes = TRUE;
END$$

DELIMITER ;

-- ============================================================
-- DONNEES DE REFERENCE EXTERNALISEES
-- Executer le fichier de donnees separe apres ce schema.
-- ============================================================

-- ============================================================
-- VUES V16 : TVA, PROMOTIONS, SECURITE
-- ============================================================

CREATE OR REPLACE VIEW v_facture_tva_detail AS
SELECT
    f.id_facture,
    f.reference_facture,
    f.date_facture,
    tv.code        AS code_tva,
    tv.taux        AS taux_tva,
    SUM(lf.montant_ht_ligne)  AS total_ht,
    SUM(lf.montant_tva_ligne) AS total_tva,
    SUM(lf.montant_ligne)     AS total_ttc
FROM facture f
JOIN ligne_facture lf ON lf.id_facture  = f.id_facture
JOIN taux_tva tv      ON tv.id_taux_tva = lf.id_taux_tva
GROUP BY f.id_facture, f.reference_facture, f.date_facture, tv.id_taux_tva, tv.code, tv.taux;

CREATE OR REPLACE VIEW v_promotions_actives AS
SELECT
    p.id_promotion,
    p.code_promo,
    tr.code    AS type_reduction,
    p.valeur,
    p.date_debut,
    p.date_fin,
    p.montant_minimum_commande,
    p.nb_utilisations_actuel,
    p.nb_utilisations_max,
    CASE
        WHEN p.nb_utilisations_max IS NULL THEN NULL
        ELSE ROUND(p.nb_utilisations_actuel * 100.0 / p.nb_utilisations_max, 1)
    END AS taux_utilisation_pct,
    p.cumulable
FROM promotion p
JOIN type_reduction tr ON tr.id_type_reduction = p.id_type_reduction
WHERE p.actif = TRUE
  AND NOW() BETWEEN p.date_debut AND p.date_fin;

CREATE OR REPLACE VIEW v_connexions_suspectes AS
SELECT
    t.user_type,
    t.email_tente,
    t.ip,
    t.nb_tentatives,
    t.premiere_tentative,
    t.derniere_tentative,
    t.bloque_jusqu_au,
    CASE WHEN t.bloque_jusqu_au > NOW() THEN TRUE ELSE FALSE END AS est_bloque
FROM tentative_connexion_echec t
WHERE t.nb_tentatives >= 5
ORDER BY t.nb_tentatives DESC;

CREATE OR REPLACE VIEW v_sessions_actives AS
SELECT
    s.id_session,
    s.user_type,
    s.user_id,
    CASE s.user_type
        WHEN 'CLIENT'  THEN (SELECT CONCAT(c.prenom, ' ', c.nom) FROM client c WHERE c.id_client = s.user_id)
        WHEN 'EMPLOYE' THEN (SELECT CONCAT(e.prenom, ' ', e.nom) FROM employe e WHERE e.id_employe = s.user_id)
    END AS nom_utilisateur,
    s.ip_creation,
    s.ip_derniere_activite,
    s.user_agent,
    s.date_creation,
    s.date_expiration,
    s.date_derniere_activite
FROM session_active s
WHERE s.actif = TRUE
  AND s.date_expiration > NOW();

SET FOREIGN_KEY_CHECKS = 1;
