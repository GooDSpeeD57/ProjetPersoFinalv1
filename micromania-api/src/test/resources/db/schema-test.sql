-- ============================================================
-- Micromania_V16-complet_test.sql
-- Version test MySQL locale générée à partir de schema.sql
-- Exécuter sur une base déjà sélectionnée (ex: micromania_test)
-- ============================================================

SET FOREIGN_KEY_CHECKS = 0;

-- ============================================================
-- DROP TABLES
-- ============================================================

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
DROP TABLE IF EXISTS produit_video;
DROP TABLE IF EXISTS produit_image;
DROP TABLE IF EXISTS produit_prix;
DROP TABLE IF EXISTS produit_variant;
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

CREATE TABLE type_garantie (
    id_type_garantie BIGINT AUTO_INCREMENT PRIMARY KEY,
    code             VARCHAR(50) NOT NULL UNIQUE,
    description      VARCHAR(255),
    duree_mois       INT NOT NULL,
    prix_extension   DECIMAL(10,2) NULL,
    CONSTRAINT chk_type_garantie_duree CHECK (duree_mois > 0)
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
    telephone_verifie                BOOLEAN NOT NULL DEFAULT FALSE,
    date_verification_telephone      DATETIME NULL,
    token_verification_telephone     VARCHAR(255) NULL,
    token_verification_telephone_expire_le DATETIME NULL,
    compte_active                BOOLEAN NOT NULL DEFAULT FALSE,
    cree_par_employe             BOOLEAN NOT NULL DEFAULT FALSE,
    id_employe_createur          BIGINT NULL,
    doit_definir_mot_de_passe    BOOLEAN NOT NULL DEFAULT TRUE,
    demande_suppression          BOOLEAN NOT NULL DEFAULT FALSE,
    date_suppression             DATETIME NULL,
    date_derniere_connexion      DATETIME NULL,
    date_creation                DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    date_modification            DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_client_avatar            FOREIGN KEY (id_avatar)            REFERENCES avatar(id_avatar),
    CONSTRAINT fk_client_fidelite          FOREIGN KEY (id_type_fidelite)    REFERENCES type_fidelite(id_type_fidelite),
    CONSTRAINT fk_client_employe_createur  FOREIGN KEY (id_employe_createur) REFERENCES employe(id_employe)
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
    edition                VARCHAR(100) NULL,
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
    CONSTRAINT fk_variant_tva        FOREIGN KEY (id_taux_tva)       REFERENCES taux_tva(id_taux_tva)
);

CREATE TABLE produit_prix (
    id_prix        BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_variant     BIGINT NOT NULL,
    id_canal_vente BIGINT NOT NULL,
    prix           DECIMAL(10,2) NOT NULL,
    date_debut     DATETIME NOT NULL,
    date_fin       DATETIME NULL,
    actif          BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT fk_produit_prix_variant FOREIGN KEY (id_variant)     REFERENCES produit_variant(id_variant) ON DELETE CASCADE,
    CONSTRAINT fk_produit_prix_canal   FOREIGN KEY (id_canal_vente) REFERENCES canal_vente(id_canal_vente),
    CONSTRAINT chk_prix_positif CHECK (prix > 0),
    CONSTRAINT chk_prix_dates   CHECK (date_fin IS NULL OR date_fin > date_debut)
);

CREATE TABLE produit_image (
    id_image        BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_produit      BIGINT NOT NULL,
    url             VARCHAR(255) NOT NULL,
    alt             VARCHAR(255) NOT NULL DEFAULT '',
    decorative      BOOLEAN NOT NULL DEFAULT FALSE,
    principale      BOOLEAN NOT NULL DEFAULT FALSE,
    ordre_affichage INT NOT NULL DEFAULT 0,
    date_creation   DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_image_produit FOREIGN KEY (id_produit) REFERENCES produit(id_produit) ON DELETE CASCADE
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
    id_ligne_panier BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_panier       BIGINT NOT NULL,
    id_variant      BIGINT NOT NULL,
    quantite        INT NOT NULL,
    prix_unitaire   DECIMAL(10,2) NOT NULL,
    date_creation   DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_ligne_panier_panier  FOREIGN KEY (id_panier)  REFERENCES panier(id_panier)               ON DELETE CASCADE,
    CONSTRAINT fk_ligne_panier_variant FOREIGN KEY (id_variant) REFERENCES produit_variant(id_variant),
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
    date_utilisation DATETIME NULL,
    CONSTRAINT fk_bon_client  FOREIGN KEY (id_client)  REFERENCES client(id_client),
    CONSTRAINT fk_bon_facture FOREIGN KEY (id_facture) REFERENCES facture(id_facture),
    CONSTRAINT chk_bon_valeur CHECK (valeur > 0),
    CONSTRAINT chk_bon_points CHECK (points_utilises > 0)
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
CREATE INDEX idx_variant_plateforme              ON produit_variant(id_plateforme);
CREATE INDEX idx_variant_format                  ON produit_variant(id_format_produit);
CREATE INDEX idx_variant_statut                  ON produit_variant(id_statut_produit);
CREATE INDEX idx_variant_serie                   ON produit_variant(necessite_numero_serie);
CREATE INDEX idx_variant_demat                   ON produit_variant(est_demat);
CREATE INDEX idx_variant_tcg                     ON produit_variant(est_tcg_unitaire);
CREATE INDEX idx_variant_reprise                 ON produit_variant(est_reprise);
CREATE INDEX idx_prix_variant                    ON produit_prix(id_variant);
CREATE INDEX idx_prix_canal                      ON produit_prix(id_canal_vente);
CREATE INDEX idx_prix_dates                      ON produit_prix(date_debut, date_fin);
CREATE INDEX idx_image_produit                   ON produit_image(id_produit);
CREATE INDEX idx_video_produit                   ON produit_video(id_produit);
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

-- ============================================================
-- TRIGGERS
-- ============================================================

-- ============================================================
-- PROCEDURES STOCKEES
-- ============================================================

-- ============================================================
-- PROCEDURES STOCKEES V16 : CONNEXION & PROMOTIONS
-- ============================================================

-- ============================================================
-- DONNEES DE REFERENCE
-- ============================================================

INSERT INTO type_fidelite (code, description, points_par_euro, seuil_upgrade_euro, prix_abonnement) VALUES
('NORMAL',   'Client standard',     1.00, 200.00, NULL),
('PREMIUM',  'Client premium',      1.20, NULL,   NULL),
('ULTIMATE', 'Abonnement Ultimate', 1.50, NULL,   9.99);

INSERT INTO type_categorie (code, description) VALUES
('JEU',          'Jeux video'),
('CONSOLE',      'Consoles de jeu'),
('ACCESSOIRE',   'Accessoires gaming'),
('GOODIES',      'Produits derives'),
('CARTE_CADEAU', 'Cartes cadeaux'),
('TCG',          'Trading Card Game');

INSERT INTO type_adresse (code) VALUES
('FACTURATION'), ('DOMICILE'), ('LIVRAISON'), ('MAGASIN');

INSERT INTO role (code, libelle) VALUES
('VENDEUR', 'Vendeur'),
('MANAGER', 'Manager'),
('ADMIN',   'Administrateur');

INSERT INTO permission (code, description) VALUES
('CATALOGUE_READ',  'Consulter le catalogue'),
('CATALOGUE_WRITE', 'Modifier le catalogue'),
('STOCK_READ',      'Consulter le stock'),
('STOCK_WRITE',     'Modifier le stock'),
('AVIS_MODERATE',   'Moderer les avis'),
('SAV_MANAGE',      'Gerer le SAV'),
('PLANNING_READ',   'Consulter le planning'),
('PLANNING_WRITE',  'Modifier le planning'),
('REPRISE_MANAGE',  'Gerer les reprises'),
('TCG_MANAGE',      'Gerer le catalogue TCG');

INSERT INTO statut_produit (code, description) VALUES
('NEUF',     'Produit neuf'),
('OCCASION', 'Produit d occasion'),
('REPRISE',  'Produit issu d une reprise'),
('LOCATION', 'Produit loue');

INSERT INTO statut_panier (code) VALUES ('ACTIF'), ('VALIDE'), ('ABANDONNE');

INSERT INTO statut_commande (code) VALUES
('CREEE'), ('PAYEE'), ('PREPARATION'), ('EXPEDIEE'), ('LIVREE'),
('RETIRABLE'), ('RETIREE'), ('ANNULEE'), ('REMBOURSEE');

INSERT INTO statut_facture (code) VALUES ('EMISE'), ('ANNULEE'), ('REMBOURSEE');

INSERT INTO statut_planning (code) VALUES ('PREVU'), ('PRESENT'), ('ABSENT'), ('CONGE');

INSERT INTO statut_abonnement (code) VALUES ('ACTIF'), ('EXPIRE'), ('RESILIE'), ('EN_ATTENTE');

INSERT INTO statut_retour (code) VALUES ('DEMANDE'), ('ACCEPTE'), ('REFUSE'), ('TRAITE');

INSERT INTO statut_sav (code) VALUES ('OUVERT'), ('EN_COURS'), ('EN_ATTENTE_PIECE'), ('CLOTURE');

INSERT INTO statut_reprise (code) VALUES ('BROUILLON'), ('ESTIMEE'), ('VALIDEE'), ('REFUSEE'), ('PAYEE');

INSERT INTO statut_avis (code) VALUES ('EN_ATTENTE'), ('APPROUVE'), ('REFUSE'), ('SIGNALE');

INSERT INTO type_reduction (code) VALUES ('POURCENTAGE'), ('MONTANT_FIXE');

INSERT INTO taux_tva (code, description, taux) VALUES
('NORMAL',       'Taux normal',       20.00),
('REDUIT',       'Taux réduit',        5.50),
('SUPER_REDUIT', 'Taux super réduit',  2.10),
('ZERO',         'Exonéré de TVA',     0.00);

UPDATE type_categorie
SET id_taux_tva_defaut = (SELECT id_taux_tva FROM taux_tva WHERE code = 'NORMAL')
WHERE code IN ('JEU', 'CONSOLE', 'ACCESSOIRE', 'GOODIES', 'TCG', 'CARTE_CADEAU');

INSERT INTO type_mouvement (code) VALUES
('ENTREE'), ('SORTIE'), ('TRANSFERT'), ('AJUSTEMENT'), ('RETOUR'), ('REPRISE_ENTREE');

INSERT INTO type_retour (code) VALUES
('RETRACTATION'), ('DEFAUT'), ('SAV'), ('ERREUR_COMMANDE');

INSERT INTO provider_auth (code, libelle) VALUES
('GOOGLE',   'Google'),
('FACEBOOK', 'Facebook'),
('APPLE',    'Apple');

INSERT INTO statut_precommande (code) VALUES
('ENREGISTREE'), ('ACOMPTE_PAYE'), ('CONFIRMEE'), ('ANNULEE'), ('CONVERTIE_EN_COMMANDE');

INSERT INTO statut_paiement (code) VALUES
('EN_ATTENTE'), ('AUTORISE'), ('CAPTURE'), ('REFUSE'), ('REMBOURSE'), ('ANNULE');

INSERT INTO mode_paiement (code) VALUES
('CB'), ('ESPECES'), ('PAYPAL'), ('APPLE_PAY'), ('GOOGLE_PAY'), ('STRIPE'), ('KLARNA');

INSERT INTO mode_compensation_reprise (code, description) VALUES
('ESPECES',   'Paiement en especes'),
('BON_ACHAT', 'Bon achat'),
('AVOIR',     'Avoir client');

INSERT INTO mode_livraison (code, description) VALUES
('DOMICILE',        'Livraison a domicile'),
('RETRAIT_MAGASIN', 'Retrait en magasin'),
('POINT_RELAIS',    'Livraison en point relais');

INSERT INTO canal_vente (code) VALUES ('WEB'), ('MOBILE'), ('MAGASIN');

INSERT INTO format_produit (code, description) VALUES
('PHYSIQUE',       'Produit physique'),
('DEMAT',          'Produit dematerialise'),
('CARTE_CODE',     'Carte avec code'),
('SCELLE',         'Produit TCG scelle'),
('CARTE_UNITAIRE', 'Carte TCG a l unite');

INSERT INTO contexte_vente (code, description) VALUES
('EN_LIGNE',   'Vente effectuee en ligne (web ou mobile)'),
('EN_MAGASIN', 'Vente effectuee en magasin physique');

INSERT INTO plateforme (code, libelle) VALUES
('PSP',            'PlayStation Portable'),
('PS_VITA',        'PlayStation Vita'),
('PS1',            'PlayStation'),
('PS2',            'PlayStation 2'),
('PS3',            'PlayStation 3'),
('PS4',            'PlayStation 4'),
('PS5',            'PlayStation 5'),
('XBOX',           'Xbox'),
('XBOX_360',       'Xbox 360'),
('XBOX_ONE',       'Xbox One S/X'),
('XBOX_SERIES',    'Xbox Series S/X'),
('NES',            'Nintendo Entertainment System'),
('SNES',           'Super Nintendo'),
('N64',            'Nintendo 64'),
('WII',            'Nintendo Wii'),
('GAMECUBE',       'Game Cube'),
('WII_U',          'Nintendo Wii U'),
('SWITCH',         'Nintendo Switch'),
('SWITCH2',        'Nintendo Switch 2'),
('GAMEBOY',        'Game Boy'),
('GAMEBOY_COLOR',  'Game Boy Color'),
('GAMEBOY_ADVANCE','Game Boy Advance'),
('GBA_SP',         'Game Boy Advance SP'),
('NDS',            'Nintendo DS'),
('NDS_LITE',       'Nintendo DS Lite'),
('DSI',            'Nintendo DSi'),
('3DS',            'Nintendo 3DS'),
('NEW_3DS',        'New Nintendo 3DS'),
('2DS',            'Nintendo 2DS'),
('NEW_2DS_XL',     'New Nintendo 2DS XL'),
('SG1000',         'Sega SG-1000'),
('MASTER_SYSTEM',  'Sega Master System'),
('MEGADRIVE',      'Sega Mega Drive / Genesis'),
('SEGA_CD',        'Sega CD / Mega-CD'),
('SEGA_32X',       'Sega 32X'),
('SATURN',         'Sega Saturn'),
('DREAMCAST',      'Sega Dreamcast'),
('GAME_GEAR',      'Sega Game Gear'),
('NOMAD',          'Sega Nomad'),
('NEOGEO_AES',     'SNK Neo Geo AES'),
('NEOGEO_CD',      'SNK Neo Geo CD'),
('JAGUAR',         'Atari Jaguar'),
('LYNX',           'Atari Lynx'),
('3DO',            'Panasonic 3DO'),
('PC',             'PC');

INSERT INTO type_garantie (code, description, duree_mois, prix_extension) VALUES
('STANDARD_CONSOLE',    'Garantie standard console',    24, 49.99),
('STANDARD_ACCESSOIRE', 'Garantie standard accessoire', 12, 19.99),
('ETENDUE_CONSOLE',     'Extension console',            12, 39.99),
('ETENDUE_ACCESSOIRE',  'Extension accessoire',         12,  9.99),
('OCCASION_CONSOLE',    'Garantie occasion console',     6,  NULL);

INSERT INTO etat_carte_tcg (code, libelle, coefficient_prix) VALUES
('NM',   'Near Mint', 1.00),
('EX',   'Excellent', 0.90),
('GD',   'Good',      0.75),
('PL',   'Played',    0.60),
('POOR', 'Poor',      0.40);

INSERT INTO ratio_points (id_type_categorie, id_type_fidelite, ratio)
SELECT tc.id_type_categorie, tf.id_type_fidelite, r.ratio
FROM (
    SELECT 'JEU'       AS cat, 'NORMAL'   AS niv, 1.00 AS ratio UNION ALL
    SELECT 'JEU',              'PREMIUM',          1.20 UNION ALL
    SELECT 'JEU',              'ULTIMATE',         1.50 UNION ALL
    SELECT 'CONSOLE',          'NORMAL',           0.50 UNION ALL
    SELECT 'CONSOLE',          'PREMIUM',          0.60 UNION ALL
    SELECT 'CONSOLE',          'ULTIMATE',         0.80 UNION ALL
    SELECT 'ACCESSOIRE',       'NORMAL',           0.80 UNION ALL
    SELECT 'ACCESSOIRE',       'PREMIUM',          1.00 UNION ALL
    SELECT 'ACCESSOIRE',       'ULTIMATE',         1.20 UNION ALL
    SELECT 'GOODIES',          'NORMAL',           1.00 UNION ALL
    SELECT 'GOODIES',          'PREMIUM',          1.20 UNION ALL
    SELECT 'GOODIES',          'ULTIMATE',         1.50 UNION ALL
    SELECT 'TCG',              'NORMAL',           0.70 UNION ALL
    SELECT 'TCG',              'PREMIUM',          0.90 UNION ALL
    SELECT 'TCG',              'ULTIMATE',         1.10
) r
JOIN type_categorie tc ON tc.code = r.cat
JOIN type_fidelite tf  ON tf.code = r.niv;

INSERT INTO tcg_jeu (code, nom, editeur) VALUES
('POKEMON', 'Pokemon',              'The Pokemon Company'),
('YUGIOH',  'Yu-Gi-Oh!',            'Konami'),
('MAGIC',   'Magic: The Gathering', 'Wizards of the Coast'),
('LORCANA', 'Disney Lorcana',       'Ravensburger');

INSERT INTO role_permission (id_role, id_permission)
SELECT r.id_role, p.id_permission
FROM role r
JOIN permission p
WHERE (r.code = 'ADMIN')
   OR (r.code = 'MANAGER'  AND p.code IN ('CATALOGUE_READ','CATALOGUE_WRITE','STOCK_READ','STOCK_WRITE',
       'AVIS_MODERATE','SAV_MANAGE','PLANNING_READ','PLANNING_WRITE','REPRISE_MANAGE','TCG_MANAGE'))
   OR (r.code = 'VENDEUR'  AND p.code IN ('CATALOGUE_READ','STOCK_READ','SAV_MANAGE','PLANNING_READ','REPRISE_MANAGE'));

-- ============================================================
-- JEUX DE DONNEES DE TEST ET CATALOGUE ETENDU
-- ============================================================

-- IMPORTANT SECURITE
-- Remplacer les placeholders BCrypt ci-dessous par de vrais hashes generes par l'API ou Spring Security.
-- Mot de passe clair CLIENT  : Azerty1!
-- Mot de passe clair EMPLOYE : Admin1!?

-- ------------------------------------------------------------
-- MAGASINS
-- ------------------------------------------------------------

INSERT INTO magasin (nom, telephone, email) VALUES
('Micromania Paris Les Halles', '0140203040', 'halles@micromania.fr'),
('Micromania Lyon Part-Dieu',   '0472000000', 'lyon@micromania.fr'),
('Micromania Lille Centre',     '0320000000', 'lille@micromania.fr');

INSERT INTO entrepot (nom, code, telephone, email, responsable) VALUES
('Entrepot National Micromania', 'ENT-NATIONAL', '0123456789', 'logistique@micromania.fr', 'Direction Logistique');

-- ------------------------------------------------------------
-- AVATARS (3 inseres : id 1 par defaut, 2 et 3 pour clients)
-- ------------------------------------------------------------

INSERT INTO avatar (id_avatar, nom, url, alt, decorative, actif) VALUES
(1, 'Avatar par defaut',  '/images/avatars/default.png',  'Avatar utilisateur par defaut',  FALSE, TRUE),
(2, 'Avatar Bleu',        '/images/avatars/avatar-2.png', 'Avatar bleu',                    FALSE, TRUE),
(3, 'Avatar Rouge',       '/images/avatars/avatar-3.png', 'Avatar rouge',                   FALSE, TRUE);

-- ------------------------------------------------------------
-- EMPLOYES
-- ------------------------------------------------------------

INSERT INTO employe (nom, prenom, email, telephone, mot_de_passe, id_role, id_magasin) VALUES
('Martin',  'Lucas', 'lucas.martin@micromania.fr',   '0600000001', '$2a$10$EMPLOYE_HASH_EXEMPLE_REPLACE', 1, 1),
('Durand',  'Emma',  'emma.durand@micromania.fr',    '0600000002', '$2a$10$EMPLOYE_HASH_EXEMPLE_REPLACE', 1, 1),
('Bernard', 'Nathan','nathan.bernard@micromania.fr', '0600000003', '$2a$10$EMPLOYE_HASH_EXEMPLE_REPLACE', 2, 2),
('Petit',   'Chloe', 'chloe.petit@micromania.fr',    '0600000004', '$2a$10$EMPLOYE_HASH_EXEMPLE_REPLACE', 2, 2),
('Moreau',  'Hugo',  'hugo.moreau@micromania.fr',    '0600000005', '$2a$10$EMPLOYE_HASH_EXEMPLE_REPLACE', 3, 3);

-- ------------------------------------------------------------
-- CLIENTS
-- Colonnes : pseudo, nom, prenom, date_naissance, email, telephone, mot_de_passe,
--            id_type_fidelite, id_avatar, email_verifie, date_verification_email,
--            token_verification_email, token_verification_expire_le,
--            compte_active, cree_par_employe, id_employe_createur, doit_definir_mot_de_passe
-- ------------------------------------------------------------

INSERT INTO client (
    pseudo, nom, prenom, date_naissance, email, telephone, mot_de_passe,
    id_type_fidelite, id_avatar, email_verifie, date_verification_email,
    token_verification_email, token_verification_expire_le,
    compte_active, cree_par_employe, id_employe_createur, doit_definir_mot_de_passe
) VALUES
('gamermax',      'Dupont', 'Maxime', '1995-04-12', 'maxime.dupont@email.fr',  '0610000001', '$2a$10$9MRPaZaEWh4YUeYRhpjOO.zuJ5G9UZERCLpRNC6qKDNdBArhtGg/i', 1, 1, TRUE,  NOW(), NULL,                          NULL,                                  TRUE,  FALSE, NULL, FALSE),
('nintendofan',   'Leroy',  'Julie',  '1998-09-21', 'julie.leroy@email.fr',    '0610000002', '$2a$10$LF6JZvQ7EYSAKY6F7lmGP.RGzWBtWv2RItg3ULT1w0BlQ1zOINx7q', 1, 2, TRUE,  NOW(), NULL,                          NULL,                                  TRUE,  FALSE, NULL, FALSE),
('retroplayer',   'Garcia', 'Thomas', '1990-02-05', 'thomas.garcia@email.fr',  '0610000003', '$2a$10$LF6JZvQ7EYSAKY6F7lmGP.RGzWBtWv2RItg3ULT1w0BlQ1zOINx7q', 1, 1, FALSE, NULL,  'verify-retroplayer-token',    DATE_ADD(NOW(), INTERVAL 24 HOUR),     FALSE, FALSE, NULL, TRUE),
('cardmaster',    'Roux',   'Alex',   '1997-07-11', 'alex.roux@email.fr',      '0610000004', '$2a$10$LF6JZvQ7EYSAKY6F7lmGP.RGzWBtWv2RItg3ULT1w0BlQ1zOINx7q', 1, 3, TRUE,  NOW(), NULL,                          NULL,                                  TRUE,  FALSE, NULL, FALSE),
('consoleking',   'Faure',  'Leo',    '2000-12-01', 'leo.faure@email.fr',      '0610000005', '$2a$10$LF6JZvQ7EYSAKY6F7lmGP.RGzWBtWv2RItg3ULT1w0BlQ1zOINx7q', 1, 1, FALSE, NULL,  'verify-consoleking-token',    DATE_ADD(NOW(), INTERVAL 24 HOUR),     FALSE, FALSE, NULL, TRUE),
('clientmagasin', 'Martin', 'Paul',   '1990-01-01', 'paul.martin@email.fr',    '0610000006', '$2a$10$LF6JZvQ7EYSAKY6F7lmGP.RGzWBtWv2RItg3ULT1w0BlQ1zOINx7q', 1, 1, FALSE, NULL,  'verify-clientmagasin-token',  DATE_ADD(NOW(), INTERVAL 24 HOUR),     FALSE, TRUE,  1,    TRUE);

-- ------------------------------------------------------------
-- TOKENS / FAVORIS / AUTH SOCIALE
-- ------------------------------------------------------------

INSERT INTO reset_password_token (id_client, token, expire_le, utilise) VALUES
(3, 'reset-retroplayer-token', DATE_ADD(NOW(), INTERVAL 2 HOUR), FALSE),
(5, 'reset-consoleking-token', DATE_ADD(NOW(), INTERVAL 1 HOUR), FALSE);

INSERT INTO favori_magasin (id_client, id_magasin, principal) VALUES
(1, 1, TRUE), (2, 2, TRUE), (3, 3, TRUE), (4, 1, TRUE);

INSERT INTO client_auth_provider (id_client, id_provider_auth, provider_user_id, email_provider) VALUES
(1, (SELECT id_provider_auth FROM provider_auth WHERE code='GOOGLE'),   'google-uid-1001',   'maxime.dupont@email.fr'),
(2, (SELECT id_provider_auth FROM provider_auth WHERE code='APPLE'),    'apple-uid-2001',    'julie.leroy@email.fr'),
(4, (SELECT id_provider_auth FROM provider_auth WHERE code='FACEBOOK'), 'facebook-uid-4001', 'alex.roux@email.fr');

-- ------------------------------------------------------------
-- ADRESSES (GPS inclus)
-- ------------------------------------------------------------

INSERT INTO adresse (id_magasin, id_type_adresse, rue, ville, code_postal, pays, latitude, longitude, est_defaut) VALUES
(1, 4, '101 Porte Berger',          'Paris', '75001', 'France', 48.861111, 2.346389, TRUE),
(2, 4, '17 Rue du Docteur Bouchut', 'Lyon',  '69003', 'France', 45.760700, 4.861900, TRUE),
(3, 4, '31 Rue de Bethune',         'Lille', '59800', 'France', 50.636400, 3.063000, TRUE);

INSERT INTO adresse (id_entrepot, id_type_adresse, rue, ville, code_postal, pays, latitude, longitude, est_defaut) VALUES
(1, 4, 'Zone Industrielle du Mesnil-Amelot', 'Mesnil-Amelot', '77990', 'France', 49.020000, 2.700000, TRUE);

INSERT INTO adresse (id_client, id_type_adresse, rue, ville, code_postal, pays, latitude, longitude, est_defaut) VALUES
(1, 3, '12 Rue Rambuteau',       'Paris', '75003', 'France', 48.860200, 2.352100, TRUE),
(2, 3, '8 Rue de la Republique', 'Lyon',  '69002', 'France', 45.757900, 4.834600, TRUE),
(3, 3, '22 Rue Nationale',       'Lille', '59800', 'France', 50.637300, 3.063900, TRUE),
(4, 3, '5 Avenue Jean Jaures',   'Paris', '75019', 'France', 48.882000, 2.376000, TRUE),
(5, 3, '14 Rue Faidherbe',       'Lille', '59000', 'France', 50.633800, 3.069100, TRUE),
(6, 3, '18 Rue de Rivoli',       'Paris', '75004', 'France', 48.855700, 2.357300, TRUE);

-- ------------------------------------------------------------
-- CATEGORIES
-- ------------------------------------------------------------

INSERT INTO categorie (id_type_categorie, nom, description) VALUES
((SELECT id_type_categorie FROM type_categorie WHERE code='JEU'),       'Jeux Video',  'Jeux toutes plateformes'),
((SELECT id_type_categorie FROM type_categorie WHERE code='CONSOLE'),   'Consoles',    'Consoles de jeux'),
((SELECT id_type_categorie FROM type_categorie WHERE code='ACCESSOIRE'),'Accessoires', 'Accessoires gaming'),
((SELECT id_type_categorie FROM type_categorie WHERE code='GOODIES'),   'Goodies',     'Produits derives gaming'),
((SELECT id_type_categorie FROM type_categorie WHERE code='TCG'),       'Cartes TCG',  'Trading card games');

-- ------------------------------------------------------------
-- 60 JEUX (PS4 / PS5 / XBOX ONE / XBOX SERIES / SWITCH / SWITCH2)
-- ------------------------------------------------------------

INSERT INTO produit (id_categorie, nom, slug, description, resume_court, editeur, pegi, marque, mis_en_avant) VALUES
((SELECT id_categorie FROM categorie WHERE nom='Jeux Video'),'Marvel Spider-Man','marvel-spider-man-ps4','Marvel Spider-Man sur PS4','Marvel Spider-Man version PS4','Sony',16,'Sony',FALSE),
((SELECT id_categorie FROM categorie WHERE nom='Jeux Video'),'God of War Ragnarok','god-of-war-ragnarok-ps4','God of War Ragnarok sur PS4','God of War Ragnarok version PS4','Sony',18,'Sony',FALSE),
((SELECT id_categorie FROM categorie WHERE nom='Jeux Video'),'Horizon Forbidden West','horizon-forbidden-west-ps4','Horizon Forbidden West sur PS4','Horizon Forbidden West version PS4','Sony',16,'Sony',FALSE),
((SELECT id_categorie FROM categorie WHERE nom='Jeux Video'),'Gran Turismo 7','gran-turismo-7-ps4','Gran Turismo 7 sur PS4','Gran Turismo 7 version PS4','Sony',3,'Sony',FALSE),
((SELECT id_categorie FROM categorie WHERE nom='Jeux Video'),'Ghost of Tsushima','ghost-of-tsushima-ps4','Ghost of Tsushima sur PS4','Ghost of Tsushima version PS4','Sony',18,'Sony',FALSE),
((SELECT id_categorie FROM categorie WHERE nom='Jeux Video'),'The Last of Us Part II','the-last-of-us-part-ii-ps4','The Last of Us Part II sur PS4','The Last of Us Part II version PS4','Sony',18,'Sony',FALSE),
((SELECT id_categorie FROM categorie WHERE nom='Jeux Video'),'Ratchet and Clank','ratchet-and-clank-ps4','Ratchet and Clank sur PS4','Ratchet and Clank version PS4','Sony',7,'Sony',FALSE),
((SELECT id_categorie FROM categorie WHERE nom='Jeux Video'),'Death Stranding','death-stranding-ps4','Death Stranding sur PS4','Death Stranding version PS4','Sony',18,'Sony',FALSE),
((SELECT id_categorie FROM categorie WHERE nom='Jeux Video'),'Resident Evil 4','resident-evil-4-ps4','Resident Evil 4 sur PS4','Resident Evil 4 version PS4','Capcom',18,'Capcom',FALSE),
((SELECT id_categorie FROM categorie WHERE nom='Jeux Video'),'EA Sports FC 25','ea-sports-fc-25-ps4','EA Sports FC 25 sur PS4','EA Sports FC 25 version PS4','EA',3,'EA',FALSE),
((SELECT id_categorie FROM categorie WHERE nom='Jeux Video'),'Marvel Spider-Man 2','marvel-spider-man-2-ps5','Marvel Spider-Man 2 sur PS5','Marvel Spider-Man 2 version PS5','Sony',16,'Sony',FALSE),
((SELECT id_categorie FROM categorie WHERE nom='Jeux Video'),'Final Fantasy VII Rebirth','final-fantasy-vii-rebirth-ps5','Final Fantasy VII Rebirth sur PS5','Final Fantasy VII Rebirth version PS5','Square Enix',16,'Square Enix',FALSE),
((SELECT id_categorie FROM categorie WHERE nom='Jeux Video'),'Astro Bot','astro-bot-ps5','Astro Bot sur PS5','Astro Bot version PS5','Sony',7,'Sony',FALSE),
((SELECT id_categorie FROM categorie WHERE nom='Jeux Video'),'Stellar Blade','stellar-blade-ps5','Stellar Blade sur PS5','Stellar Blade version PS5','Sony',18,'Sony',FALSE),
((SELECT id_categorie FROM categorie WHERE nom='Jeux Video'),'Demon Souls','demon-souls-ps5','Demon Souls sur PS5','Demon Souls version PS5','Sony',18,'Sony',FALSE),
((SELECT id_categorie FROM categorie WHERE nom='Jeux Video'),'Returnal','returnal-ps5','Returnal sur PS5','Returnal version PS5','Sony',16,'Sony',FALSE),
((SELECT id_categorie FROM categorie WHERE nom='Jeux Video'),'Rise of the Ronin','rise-of-the-ronin-ps5','Rise of the Ronin sur PS5','Rise of the Ronin version PS5','Sony',18,'Sony',FALSE),
((SELECT id_categorie FROM categorie WHERE nom='Jeux Video'),'Gran Turismo 7 PS5','gran-turismo-7-ps5-ps5','Gran Turismo 7 PS5 sur PS5','Gran Turismo 7 PS5 version PS5','Sony',3,'Sony',FALSE),
((SELECT id_categorie FROM categorie WHERE nom='Jeux Video'),'The Last of Us Part I','the-last-of-us-part-i-ps5','The Last of Us Part I sur PS5','The Last of Us Part I version PS5','Sony',18,'Sony',FALSE),
((SELECT id_categorie FROM categorie WHERE nom='Jeux Video'),'Helldivers 2','helldivers-2-ps5','Helldivers 2 sur PS5','Helldivers 2 version PS5','Sony',18,'Sony',FALSE),
((SELECT id_categorie FROM categorie WHERE nom='Jeux Video'),'Halo The Master Chief Collection','halo-the-master-chief-collection-xbox-one','Halo The Master Chief Collection sur XBOX_ONE','Halo The Master Chief Collection version XBOX_ONE','Microsoft',16,'Microsoft',FALSE),
((SELECT id_categorie FROM categorie WHERE nom='Jeux Video'),'Halo 5 Guardians','halo-5-guardians-xbox-one','Halo 5 Guardians sur XBOX_ONE','Halo 5 Guardians version XBOX_ONE','Microsoft',16,'Microsoft',FALSE),
((SELECT id_categorie FROM categorie WHERE nom='Jeux Video'),'Forza Horizon 4','forza-horizon-4-xbox-one','Forza Horizon 4 sur XBOX_ONE','Forza Horizon 4 version XBOX_ONE','Microsoft',3,'Microsoft',FALSE),
((SELECT id_categorie FROM categorie WHERE nom='Jeux Video'),'Forza Motorsport 7','forza-motorsport-7-xbox-one','Forza Motorsport 7 sur XBOX_ONE','Forza Motorsport 7 version XBOX_ONE','Microsoft',3,'Microsoft',FALSE),
((SELECT id_categorie FROM categorie WHERE nom='Jeux Video'),'Gears 5','gears-5-xbox-one','Gears 5 sur XBOX_ONE','Gears 5 version XBOX_ONE','Microsoft',18,'Microsoft',FALSE),
((SELECT id_categorie FROM categorie WHERE nom='Jeux Video'),'Sea of Thieves','sea-of-thieves-xbox-one','Sea of Thieves sur XBOX_ONE','Sea of Thieves version XBOX_ONE','Microsoft',12,'Microsoft',FALSE),
((SELECT id_categorie FROM categorie WHERE nom='Jeux Video'),'Assassins Creed Valhalla','assassins-creed-valhalla-xbox-one','Assassins Creed Valhalla sur XBOX_ONE','Assassins Creed Valhalla version XBOX_ONE','Ubisoft',18,'Ubisoft',FALSE),
((SELECT id_categorie FROM categorie WHERE nom='Jeux Video'),'Red Dead Redemption 2','red-dead-redemption-2-xbox-one','Red Dead Redemption 2 sur XBOX_ONE','Red Dead Redemption 2 version XBOX_ONE','Rockstar',18,'Rockstar',FALSE),
((SELECT id_categorie FROM categorie WHERE nom='Jeux Video'),'EA Sports FC 25 Xbox One','ea-sports-fc-25-xbox-one-xbox-one','EA Sports FC 25 Xbox One sur XBOX_ONE','EA Sports FC 25 Xbox One version XBOX_ONE','EA',3,'EA',FALSE),
((SELECT id_categorie FROM categorie WHERE nom='Jeux Video'),'Elden Ring Xbox One','elden-ring-xbox-one-xbox-one','Elden Ring Xbox One sur XBOX_ONE','Elden Ring Xbox One version XBOX_ONE','Bandai Namco',18,'Bandai Namco',FALSE),
((SELECT id_categorie FROM categorie WHERE nom='Jeux Video'),'Halo Infinite','halo-infinite-xbox-series','Halo Infinite sur XBOX_SERIES','Halo Infinite version XBOX_SERIES','Microsoft',16,'Microsoft',FALSE),
((SELECT id_categorie FROM categorie WHERE nom='Jeux Video'),'Forza Horizon 5','forza-horizon-5-xbox-series','Forza Horizon 5 sur XBOX_SERIES','Forza Horizon 5 version XBOX_SERIES','Microsoft',3,'Microsoft',FALSE),
((SELECT id_categorie FROM categorie WHERE nom='Jeux Video'),'Starfield','starfield-xbox-series','Starfield sur XBOX_SERIES','Starfield version XBOX_SERIES','Bethesda',18,'Bethesda',FALSE),
((SELECT id_categorie FROM categorie WHERE nom='Jeux Video'),'Indiana Jones and the Great Circle','indiana-jones-and-the-great-circle-xbox-series','Indiana Jones and the Great Circle sur XBOX_SERIES','Indiana Jones and the Great Circle version XBOX_SERIES','Bethesda',16,'Bethesda',FALSE),
((SELECT id_categorie FROM categorie WHERE nom='Jeux Video'),'Avowed','avowed-xbox-series','Avowed sur XBOX_SERIES','Avowed version XBOX_SERIES','Xbox Game Studios',18,'Xbox Game Studios',FALSE),
((SELECT id_categorie FROM categorie WHERE nom='Jeux Video'),'Senua Saga Hellblade II','senua-saga-hellblade-ii-xbox-series','Senua Saga Hellblade II sur XBOX_SERIES','Senua Saga Hellblade II version XBOX_SERIES','Xbox Game Studios',18,'Xbox Game Studios',FALSE),
((SELECT id_categorie FROM categorie WHERE nom='Jeux Video'),'Microsoft Flight Simulator 2024','microsoft-flight-simulator-2024-xbox-series','Microsoft Flight Simulator 2024 sur XBOX_SERIES','Microsoft Flight Simulator 2024 version XBOX_SERIES','Microsoft',3,'Microsoft',FALSE),
((SELECT id_categorie FROM categorie WHERE nom='Jeux Video'),'Diablo IV','diablo-iv-xbox-series','Diablo IV sur XBOX_SERIES','Diablo IV version XBOX_SERIES','Blizzard',18,'Blizzard',FALSE),
((SELECT id_categorie FROM categorie WHERE nom='Jeux Video'),'Like a Dragon Infinite Wealth','like-a-dragon-infinite-wealth-xbox-series','Like a Dragon Infinite Wealth sur XBOX_SERIES','Like a Dragon Infinite Wealth version XBOX_SERIES','Sega',18,'Sega',FALSE),
((SELECT id_categorie FROM categorie WHERE nom='Jeux Video'),'EA Sports FC 25 Xbox Series','ea-sports-fc-25-xbox-series-xbox-series','EA Sports FC 25 Xbox Series sur XBOX_SERIES','EA Sports FC 25 Xbox Series version XBOX_SERIES','EA',3,'EA',FALSE),
((SELECT id_categorie FROM categorie WHERE nom='Jeux Video'),'Mario Kart 8 Deluxe','mario-kart-8-deluxe-switch','Mario Kart 8 Deluxe sur SWITCH','Mario Kart 8 Deluxe version SWITCH','Nintendo',3,'Nintendo',FALSE),
((SELECT id_categorie FROM categorie WHERE nom='Jeux Video'),'The Legend of Zelda Breath of the Wild','the-legend-of-zelda-breath-of-the-wild-switch','The Legend of Zelda Breath of the Wild sur SWITCH','The Legend of Zelda Breath of the Wild version SWITCH','Nintendo',12,'Nintendo',FALSE),
((SELECT id_categorie FROM categorie WHERE nom='Jeux Video'),'The Legend of Zelda Tears of the Kingdom','the-legend-of-zelda-tears-of-the-kingdom-switch','The Legend of Zelda Tears of the Kingdom sur SWITCH','The Legend of Zelda Tears of the Kingdom version SWITCH','Nintendo',12,'Nintendo',FALSE),
((SELECT id_categorie FROM categorie WHERE nom='Jeux Video'),'Super Mario Odyssey','super-mario-odyssey-switch','Super Mario Odyssey sur SWITCH','Super Mario Odyssey version SWITCH','Nintendo',7,'Nintendo',FALSE),
((SELECT id_categorie FROM categorie WHERE nom='Jeux Video'),'Pokemon Ecarlate','pokemon-ecarlate-switch','Pokemon Ecarlate sur SWITCH','Pokemon Ecarlate version SWITCH','Nintendo',7,'Nintendo',FALSE),
((SELECT id_categorie FROM categorie WHERE nom='Jeux Video'),'Pokemon Violet','pokemon-violet-switch','Pokemon Violet sur SWITCH','Pokemon Violet version SWITCH','Nintendo',7,'Nintendo',FALSE),
((SELECT id_categorie FROM categorie WHERE nom='Jeux Video'),'Super Smash Bros Ultimate','super-smash-bros-ultimate-switch','Super Smash Bros Ultimate sur SWITCH','Super Smash Bros Ultimate version SWITCH','Nintendo',12,'Nintendo',FALSE),
((SELECT id_categorie FROM categorie WHERE nom='Jeux Video'),'Animal Crossing New Horizons','animal-crossing-new-horizons-switch','Animal Crossing New Horizons sur SWITCH','Animal Crossing New Horizons version SWITCH','Nintendo',3,'Nintendo',FALSE),
((SELECT id_categorie FROM categorie WHERE nom='Jeux Video'),'Metroid Dread','metroid-dread-switch','Metroid Dread sur SWITCH','Metroid Dread version SWITCH','Nintendo',12,'Nintendo',FALSE),
((SELECT id_categorie FROM categorie WHERE nom='Jeux Video'),'Splatoon 3','splatoon-3-switch','Splatoon 3 sur SWITCH','Splatoon 3 version SWITCH','Nintendo',7,'Nintendo',FALSE),
((SELECT id_categorie FROM categorie WHERE nom='Jeux Video'),'Mario Kart World','mario-kart-world-switch2','Mario Kart World sur SWITCH2','Mario Kart World version SWITCH2','Nintendo',3,'Nintendo',FALSE),
((SELECT id_categorie FROM categorie WHERE nom='Jeux Video'),'Donkey Kong Bananza','donkey-kong-bananza-switch2','Donkey Kong Bananza sur SWITCH2','Donkey Kong Bananza version SWITCH2','Nintendo',7,'Nintendo',FALSE),
((SELECT id_categorie FROM categorie WHERE nom='Jeux Video'),'Nintendo Switch 2 Welcome Tour','nintendo-switch-2-welcome-tour-switch2','Nintendo Switch 2 Welcome Tour sur SWITCH2','Nintendo Switch 2 Welcome Tour version SWITCH2','Nintendo',3,'Nintendo',FALSE),
((SELECT id_categorie FROM categorie WHERE nom='Jeux Video'),'Hyrule Warriors Les Chroniques du Sceau','hyrule-warriors-les-chroniques-du-sceau-switch2','Hyrule Warriors Les Chroniques du Sceau sur SWITCH2','Hyrule Warriors Les Chroniques du Sceau version SWITCH2','Nintendo',12,'Nintendo',FALSE),
((SELECT id_categorie FROM categorie WHERE nom='Jeux Video'),'Kirby Air Riders','kirby-air-riders-switch2','Kirby Air Riders sur SWITCH2','Kirby Air Riders version SWITCH2','Nintendo',7,'Nintendo',FALSE),
((SELECT id_categorie FROM categorie WHERE nom='Jeux Video'),'Animal Crossing New Horizons Switch 2 Edition','animal-crossing-new-horizons-switch-2-edition-switch2','Animal Crossing New Horizons Switch 2 Edition sur SWITCH2','Animal Crossing New Horizons Switch 2 Edition version SWITCH2','Nintendo',3,'Nintendo',FALSE),
((SELECT id_categorie FROM categorie WHERE nom='Jeux Video'),'Legendes Pokemon ZA Switch 2 Edition','legendes-pokemon-za-switch-2-edition-switch2','Legendes Pokemon ZA Switch 2 Edition sur SWITCH2','Legendes Pokemon ZA Switch 2 Edition version SWITCH2','Nintendo',7,'Nintendo',FALSE),
((SELECT id_categorie FROM categorie WHERE nom='Jeux Video'),'Xenoblade Chronicles X Definitive Edition Switch 2','xenoblade-chronicles-x-definitive-edition-switch-2-switch2','Xenoblade Chronicles X Definitive Edition Switch 2 sur SWITCH2','Xenoblade Chronicles X Definitive Edition Switch 2 version SWITCH2','Nintendo',12,'Nintendo',FALSE),
((SELECT id_categorie FROM categorie WHERE nom='Jeux Video'),'Final Fantasy VII Remake Intergrade Switch 2','final-fantasy-vii-remake-intergrade-switch-2-switch2','Final Fantasy VII Remake Intergrade Switch 2 sur SWITCH2','Final Fantasy VII Remake Intergrade Switch 2 version SWITCH2','Square Enix',16,'Square Enix',FALSE),
((SELECT id_categorie FROM categorie WHERE nom='Jeux Video'),'Yakuza Kiwami 3 and Dark Ties','yakuza-kiwami-3-and-dark-ties-switch2','Yakuza Kiwami 3 and Dark Ties sur SWITCH2','Yakuza Kiwami 3 and Dark Ties version SWITCH2','Sega',18,'Sega',FALSE);

INSERT INTO produit_image (id_produit, url, alt, principale, ordre_affichage)
SELECT id_produit,
       CONCAT('https://images.micromania.local/catalogue/jeux/', slug, '.jpg'),
       CONCAT('Visuel ', nom), TRUE, 1
FROM produit
WHERE id_categorie = (SELECT id_categorie FROM categorie WHERE nom='Jeux Video');

INSERT INTO produit_variant (id_produit, sku, id_plateforme, id_format_produit, id_statut_produit, nom_commercial, scelle, est_demat, est_tcg_unitaire, est_reprise)
SELECT
    p.id_produit,
    UPPER(CONCAT(p.slug, '-NEUF')) AS sku,
    pf.id_plateforme,
    (SELECT id_format_produit FROM format_produit WHERE code = 'PHYSIQUE'),
    (SELECT id_statut_produit FROM statut_produit WHERE code = 'NEUF'),
    CONCAT(p.nom, ' ', pf.code, ' Neuf'),
    FALSE, FALSE, FALSE, FALSE
FROM produit p
JOIN plateforme pf ON pf.code = CASE
    WHEN p.slug LIKE '%-ps4'         THEN 'PS4'
    WHEN p.slug LIKE '%-ps5'         THEN 'PS5'
    WHEN p.slug LIKE '%-xbox-one'    THEN 'XBOX_ONE'
    WHEN p.slug LIKE '%-xbox-series' THEN 'XBOX_SERIES'
    WHEN p.slug LIKE '%-switch2'     THEN 'SWITCH2'
    WHEN p.slug LIKE '%-switch'      THEN 'SWITCH'
END
WHERE p.id_categorie = (SELECT id_categorie FROM categorie WHERE nom = 'Jeux Video');

INSERT INTO produit_variant (id_produit, sku, id_plateforme, id_format_produit, id_statut_produit, nom_commercial, scelle, est_demat, est_tcg_unitaire, est_reprise)
SELECT
    p.id_produit,
    UPPER(CONCAT(p.slug, '-OCC')) AS sku,
    pf.id_plateforme,
    (SELECT id_format_produit FROM format_produit WHERE code = 'PHYSIQUE'),
    (SELECT id_statut_produit FROM statut_produit WHERE code = 'OCCASION'),
    CONCAT(p.nom, ' ', pf.code, ' Occasion'),
    FALSE, FALSE, FALSE, FALSE
FROM produit p
JOIN plateforme pf ON pf.code = CASE
    WHEN p.slug LIKE '%-ps4'         THEN 'PS4'
    WHEN p.slug LIKE '%-ps5'         THEN 'PS5'
    WHEN p.slug LIKE '%-xbox-one'    THEN 'XBOX_ONE'
    WHEN p.slug LIKE '%-xbox-series' THEN 'XBOX_SERIES'
    WHEN p.slug LIKE '%-switch2'     THEN 'SWITCH2'
    WHEN p.slug LIKE '%-switch'      THEN 'SWITCH'
END
WHERE p.id_categorie = (SELECT id_categorie FROM categorie WHERE nom = 'Jeux Video');

-- ------------------------------------------------------------
-- 10 CONSOLES (NEUF + OCCASION)
-- ------------------------------------------------------------

INSERT INTO produit (id_categorie, nom, slug, description, resume_court, constructeur, marque, mis_en_avant) VALUES
((SELECT id_categorie FROM categorie WHERE nom='Consoles'),'PlayStation 4 500 Go',        'playstation-4-500-go',        'PlayStation 4 500 Go',        'PlayStation 4 500 Go',        'Sony',      'Sony',      TRUE),
((SELECT id_categorie FROM categorie WHERE nom='Consoles'),'PlayStation 4 Pro 1 To',      'playstation-4-pro-1-to',      'PlayStation 4 Pro 1 To',      'PlayStation 4 Pro 1 To',      'Sony',      'Sony',      TRUE),
((SELECT id_categorie FROM categorie WHERE nom='Consoles'),'PlayStation 5 Slim Standard', 'playstation-5-slim-standard', 'PlayStation 5 Slim Standard', 'PlayStation 5 Slim Standard', 'Sony',      'Sony',      TRUE),
((SELECT id_categorie FROM categorie WHERE nom='Consoles'),'PlayStation 5 Pro 2 To',      'playstation-5-pro-2-to',      'PlayStation 5 Pro 2 To',      'PlayStation 5 Pro 2 To',      'Sony',      'Sony',      TRUE),
((SELECT id_categorie FROM categorie WHERE nom='Consoles'),'Nintendo Switch Standard',    'nintendo-switch-standard',    'Nintendo Switch Standard',    'Nintendo Switch Standard',    'Nintendo',  'Nintendo',  TRUE),
((SELECT id_categorie FROM categorie WHERE nom='Consoles'),'Nintendo Switch OLED Blanche','nintendo-switch-oled-blanche','Nintendo Switch OLED Blanche','Nintendo Switch OLED Blanche','Nintendo',  'Nintendo',  TRUE),
((SELECT id_categorie FROM categorie WHERE nom='Consoles'),'Nintendo Switch Lite Turquoise','nintendo-switch-lite-turquoise','Nintendo Switch Lite Turquoise','Nintendo Switch Lite Turquoise','Nintendo','Nintendo',TRUE),
((SELECT id_categorie FROM categorie WHERE nom='Consoles'),'Nintendo Switch 2 Standard',  'nintendo-switch-2-standard',  'Nintendo Switch 2 Standard',  'Nintendo Switch 2 Standard',  'Nintendo',  'Nintendo',  TRUE),
((SELECT id_categorie FROM categorie WHERE nom='Consoles'),'Xbox Series S 1 To',          'xbox-series-s-1-to',          'Xbox Series S 1 To',          'Xbox Series S 1 To',          'Microsoft', 'Microsoft', TRUE),
((SELECT id_categorie FROM categorie WHERE nom='Consoles'),'Xbox Series X 1 To',          'xbox-series-x-1-to',          'Xbox Series X 1 To',          'Xbox Series X 1 To',          'Microsoft', 'Microsoft', TRUE);

INSERT INTO produit_image (id_produit, url, alt, principale, ordre_affichage)
SELECT id_produit, CONCAT('https://images.micromania.local/catalogue/consoles/', slug, '.jpg'), CONCAT('Visuel ', nom), TRUE, 1
FROM produit
WHERE id_categorie = (SELECT id_categorie FROM categorie WHERE nom='Consoles');

INSERT INTO produit_variant (id_produit, sku, id_plateforme, id_format_produit, id_statut_produit, nom_commercial, scelle, est_demat, est_tcg_unitaire, est_reprise, necessite_numero_serie) VALUES
((SELECT id_produit FROM produit WHERE slug='playstation-4-500-go'),        'PLAYSTATION-4-500-GO-NEUF',          (SELECT id_plateforme FROM plateforme WHERE code='PS4'),        (SELECT id_format_produit FROM format_produit WHERE code='PHYSIQUE'),(SELECT id_statut_produit FROM statut_produit WHERE code='NEUF'),    'PlayStation 4 500 Go Neuf',          FALSE,FALSE,FALSE,FALSE,TRUE),
((SELECT id_produit FROM produit WHERE slug='playstation-4-500-go'),        'PLAYSTATION-4-500-GO-OCC',           (SELECT id_plateforme FROM plateforme WHERE code='PS4'),        (SELECT id_format_produit FROM format_produit WHERE code='PHYSIQUE'),(SELECT id_statut_produit FROM statut_produit WHERE code='OCCASION'),'PlayStation 4 500 Go Occasion',      FALSE,FALSE,FALSE,FALSE,TRUE),
((SELECT id_produit FROM produit WHERE slug='playstation-4-pro-1-to'),      'PLAYSTATION-4-PRO-1-TO-NEUF',        (SELECT id_plateforme FROM plateforme WHERE code='PS4'),        (SELECT id_format_produit FROM format_produit WHERE code='PHYSIQUE'),(SELECT id_statut_produit FROM statut_produit WHERE code='NEUF'),    'PlayStation 4 Pro 1 To Neuf',        FALSE,FALSE,FALSE,FALSE,TRUE),
((SELECT id_produit FROM produit WHERE slug='playstation-4-pro-1-to'),      'PLAYSTATION-4-PRO-1-TO-OCC',         (SELECT id_plateforme FROM plateforme WHERE code='PS4'),        (SELECT id_format_produit FROM format_produit WHERE code='PHYSIQUE'),(SELECT id_statut_produit FROM statut_produit WHERE code='OCCASION'),'PlayStation 4 Pro 1 To Occasion',    FALSE,FALSE,FALSE,FALSE,TRUE),
((SELECT id_produit FROM produit WHERE slug='playstation-5-slim-standard'), 'PLAYSTATION-5-SLIM-STANDARD-NEUF',   (SELECT id_plateforme FROM plateforme WHERE code='PS5'),        (SELECT id_format_produit FROM format_produit WHERE code='PHYSIQUE'),(SELECT id_statut_produit FROM statut_produit WHERE code='NEUF'),    'PlayStation 5 Slim Standard Neuf',   FALSE,FALSE,FALSE,FALSE,TRUE),
((SELECT id_produit FROM produit WHERE slug='playstation-5-slim-standard'), 'PLAYSTATION-5-SLIM-STANDARD-OCC',    (SELECT id_plateforme FROM plateforme WHERE code='PS5'),        (SELECT id_format_produit FROM format_produit WHERE code='PHYSIQUE'),(SELECT id_statut_produit FROM statut_produit WHERE code='OCCASION'),'PlayStation 5 Slim Standard Occasion',FALSE,FALSE,FALSE,FALSE,TRUE),
((SELECT id_produit FROM produit WHERE slug='playstation-5-pro-2-to'),      'PLAYSTATION-5-PRO-2-TO-NEUF',        (SELECT id_plateforme FROM plateforme WHERE code='PS5'),        (SELECT id_format_produit FROM format_produit WHERE code='PHYSIQUE'),(SELECT id_statut_produit FROM statut_produit WHERE code='NEUF'),    'PlayStation 5 Pro 2 To Neuf',        FALSE,FALSE,FALSE,FALSE,TRUE),
((SELECT id_produit FROM produit WHERE slug='playstation-5-pro-2-to'),      'PLAYSTATION-5-PRO-2-TO-OCC',         (SELECT id_plateforme FROM plateforme WHERE code='PS5'),        (SELECT id_format_produit FROM format_produit WHERE code='PHYSIQUE'),(SELECT id_statut_produit FROM statut_produit WHERE code='OCCASION'),'PlayStation 5 Pro 2 To Occasion',    FALSE,FALSE,FALSE,FALSE,TRUE),
((SELECT id_produit FROM produit WHERE slug='nintendo-switch-standard'),    'NINTENDO-SWITCH-STANDARD-NEUF',      (SELECT id_plateforme FROM plateforme WHERE code='SWITCH'),     (SELECT id_format_produit FROM format_produit WHERE code='PHYSIQUE'),(SELECT id_statut_produit FROM statut_produit WHERE code='NEUF'),    'Nintendo Switch Standard Neuf',      FALSE,FALSE,FALSE,FALSE,TRUE),
((SELECT id_produit FROM produit WHERE slug='nintendo-switch-standard'),    'NINTENDO-SWITCH-STANDARD-OCC',       (SELECT id_plateforme FROM plateforme WHERE code='SWITCH'),     (SELECT id_format_produit FROM format_produit WHERE code='PHYSIQUE'),(SELECT id_statut_produit FROM statut_produit WHERE code='OCCASION'),'Nintendo Switch Standard Occasion',  FALSE,FALSE,FALSE,FALSE,TRUE),
((SELECT id_produit FROM produit WHERE slug='nintendo-switch-oled-blanche'),'NINTENDO-SWITCH-OLED-BLANCHE-NEUF',  (SELECT id_plateforme FROM plateforme WHERE code='SWITCH'),     (SELECT id_format_produit FROM format_produit WHERE code='PHYSIQUE'),(SELECT id_statut_produit FROM statut_produit WHERE code='NEUF'),    'Nintendo Switch OLED Blanche Neuf',  FALSE,FALSE,FALSE,FALSE,TRUE),
((SELECT id_produit FROM produit WHERE slug='nintendo-switch-oled-blanche'),'NINTENDO-SWITCH-OLED-BLANCHE-OCC',   (SELECT id_plateforme FROM plateforme WHERE code='SWITCH'),     (SELECT id_format_produit FROM format_produit WHERE code='PHYSIQUE'),(SELECT id_statut_produit FROM statut_produit WHERE code='OCCASION'),'Nintendo Switch OLED Blanche Occasion',FALSE,FALSE,FALSE,FALSE,TRUE),
((SELECT id_produit FROM produit WHERE slug='nintendo-switch-lite-turquoise'),'NINTENDO-SWITCH-LITE-TURQUOISE-NEUF',(SELECT id_plateforme FROM plateforme WHERE code='SWITCH'),   (SELECT id_format_produit FROM format_produit WHERE code='PHYSIQUE'),(SELECT id_statut_produit FROM statut_produit WHERE code='NEUF'),    'Nintendo Switch Lite Turquoise Neuf',FALSE,FALSE,FALSE,FALSE,TRUE),
((SELECT id_produit FROM produit WHERE slug='nintendo-switch-lite-turquoise'),'NINTENDO-SWITCH-LITE-TURQUOISE-OCC', (SELECT id_plateforme FROM plateforme WHERE code='SWITCH'),   (SELECT id_format_produit FROM format_produit WHERE code='PHYSIQUE'),(SELECT id_statut_produit FROM statut_produit WHERE code='OCCASION'),'Nintendo Switch Lite Turquoise Occasion',FALSE,FALSE,FALSE,FALSE,TRUE),
((SELECT id_produit FROM produit WHERE slug='nintendo-switch-2-standard'),  'NINTENDO-SWITCH-2-STANDARD-NEUF',    (SELECT id_plateforme FROM plateforme WHERE code='SWITCH2'),   (SELECT id_format_produit FROM format_produit WHERE code='PHYSIQUE'),(SELECT id_statut_produit FROM statut_produit WHERE code='NEUF'),    'Nintendo Switch 2 Standard Neuf',    FALSE,FALSE,FALSE,FALSE,TRUE),
((SELECT id_produit FROM produit WHERE slug='nintendo-switch-2-standard'),  'NINTENDO-SWITCH-2-STANDARD-OCC',     (SELECT id_plateforme FROM plateforme WHERE code='SWITCH2'),   (SELECT id_format_produit FROM format_produit WHERE code='PHYSIQUE'),(SELECT id_statut_produit FROM statut_produit WHERE code='OCCASION'),'Nintendo Switch 2 Standard Occasion',FALSE,FALSE,FALSE,FALSE,TRUE),
((SELECT id_produit FROM produit WHERE slug='xbox-series-s-1-to'),          'XBOX-SERIES-S-1-TO-NEUF',            (SELECT id_plateforme FROM plateforme WHERE code='XBOX_SERIES'),(SELECT id_format_produit FROM format_produit WHERE code='PHYSIQUE'),(SELECT id_statut_produit FROM statut_produit WHERE code='NEUF'),    'Xbox Series S 1 To Neuf',            FALSE,FALSE,FALSE,FALSE,TRUE),
((SELECT id_produit FROM produit WHERE slug='xbox-series-s-1-to'),          'XBOX-SERIES-S-1-TO-OCC',             (SELECT id_plateforme FROM plateforme WHERE code='XBOX_SERIES'),(SELECT id_format_produit FROM format_produit WHERE code='PHYSIQUE'),(SELECT id_statut_produit FROM statut_produit WHERE code='OCCASION'),'Xbox Series S 1 To Occasion',        FALSE,FALSE,FALSE,FALSE,TRUE),
((SELECT id_produit FROM produit WHERE slug='xbox-series-x-1-to'),          'XBOX-SERIES-X-1-TO-NEUF',            (SELECT id_plateforme FROM plateforme WHERE code='XBOX_SERIES'),(SELECT id_format_produit FROM format_produit WHERE code='PHYSIQUE'),(SELECT id_statut_produit FROM statut_produit WHERE code='NEUF'),    'Xbox Series X 1 To Neuf',            FALSE,FALSE,FALSE,FALSE,TRUE),
((SELECT id_produit FROM produit WHERE slug='xbox-series-x-1-to'),          'XBOX-SERIES-X-1-TO-OCC',             (SELECT id_plateforme FROM plateforme WHERE code='XBOX_SERIES'),(SELECT id_format_produit FROM format_produit WHERE code='PHYSIQUE'),(SELECT id_statut_produit FROM statut_produit WHERE code='OCCASION'),'Xbox Series X 1 To Occasion',        FALSE,FALSE,FALSE,FALSE,TRUE);

-- ------------------------------------------------------------
-- 30 ACCESSOIRES (NEUF + OCCASION)
-- ------------------------------------------------------------

INSERT INTO produit (id_categorie, nom, slug, description, resume_court, constructeur, marque) VALUES
((SELECT id_categorie FROM categorie WHERE nom='Accessoires'),'Manette DualSense Blanche',           'manette-dualsense-blanche',           'Manette DualSense Blanche',           'Manette DualSense Blanche',           'Sony',      'Sony'),
((SELECT id_categorie FROM categorie WHERE nom='Accessoires'),'Manette DualSense Midnight Black',    'manette-dualsense-midnight-black',    'Manette DualSense Midnight Black',    'Manette DualSense Midnight Black',    'Sony',      'Sony'),
((SELECT id_categorie FROM categorie WHERE nom='Accessoires'),'Station de recharge DualSense',       'station-de-recharge-dualsense',       'Station de recharge DualSense',       'Station de recharge DualSense',       'Sony',      'Sony'),
((SELECT id_categorie FROM categorie WHERE nom='Accessoires'),'Casque Pulse 3D',                     'casque-pulse-3d',                     'Casque Pulse 3D',                     'Casque Pulse 3D',                     'Sony',      'Sony'),
((SELECT id_categorie FROM categorie WHERE nom='Accessoires'),'Camera HD PS5',                       'camera-hd-ps5',                       'Camera HD PS5',                       'Camera HD PS5',                       'Sony',      'Sony'),
((SELECT id_categorie FROM categorie WHERE nom='Accessoires'),'Telecommande multimedia PS5',         'telecommande-multimedia-ps5',         'Telecommande multimedia PS5',         'Telecommande multimedia PS5',         'Sony',      'Sony'),
((SELECT id_categorie FROM categorie WHERE nom='Accessoires'),'Manette PS4 V2 Noire',                'manette-ps4-v2-noire',                'Manette PS4 V2 Noire',                'Manette PS4 V2 Noire',                'Sony',      'Sony'),
((SELECT id_categorie FROM categorie WHERE nom='Accessoires'),'Station de recharge manettes PS4',    'station-de-recharge-manettes-ps4',    'Station de recharge manettes PS4',    'Station de recharge manettes PS4',    'Sony',      'Sony'),
((SELECT id_categorie FROM categorie WHERE nom='Accessoires'),'Casque filaire gaming PS4',           'casque-filaire-gaming-ps4',           'Casque filaire gaming PS4',           'Casque filaire gaming PS4',           'Sony',      'Sony'),
((SELECT id_categorie FROM categorie WHERE nom='Accessoires'),'Disque dur externe PS4 2To',          'disque-dur-externe-ps4-2to',          'Disque dur externe PS4 2To',          'Disque dur externe PS4 2To',          'Seagate',   'Seagate'),
((SELECT id_categorie FROM categorie WHERE nom='Accessoires'),'Manette Xbox Wireless Carbon Black',  'manette-xbox-wireless-carbon-black',  'Manette Xbox Wireless Carbon Black',  'Manette Xbox Wireless Carbon Black',  'Microsoft', 'Microsoft'),
((SELECT id_categorie FROM categorie WHERE nom='Accessoires'),'Manette Xbox Wireless Robot White',   'manette-xbox-wireless-robot-white',   'Manette Xbox Wireless Robot White',   'Manette Xbox Wireless Robot White',   'Microsoft', 'Microsoft'),
((SELECT id_categorie FROM categorie WHERE nom='Accessoires'),'Batterie rechargeable Xbox',          'batterie-rechargeable-xbox',          'Batterie rechargeable Xbox',          'Batterie rechargeable Xbox',          'Microsoft', 'Microsoft'),
((SELECT id_categorie FROM categorie WHERE nom='Accessoires'),'Station de charge Xbox',              'station-de-charge-xbox',              'Station de charge Xbox',              'Station de charge Xbox',              'Venom',     'Venom'),
((SELECT id_categorie FROM categorie WHERE nom='Accessoires'),'Casque sans fil Xbox',                'casque-sans-fil-xbox',                'Casque sans fil Xbox',                'Casque sans fil Xbox',                'Microsoft', 'Microsoft'),
((SELECT id_categorie FROM categorie WHERE nom='Accessoires'),'Support vertical Xbox Series X',      'support-vertical-xbox-series-x',      'Support vertical Xbox Series X',      'Support vertical Xbox Series X',      'Generic',   'Generic'),
((SELECT id_categorie FROM categorie WHERE nom='Accessoires'),'Carte extension stockage Xbox 1To',   'carte-extension-stockage-xbox-1to',   'Carte extension stockage Xbox 1To',   'Carte extension stockage Xbox 1To',   'Seagate',   'Seagate'),
((SELECT id_categorie FROM categorie WHERE nom='Accessoires'),'Manette Xbox One Noire',              'manette-xbox-one-noire',              'Manette Xbox One Noire',              'Manette Xbox One Noire',              'Microsoft', 'Microsoft'),
((SELECT id_categorie FROM categorie WHERE nom='Accessoires'),'Casque stereo Xbox One',              'casque-stereo-xbox-one',              'Casque stereo Xbox One',              'Casque stereo Xbox One',              'Microsoft', 'Microsoft'),
((SELECT id_categorie FROM categorie WHERE nom='Accessoires'),'Dock Nintendo Switch',                'dock-nintendo-switch',                'Dock Nintendo Switch',                'Dock Nintendo Switch',                'Nintendo',  'Nintendo'),
((SELECT id_categorie FROM categorie WHERE nom='Accessoires'),'Paire Joy-Con Bleu Rouge',            'paire-joy-con-bleu-rouge',            'Paire Joy-Con Bleu Rouge',            'Paire Joy-Con Bleu Rouge',            'Nintendo',  'Nintendo'),
((SELECT id_categorie FROM categorie WHERE nom='Accessoires'),'Manette Pro Switch',                  'manette-pro-switch',                  'Manette Pro Switch',                  'Manette Pro Switch',                  'Nintendo',  'Nintendo'),
((SELECT id_categorie FROM categorie WHERE nom='Accessoires'),'Etui de transport Switch',            'etui-de-transport-switch',            'Etui de transport Switch',            'Etui de transport Switch',            'Nintendo',  'Nintendo'),
((SELECT id_categorie FROM categorie WHERE nom='Accessoires'),'Chargeur secteur Switch',             'chargeur-secteur-switch',             'Chargeur secteur Switch',             'Chargeur secteur Switch',             'Nintendo',  'Nintendo'),
((SELECT id_categorie FROM categorie WHERE nom='Accessoires'),'Volant Joy-Con Mario',                'volant-joy-con-mario',                'Volant Joy-Con Mario',                'Volant Joy-Con Mario',                'Nintendo',  'Nintendo'),
((SELECT id_categorie FROM categorie WHERE nom='Accessoires'),'Protection ecran Switch OLED',        'protection-ecran-switch-oled',        'Protection ecran Switch OLED',        'Protection ecran Switch OLED',        'BigBen',    'BigBen'),
((SELECT id_categorie FROM categorie WHERE nom='Accessoires'),'Etui Switch Lite',                    'etui-switch-lite',                    'Etui Switch Lite',                    'Etui Switch Lite',                    'Nintendo',  'Nintendo'),
((SELECT id_categorie FROM categorie WHERE nom='Accessoires'),'Paire Joy-Con 2 Switch 2',            'paire-joy-con-2-switch-2',            'Paire Joy-Con 2 Switch 2',            'Paire Joy-Con 2 Switch 2',            'Nintendo',  'Nintendo'),
((SELECT id_categorie FROM categorie WHERE nom='Accessoires'),'Manette Pro Switch 2',                'manette-pro-switch-2',                'Manette Pro Switch 2',                'Manette Pro Switch 2',                'Nintendo',  'Nintendo'),
((SELECT id_categorie FROM categorie WHERE nom='Accessoires'),'Camera Nintendo Switch 2',            'camera-nintendo-switch-2',            'Camera Nintendo Switch 2',            'Camera Nintendo Switch 2',            'Nintendo',  'Nintendo');

INSERT INTO produit_image (id_produit, url, alt, principale, ordre_affichage)
SELECT id_produit, CONCAT('https://images.micromania.local/catalogue/accessoires/', slug, '.jpg'), CONCAT('Visuel ', nom), TRUE, 1
FROM produit WHERE id_categorie = (SELECT id_categorie FROM categorie WHERE nom='Accessoires');

INSERT INTO produit_variant (id_produit, sku, id_plateforme, id_format_produit, id_statut_produit, nom_commercial, scelle, est_demat, est_tcg_unitaire, est_reprise) VALUES
((SELECT id_produit FROM produit WHERE slug='manette-dualsense-blanche'),          'MANETTE-DUALSENSE-BLANCHE-NEUF',           (SELECT id_plateforme FROM plateforme WHERE code='PS5'),        (SELECT id_format_produit FROM format_produit WHERE code='PHYSIQUE'),(SELECT id_statut_produit FROM statut_produit WHERE code='NEUF'),    'Manette DualSense Blanche Neuf',           FALSE,FALSE,FALSE,FALSE),
((SELECT id_produit FROM produit WHERE slug='manette-dualsense-blanche'),          'MANETTE-DUALSENSE-BLANCHE-OCC',            (SELECT id_plateforme FROM plateforme WHERE code='PS5'),        (SELECT id_format_produit FROM format_produit WHERE code='PHYSIQUE'),(SELECT id_statut_produit FROM statut_produit WHERE code='OCCASION'),'Manette DualSense Blanche Occasion',       FALSE,FALSE,FALSE,FALSE),
((SELECT id_produit FROM produit WHERE slug='manette-dualsense-midnight-black'),   'MANETTE-DUALSENSE-MIDNIGHT-BLACK-NEUF',    (SELECT id_plateforme FROM plateforme WHERE code='PS5'),        (SELECT id_format_produit FROM format_produit WHERE code='PHYSIQUE'),(SELECT id_statut_produit FROM statut_produit WHERE code='NEUF'),    'Manette DualSense Midnight Black Neuf',    FALSE,FALSE,FALSE,FALSE),
((SELECT id_produit FROM produit WHERE slug='manette-dualsense-midnight-black'),   'MANETTE-DUALSENSE-MIDNIGHT-BLACK-OCC',     (SELECT id_plateforme FROM plateforme WHERE code='PS5'),        (SELECT id_format_produit FROM format_produit WHERE code='PHYSIQUE'),(SELECT id_statut_produit FROM statut_produit WHERE code='OCCASION'),'Manette DualSense Midnight Black Occasion', FALSE,FALSE,FALSE,FALSE),
((SELECT id_produit FROM produit WHERE slug='station-de-recharge-dualsense'),      'STATION-DE-RECHARGE-DUALSENSE-NEUF',       (SELECT id_plateforme FROM plateforme WHERE code='PS5'),        (SELECT id_format_produit FROM format_produit WHERE code='PHYSIQUE'),(SELECT id_statut_produit FROM statut_produit WHERE code='NEUF'),    'Station de recharge DualSense Neuf',       FALSE,FALSE,FALSE,FALSE),
((SELECT id_produit FROM produit WHERE slug='station-de-recharge-dualsense'),      'STATION-DE-RECHARGE-DUALSENSE-OCC',        (SELECT id_plateforme FROM plateforme WHERE code='PS5'),        (SELECT id_format_produit FROM format_produit WHERE code='PHYSIQUE'),(SELECT id_statut_produit FROM statut_produit WHERE code='OCCASION'),'Station de recharge DualSense Occasion',   FALSE,FALSE,FALSE,FALSE),
((SELECT id_produit FROM produit WHERE slug='casque-pulse-3d'),                    'CASQUE-PULSE-3D-NEUF',                     (SELECT id_plateforme FROM plateforme WHERE code='PS5'),        (SELECT id_format_produit FROM format_produit WHERE code='PHYSIQUE'),(SELECT id_statut_produit FROM statut_produit WHERE code='NEUF'),    'Casque Pulse 3D Neuf',                     FALSE,FALSE,FALSE,FALSE),
((SELECT id_produit FROM produit WHERE slug='casque-pulse-3d'),                    'CASQUE-PULSE-3D-OCC',                      (SELECT id_plateforme FROM plateforme WHERE code='PS5'),        (SELECT id_format_produit FROM format_produit WHERE code='PHYSIQUE'),(SELECT id_statut_produit FROM statut_produit WHERE code='OCCASION'),'Casque Pulse 3D Occasion',                 FALSE,FALSE,FALSE,FALSE),
((SELECT id_produit FROM produit WHERE slug='camera-hd-ps5'),                      'CAMERA-HD-PS5-NEUF',                       (SELECT id_plateforme FROM plateforme WHERE code='PS5'),        (SELECT id_format_produit FROM format_produit WHERE code='PHYSIQUE'),(SELECT id_statut_produit FROM statut_produit WHERE code='NEUF'),    'Camera HD PS5 Neuf',                       FALSE,FALSE,FALSE,FALSE),
((SELECT id_produit FROM produit WHERE slug='camera-hd-ps5'),                      'CAMERA-HD-PS5-OCC',                        (SELECT id_plateforme FROM plateforme WHERE code='PS5'),        (SELECT id_format_produit FROM format_produit WHERE code='PHYSIQUE'),(SELECT id_statut_produit FROM statut_produit WHERE code='OCCASION'),'Camera HD PS5 Occasion',                   FALSE,FALSE,FALSE,FALSE),
((SELECT id_produit FROM produit WHERE slug='telecommande-multimedia-ps5'),        'TELECOMMANDE-MULTIMEDIA-PS5-NEUF',         (SELECT id_plateforme FROM plateforme WHERE code='PS5'),        (SELECT id_format_produit FROM format_produit WHERE code='PHYSIQUE'),(SELECT id_statut_produit FROM statut_produit WHERE code='NEUF'),    'Telecommande multimedia PS5 Neuf',         FALSE,FALSE,FALSE,FALSE),
((SELECT id_produit FROM produit WHERE slug='telecommande-multimedia-ps5'),        'TELECOMMANDE-MULTIMEDIA-PS5-OCC',          (SELECT id_plateforme FROM plateforme WHERE code='PS5'),        (SELECT id_format_produit FROM format_produit WHERE code='PHYSIQUE'),(SELECT id_statut_produit FROM statut_produit WHERE code='OCCASION'),'Telecommande multimedia PS5 Occasion',     FALSE,FALSE,FALSE,FALSE),
((SELECT id_produit FROM produit WHERE slug='manette-ps4-v2-noire'),               'MANETTE-PS4-V2-NOIRE-NEUF',                (SELECT id_plateforme FROM plateforme WHERE code='PS4'),        (SELECT id_format_produit FROM format_produit WHERE code='PHYSIQUE'),(SELECT id_statut_produit FROM statut_produit WHERE code='NEUF'),    'Manette PS4 V2 Noire Neuf',                FALSE,FALSE,FALSE,FALSE),
((SELECT id_produit FROM produit WHERE slug='manette-ps4-v2-noire'),               'MANETTE-PS4-V2-NOIRE-OCC',                 (SELECT id_plateforme FROM plateforme WHERE code='PS4'),        (SELECT id_format_produit FROM format_produit WHERE code='PHYSIQUE'),(SELECT id_statut_produit FROM statut_produit WHERE code='OCCASION'),'Manette PS4 V2 Noire Occasion',            FALSE,FALSE,FALSE,FALSE),
((SELECT id_produit FROM produit WHERE slug='station-de-recharge-manettes-ps4'),   'STATION-DE-RECHARGE-MANETTES-PS4-NEUF',    (SELECT id_plateforme FROM plateforme WHERE code='PS4'),        (SELECT id_format_produit FROM format_produit WHERE code='PHYSIQUE'),(SELECT id_statut_produit FROM statut_produit WHERE code='NEUF'),    'Station de recharge manettes PS4 Neuf',    FALSE,FALSE,FALSE,FALSE),
((SELECT id_produit FROM produit WHERE slug='station-de-recharge-manettes-ps4'),   'STATION-DE-RECHARGE-MANETTES-PS4-OCC',     (SELECT id_plateforme FROM plateforme WHERE code='PS4'),        (SELECT id_format_produit FROM format_produit WHERE code='PHYSIQUE'),(SELECT id_statut_produit FROM statut_produit WHERE code='OCCASION'),'Station de recharge manettes PS4 Occasion',FALSE,FALSE,FALSE,FALSE),
((SELECT id_produit FROM produit WHERE slug='casque-filaire-gaming-ps4'),          'CASQUE-FILAIRE-GAMING-PS4-NEUF',           (SELECT id_plateforme FROM plateforme WHERE code='PS4'),        (SELECT id_format_produit FROM format_produit WHERE code='PHYSIQUE'),(SELECT id_statut_produit FROM statut_produit WHERE code='NEUF'),    'Casque filaire gaming PS4 Neuf',           FALSE,FALSE,FALSE,FALSE),
((SELECT id_produit FROM produit WHERE slug='casque-filaire-gaming-ps4'),          'CASQUE-FILAIRE-GAMING-PS4-OCC',            (SELECT id_plateforme FROM plateforme WHERE code='PS4'),        (SELECT id_format_produit FROM format_produit WHERE code='PHYSIQUE'),(SELECT id_statut_produit FROM statut_produit WHERE code='OCCASION'),'Casque filaire gaming PS4 Occasion',       FALSE,FALSE,FALSE,FALSE),
((SELECT id_produit FROM produit WHERE slug='disque-dur-externe-ps4-2to'),         'DISQUE-DUR-EXTERNE-PS4-2TO-NEUF',          (SELECT id_plateforme FROM plateforme WHERE code='PS4'),        (SELECT id_format_produit FROM format_produit WHERE code='PHYSIQUE'),(SELECT id_statut_produit FROM statut_produit WHERE code='NEUF'),    'Disque dur externe PS4 2To Neuf',          FALSE,FALSE,FALSE,FALSE),
((SELECT id_produit FROM produit WHERE slug='disque-dur-externe-ps4-2to'),         'DISQUE-DUR-EXTERNE-PS4-2TO-OCC',           (SELECT id_plateforme FROM plateforme WHERE code='PS4'),        (SELECT id_format_produit FROM format_produit WHERE code='PHYSIQUE'),(SELECT id_statut_produit FROM statut_produit WHERE code='OCCASION'),'Disque dur externe PS4 2To Occasion',      FALSE,FALSE,FALSE,FALSE),
((SELECT id_produit FROM produit WHERE slug='manette-xbox-wireless-carbon-black'), 'MANETTE-XBOX-WIRELESS-CARBON-BLACK-NEUF',  (SELECT id_plateforme FROM plateforme WHERE code='XBOX_SERIES'),(SELECT id_format_produit FROM format_produit WHERE code='PHYSIQUE'),(SELECT id_statut_produit FROM statut_produit WHERE code='NEUF'),    'Manette Xbox Wireless Carbon Black Neuf',  FALSE,FALSE,FALSE,FALSE),
((SELECT id_produit FROM produit WHERE slug='manette-xbox-wireless-carbon-black'), 'MANETTE-XBOX-WIRELESS-CARBON-BLACK-OCC',   (SELECT id_plateforme FROM plateforme WHERE code='XBOX_SERIES'),(SELECT id_format_produit FROM format_produit WHERE code='PHYSIQUE'),(SELECT id_statut_produit FROM statut_produit WHERE code='OCCASION'),'Manette Xbox Wireless Carbon Black Occasion',FALSE,FALSE,FALSE,FALSE),
((SELECT id_produit FROM produit WHERE slug='manette-xbox-wireless-robot-white'),  'MANETTE-XBOX-WIRELESS-ROBOT-WHITE-NEUF',   (SELECT id_plateforme FROM plateforme WHERE code='XBOX_SERIES'),(SELECT id_format_produit FROM format_produit WHERE code='PHYSIQUE'),(SELECT id_statut_produit FROM statut_produit WHERE code='NEUF'),    'Manette Xbox Wireless Robot White Neuf',   FALSE,FALSE,FALSE,FALSE),
((SELECT id_produit FROM produit WHERE slug='manette-xbox-wireless-robot-white'),  'MANETTE-XBOX-WIRELESS-ROBOT-WHITE-OCC',    (SELECT id_plateforme FROM plateforme WHERE code='XBOX_SERIES'),(SELECT id_format_produit FROM format_produit WHERE code='PHYSIQUE'),(SELECT id_statut_produit FROM statut_produit WHERE code='OCCASION'),'Manette Xbox Wireless Robot White Occasion',FALSE,FALSE,FALSE,FALSE),
((SELECT id_produit FROM produit WHERE slug='batterie-rechargeable-xbox'),         'BATTERIE-RECHARGEABLE-XBOX-NEUF',          (SELECT id_plateforme FROM plateforme WHERE code='XBOX_SERIES'),(SELECT id_format_produit FROM format_produit WHERE code='PHYSIQUE'),(SELECT id_statut_produit FROM statut_produit WHERE code='NEUF'),    'Batterie rechargeable Xbox Neuf',          FALSE,FALSE,FALSE,FALSE),
((SELECT id_produit FROM produit WHERE slug='batterie-rechargeable-xbox'),         'BATTERIE-RECHARGEABLE-XBOX-OCC',           (SELECT id_plateforme FROM plateforme WHERE code='XBOX_SERIES'),(SELECT id_format_produit FROM format_produit WHERE code='PHYSIQUE'),(SELECT id_statut_produit FROM statut_produit WHERE code='OCCASION'),'Batterie rechargeable Xbox Occasion',      FALSE,FALSE,FALSE,FALSE),
((SELECT id_produit FROM produit WHERE slug='station-de-charge-xbox'),             'STATION-DE-CHARGE-XBOX-NEUF',              (SELECT id_plateforme FROM plateforme WHERE code='XBOX_SERIES'),(SELECT id_format_produit FROM format_produit WHERE code='PHYSIQUE'),(SELECT id_statut_produit FROM statut_produit WHERE code='NEUF'),    'Station de charge Xbox Neuf',              FALSE,FALSE,FALSE,FALSE),
((SELECT id_produit FROM produit WHERE slug='station-de-charge-xbox'),             'STATION-DE-CHARGE-XBOX-OCC',               (SELECT id_plateforme FROM plateforme WHERE code='XBOX_SERIES'),(SELECT id_format_produit FROM format_produit WHERE code='PHYSIQUE'),(SELECT id_statut_produit FROM statut_produit WHERE code='OCCASION'),'Station de charge Xbox Occasion',          FALSE,FALSE,FALSE,FALSE),
((SELECT id_produit FROM produit WHERE slug='casque-sans-fil-xbox'),               'CASQUE-SANS-FIL-XBOX-NEUF',                (SELECT id_plateforme FROM plateforme WHERE code='XBOX_SERIES'),(SELECT id_format_produit FROM format_produit WHERE code='PHYSIQUE'),(SELECT id_statut_produit FROM statut_produit WHERE code='NEUF'),    'Casque sans fil Xbox Neuf',                FALSE,FALSE,FALSE,FALSE),
((SELECT id_produit FROM produit WHERE slug='casque-sans-fil-xbox'),               'CASQUE-SANS-FIL-XBOX-OCC',                 (SELECT id_plateforme FROM plateforme WHERE code='XBOX_SERIES'),(SELECT id_format_produit FROM format_produit WHERE code='PHYSIQUE'),(SELECT id_statut_produit FROM statut_produit WHERE code='OCCASION'),'Casque sans fil Xbox Occasion',            FALSE,FALSE,FALSE,FALSE),
((SELECT id_produit FROM produit WHERE slug='support-vertical-xbox-series-x'),    'SUPPORT-VERTICAL-XBOX-SERIES-X-NEUF',      (SELECT id_plateforme FROM plateforme WHERE code='XBOX_SERIES'),(SELECT id_format_produit FROM format_produit WHERE code='PHYSIQUE'),(SELECT id_statut_produit FROM statut_produit WHERE code='NEUF'),    'Support vertical Xbox Series X Neuf',      FALSE,FALSE,FALSE,FALSE),
((SELECT id_produit FROM produit WHERE slug='support-vertical-xbox-series-x'),    'SUPPORT-VERTICAL-XBOX-SERIES-X-OCC',       (SELECT id_plateforme FROM plateforme WHERE code='XBOX_SERIES'),(SELECT id_format_produit FROM format_produit WHERE code='PHYSIQUE'),(SELECT id_statut_produit FROM statut_produit WHERE code='OCCASION'),'Support vertical Xbox Series X Occasion',  FALSE,FALSE,FALSE,FALSE),
((SELECT id_produit FROM produit WHERE slug='carte-extension-stockage-xbox-1to'), 'CARTE-EXTENSION-STOCKAGE-XBOX-1TO-NEUF',   (SELECT id_plateforme FROM plateforme WHERE code='XBOX_SERIES'),(SELECT id_format_produit FROM format_produit WHERE code='PHYSIQUE'),(SELECT id_statut_produit FROM statut_produit WHERE code='NEUF'),    'Carte extension stockage Xbox 1To Neuf',   FALSE,FALSE,FALSE,FALSE),
((SELECT id_produit FROM produit WHERE slug='carte-extension-stockage-xbox-1to'), 'CARTE-EXTENSION-STOCKAGE-XBOX-1TO-OCC',    (SELECT id_plateforme FROM plateforme WHERE code='XBOX_SERIES'),(SELECT id_format_produit FROM format_produit WHERE code='PHYSIQUE'),(SELECT id_statut_produit FROM statut_produit WHERE code='OCCASION'),'Carte extension stockage Xbox 1To Occasion',FALSE,FALSE,FALSE,FALSE),
((SELECT id_produit FROM produit WHERE slug='manette-xbox-one-noire'),             'MANETTE-XBOX-ONE-NOIRE-NEUF',              (SELECT id_plateforme FROM plateforme WHERE code='XBOX_ONE'),   (SELECT id_format_produit FROM format_produit WHERE code='PHYSIQUE'),(SELECT id_statut_produit FROM statut_produit WHERE code='NEUF'),    'Manette Xbox One Noire Neuf',              FALSE,FALSE,FALSE,FALSE),
((SELECT id_produit FROM produit WHERE slug='manette-xbox-one-noire'),             'MANETTE-XBOX-ONE-NOIRE-OCC',               (SELECT id_plateforme FROM plateforme WHERE code='XBOX_ONE'),   (SELECT id_format_produit FROM format_produit WHERE code='PHYSIQUE'),(SELECT id_statut_produit FROM statut_produit WHERE code='OCCASION'),'Manette Xbox One Noire Occasion',          FALSE,FALSE,FALSE,FALSE),
((SELECT id_produit FROM produit WHERE slug='casque-stereo-xbox-one'),             'CASQUE-STEREO-XBOX-ONE-NEUF',              (SELECT id_plateforme FROM plateforme WHERE code='XBOX_ONE'),   (SELECT id_format_produit FROM format_produit WHERE code='PHYSIQUE'),(SELECT id_statut_produit FROM statut_produit WHERE code='NEUF'),    'Casque stereo Xbox One Neuf',              FALSE,FALSE,FALSE,FALSE),
((SELECT id_produit FROM produit WHERE slug='casque-stereo-xbox-one'),             'CASQUE-STEREO-XBOX-ONE-OCC',               (SELECT id_plateforme FROM plateforme WHERE code='XBOX_ONE'),   (SELECT id_format_produit FROM format_produit WHERE code='PHYSIQUE'),(SELECT id_statut_produit FROM statut_produit WHERE code='OCCASION'),'Casque stereo Xbox One Occasion',          FALSE,FALSE,FALSE,FALSE),
((SELECT id_produit FROM produit WHERE slug='dock-nintendo-switch'),               'DOCK-NINTENDO-SWITCH-NEUF',                (SELECT id_plateforme FROM plateforme WHERE code='SWITCH'),     (SELECT id_format_produit FROM format_produit WHERE code='PHYSIQUE'),(SELECT id_statut_produit FROM statut_produit WHERE code='NEUF'),    'Dock Nintendo Switch Neuf',                FALSE,FALSE,FALSE,FALSE),
((SELECT id_produit FROM produit WHERE slug='dock-nintendo-switch'),               'DOCK-NINTENDO-SWITCH-OCC',                 (SELECT id_plateforme FROM plateforme WHERE code='SWITCH'),     (SELECT id_format_produit FROM format_produit WHERE code='PHYSIQUE'),(SELECT id_statut_produit FROM statut_produit WHERE code='OCCASION'),'Dock Nintendo Switch Occasion',            FALSE,FALSE,FALSE,FALSE),
((SELECT id_produit FROM produit WHERE slug='paire-joy-con-bleu-rouge'),           'PAIRE-JOY-CON-BLEU-ROUGE-NEUF',            (SELECT id_plateforme FROM plateforme WHERE code='SWITCH'),     (SELECT id_format_produit FROM format_produit WHERE code='PHYSIQUE'),(SELECT id_statut_produit FROM statut_produit WHERE code='NEUF'),    'Paire Joy-Con Bleu Rouge Neuf',            FALSE,FALSE,FALSE,FALSE),
((SELECT id_produit FROM produit WHERE slug='paire-joy-con-bleu-rouge'),           'PAIRE-JOY-CON-BLEU-ROUGE-OCC',             (SELECT id_plateforme FROM plateforme WHERE code='SWITCH'),     (SELECT id_format_produit FROM format_produit WHERE code='PHYSIQUE'),(SELECT id_statut_produit FROM statut_produit WHERE code='OCCASION'),'Paire Joy-Con Bleu Rouge Occasion',        FALSE,FALSE,FALSE,FALSE),
((SELECT id_produit FROM produit WHERE slug='manette-pro-switch'),                 'MANETTE-PRO-SWITCH-NEUF',                  (SELECT id_plateforme FROM plateforme WHERE code='SWITCH'),     (SELECT id_format_produit FROM format_produit WHERE code='PHYSIQUE'),(SELECT id_statut_produit FROM statut_produit WHERE code='NEUF'),    'Manette Pro Switch Neuf',                  FALSE,FALSE,FALSE,FALSE),
((SELECT id_produit FROM produit WHERE slug='manette-pro-switch'),                 'MANETTE-PRO-SWITCH-OCC',                   (SELECT id_plateforme FROM plateforme WHERE code='SWITCH'),     (SELECT id_format_produit FROM format_produit WHERE code='PHYSIQUE'),(SELECT id_statut_produit FROM statut_produit WHERE code='OCCASION'),'Manette Pro Switch Occasion',              FALSE,FALSE,FALSE,FALSE),
((SELECT id_produit FROM produit WHERE slug='etui-de-transport-switch'),           'ETUI-DE-TRANSPORT-SWITCH-NEUF',            (SELECT id_plateforme FROM plateforme WHERE code='SWITCH'),     (SELECT id_format_produit FROM format_produit WHERE code='PHYSIQUE'),(SELECT id_statut_produit FROM statut_produit WHERE code='NEUF'),    'Etui de transport Switch Neuf',            FALSE,FALSE,FALSE,FALSE),
((SELECT id_produit FROM produit WHERE slug='etui-de-transport-switch'),           'ETUI-DE-TRANSPORT-SWITCH-OCC',             (SELECT id_plateforme FROM plateforme WHERE code='SWITCH'),     (SELECT id_format_produit FROM format_produit WHERE code='PHYSIQUE'),(SELECT id_statut_produit FROM statut_produit WHERE code='OCCASION'),'Etui de transport Switch Occasion',        FALSE,FALSE,FALSE,FALSE),
((SELECT id_produit FROM produit WHERE slug='chargeur-secteur-switch'),            'CHARGEUR-SECTEUR-SWITCH-NEUF',             (SELECT id_plateforme FROM plateforme WHERE code='SWITCH'),     (SELECT id_format_produit FROM format_produit WHERE code='PHYSIQUE'),(SELECT id_statut_produit FROM statut_produit WHERE code='NEUF'),    'Chargeur secteur Switch Neuf',             FALSE,FALSE,FALSE,FALSE),
((SELECT id_produit FROM produit WHERE slug='chargeur-secteur-switch'),            'CHARGEUR-SECTEUR-SWITCH-OCC',              (SELECT id_plateforme FROM plateforme WHERE code='SWITCH'),     (SELECT id_format_produit FROM format_produit WHERE code='PHYSIQUE'),(SELECT id_statut_produit FROM statut_produit WHERE code='OCCASION'),'Chargeur secteur Switch Occasion',         FALSE,FALSE,FALSE,FALSE),
((SELECT id_produit FROM produit WHERE slug='volant-joy-con-mario'),               'VOLANT-JOY-CON-MARIO-NEUF',                (SELECT id_plateforme FROM plateforme WHERE code='SWITCH'),     (SELECT id_format_produit FROM format_produit WHERE code='PHYSIQUE'),(SELECT id_statut_produit FROM statut_produit WHERE code='NEUF'),    'Volant Joy-Con Mario Neuf',                FALSE,FALSE,FALSE,FALSE),
((SELECT id_produit FROM produit WHERE slug='volant-joy-con-mario'),               'VOLANT-JOY-CON-MARIO-OCC',                 (SELECT id_plateforme FROM plateforme WHERE code='SWITCH'),     (SELECT id_format_produit FROM format_produit WHERE code='PHYSIQUE'),(SELECT id_statut_produit FROM statut_produit WHERE code='OCCASION'),'Volant Joy-Con Mario Occasion',            FALSE,FALSE,FALSE,FALSE),
((SELECT id_produit FROM produit WHERE slug='protection-ecran-switch-oled'),       'PROTECTION-ECRAN-SWITCH-OLED-NEUF',        (SELECT id_plateforme FROM plateforme WHERE code='SWITCH'),     (SELECT id_format_produit FROM format_produit WHERE code='PHYSIQUE'),(SELECT id_statut_produit FROM statut_produit WHERE code='NEUF'),    'Protection ecran Switch OLED Neuf',        FALSE,FALSE,FALSE,FALSE),
((SELECT id_produit FROM produit WHERE slug='protection-ecran-switch-oled'),       'PROTECTION-ECRAN-SWITCH-OLED-OCC',         (SELECT id_plateforme FROM plateforme WHERE code='SWITCH'),     (SELECT id_format_produit FROM format_produit WHERE code='PHYSIQUE'),(SELECT id_statut_produit FROM statut_produit WHERE code='OCCASION'),'Protection ecran Switch OLED Occasion',    FALSE,FALSE,FALSE,FALSE),
((SELECT id_produit FROM produit WHERE slug='etui-switch-lite'),                   'ETUI-SWITCH-LITE-NEUF',                    (SELECT id_plateforme FROM plateforme WHERE code='SWITCH'),     (SELECT id_format_produit FROM format_produit WHERE code='PHYSIQUE'),(SELECT id_statut_produit FROM statut_produit WHERE code='NEUF'),    'Etui Switch Lite Neuf',                    FALSE,FALSE,FALSE,FALSE),
((SELECT id_produit FROM produit WHERE slug='etui-switch-lite'),                   'ETUI-SWITCH-LITE-OCC',                     (SELECT id_plateforme FROM plateforme WHERE code='SWITCH'),     (SELECT id_format_produit FROM format_produit WHERE code='PHYSIQUE'),(SELECT id_statut_produit FROM statut_produit WHERE code='OCCASION'),'Etui Switch Lite Occasion',                FALSE,FALSE,FALSE,FALSE),
((SELECT id_produit FROM produit WHERE slug='paire-joy-con-2-switch-2'),           'PAIRE-JOY-CON-2-SWITCH-2-NEUF',            (SELECT id_plateforme FROM plateforme WHERE code='SWITCH2'),   (SELECT id_format_produit FROM format_produit WHERE code='PHYSIQUE'),(SELECT id_statut_produit FROM statut_produit WHERE code='NEUF'),    'Paire Joy-Con 2 Switch 2 Neuf',            FALSE,FALSE,FALSE,FALSE),
((SELECT id_produit FROM produit WHERE slug='paire-joy-con-2-switch-2'),           'PAIRE-JOY-CON-2-SWITCH-2-OCC',             (SELECT id_plateforme FROM plateforme WHERE code='SWITCH2'),   (SELECT id_format_produit FROM format_produit WHERE code='PHYSIQUE'),(SELECT id_statut_produit FROM statut_produit WHERE code='OCCASION'),'Paire Joy-Con 2 Switch 2 Occasion',        FALSE,FALSE,FALSE,FALSE),
((SELECT id_produit FROM produit WHERE slug='manette-pro-switch-2'),               'MANETTE-PRO-SWITCH-2-NEUF',                (SELECT id_plateforme FROM plateforme WHERE code='SWITCH2'),   (SELECT id_format_produit FROM format_produit WHERE code='PHYSIQUE'),(SELECT id_statut_produit FROM statut_produit WHERE code='NEUF'),    'Manette Pro Switch 2 Neuf',                FALSE,FALSE,FALSE,FALSE),
((SELECT id_produit FROM produit WHERE slug='manette-pro-switch-2'),               'MANETTE-PRO-SWITCH-2-OCC',                 (SELECT id_plateforme FROM plateforme WHERE code='SWITCH2'),   (SELECT id_format_produit FROM format_produit WHERE code='PHYSIQUE'),(SELECT id_statut_produit FROM statut_produit WHERE code='OCCASION'),'Manette Pro Switch 2 Occasion',            FALSE,FALSE,FALSE,FALSE),
((SELECT id_produit FROM produit WHERE slug='camera-nintendo-switch-2'),           'CAMERA-NINTENDO-SWITCH-2-NEUF',            (SELECT id_plateforme FROM plateforme WHERE code='SWITCH2'),   (SELECT id_format_produit FROM format_produit WHERE code='PHYSIQUE'),(SELECT id_statut_produit FROM statut_produit WHERE code='NEUF'),    'Camera Nintendo Switch 2 Neuf',            FALSE,FALSE,FALSE,FALSE),
((SELECT id_produit FROM produit WHERE slug='camera-nintendo-switch-2'),           'CAMERA-NINTENDO-SWITCH-2-OCC',             (SELECT id_plateforme FROM plateforme WHERE code='SWITCH2'),   (SELECT id_format_produit FROM format_produit WHERE code='PHYSIQUE'),(SELECT id_statut_produit FROM statut_produit WHERE code='OCCASION'),'Camera Nintendo Switch 2 Occasion',        FALSE,FALSE,FALSE,FALSE);

-- ------------------------------------------------------------
-- EXTENSIONS POKEMON (TCG)
-- ------------------------------------------------------------

INSERT INTO tcg_extension (id_tcg_jeu, code, nom, date_sortie) VALUES
((SELECT id_tcg_jeu FROM tcg_jeu WHERE code='POKEMON'), 'EV01', 'Ecarlate et Violet',  '2023-03-31'),
((SELECT id_tcg_jeu FROM tcg_jeu WHERE code='POKEMON'), 'EV03', 'Flammes Obsidiennes', '2023-08-11'),
((SELECT id_tcg_jeu FROM tcg_jeu WHERE code='POKEMON'), 'EV04', 'Paradoxe Rift',       '2023-11-03'),
((SELECT id_tcg_jeu FROM tcg_jeu WHERE code='POKEMON'), 'EV05', 'Forces Temporelles',  '2024-03-22'),
((SELECT id_tcg_jeu FROM tcg_jeu WHERE code='POKEMON'), 'EV06', 'Fable Nebuleuse',     '2024-08-02');

-- ------------------------------------------------------------
-- CARTES UNITAIRES POKEMON
-- ------------------------------------------------------------

INSERT INTO tcg_carte_reference (id_tcg_extension, nom_carte, numero_carte, rarete) VALUES
((SELECT id_tcg_extension FROM tcg_extension WHERE code='EV01'), 'Miraidon EX',     '081', 'Ultra Rare'),
((SELECT id_tcg_extension FROM tcg_extension WHERE code='EV01'), 'Koraidon EX',     '125', 'Ultra Rare'),
((SELECT id_tcg_extension FROM tcg_extension WHERE code='EV03'), 'Dracaufeu EX',    '223', 'Secret Rare'),
((SELECT id_tcg_extension FROM tcg_extension WHERE code='EV03'), 'Pikachu',         '065', 'Rare'),
((SELECT id_tcg_extension FROM tcg_extension WHERE code='EV04'), 'Iron Valiant EX', '124', 'Ultra Rare'),
((SELECT id_tcg_extension FROM tcg_extension WHERE code='EV04'), 'Roaring Moon EX', '251', 'Secret Rare'),
((SELECT id_tcg_extension FROM tcg_extension WHERE code='EV05'), 'Gouging Fire EX', '213', 'Ultra Rare'),
((SELECT id_tcg_extension FROM tcg_extension WHERE code='EV05'), 'Iron Crown EX',   '191', 'Ultra Rare'),
((SELECT id_tcg_extension FROM tcg_extension WHERE code='EV06'), 'Pecharunt EX',    '045', 'Ultra Rare'),
((SELECT id_tcg_extension FROM tcg_extension WHERE code='EV06'), 'Ogerpon EX',      '067', 'Ultra Rare');

-- ------------------------------------------------------------
-- INVENTAIRE CARTES TCG PAR MAGASIN
-- ------------------------------------------------------------

INSERT INTO tcg_carte_inventaire (id_tcg_carte_reference, id_variant, id_magasin, id_etat_carte_tcg, langue, foil, reverse_foil, alternate_art, prix_vente, disponible) VALUES
((SELECT id_tcg_carte_reference FROM tcg_carte_reference WHERE nom_carte='Miraidon EX'),     NULL, 1, (SELECT id_etat_carte_tcg FROM etat_carte_tcg WHERE code='NM'), 'FR', TRUE, FALSE, FALSE, 12.90, TRUE),
((SELECT id_tcg_carte_reference FROM tcg_carte_reference WHERE nom_carte='Koraidon EX'),     NULL, 1, (SELECT id_etat_carte_tcg FROM etat_carte_tcg WHERE code='NM'), 'FR', TRUE, FALSE, FALSE, 11.50, TRUE),
((SELECT id_tcg_carte_reference FROM tcg_carte_reference WHERE nom_carte='Dracaufeu EX'),    NULL, 2, (SELECT id_etat_carte_tcg FROM etat_carte_tcg WHERE code='EX'), 'FR', TRUE, FALSE, TRUE,  89.90, TRUE),
((SELECT id_tcg_carte_reference FROM tcg_carte_reference WHERE nom_carte='Pikachu'),         NULL, 2, (SELECT id_etat_carte_tcg FROM etat_carte_tcg WHERE code='NM'), 'FR', FALSE,FALSE, FALSE,  2.50, TRUE),
((SELECT id_tcg_carte_reference FROM tcg_carte_reference WHERE nom_carte='Iron Valiant EX'), NULL, 3, (SELECT id_etat_carte_tcg FROM etat_carte_tcg WHERE code='NM'), 'EN', TRUE, FALSE, FALSE, 15.90, TRUE),
((SELECT id_tcg_carte_reference FROM tcg_carte_reference WHERE nom_carte='Roaring Moon EX'), NULL, 3, (SELECT id_etat_carte_tcg FROM etat_carte_tcg WHERE code='GD'), 'EN', TRUE, FALSE, TRUE,  25.00, TRUE),
((SELECT id_tcg_carte_reference FROM tcg_carte_reference WHERE nom_carte='Gouging Fire EX'), NULL, 1, (SELECT id_etat_carte_tcg FROM etat_carte_tcg WHERE code='NM'), 'FR', TRUE, FALSE, FALSE, 19.90, TRUE),
((SELECT id_tcg_carte_reference FROM tcg_carte_reference WHERE nom_carte='Iron Crown EX'),   NULL, 1, (SELECT id_etat_carte_tcg FROM etat_carte_tcg WHERE code='NM'), 'FR', TRUE, FALSE, FALSE, 17.50, TRUE),
((SELECT id_tcg_carte_reference FROM tcg_carte_reference WHERE nom_carte='Pecharunt EX'),    NULL, 2, (SELECT id_etat_carte_tcg FROM etat_carte_tcg WHERE code='EX'), 'FR', TRUE, FALSE, FALSE, 14.90, TRUE),
((SELECT id_tcg_carte_reference FROM tcg_carte_reference WHERE nom_carte='Ogerpon EX'),      NULL, 2, (SELECT id_etat_carte_tcg FROM etat_carte_tcg WHERE code='NM'), 'FR', TRUE, FALSE, FALSE, 13.90, TRUE);

-- ------------------------------------------------------------
-- REPRISES CLIENTS
-- ------------------------------------------------------------

INSERT INTO reprise (reference_reprise, id_client, id_employe, id_magasin, id_statut_reprise, id_mode_compensation_reprise, montant_total_estime, montant_total_valide) VALUES
('REP-0001', 1, 1, 1, (SELECT id_statut_reprise FROM statut_reprise WHERE code='ESTIMEE'),  (SELECT id_mode_compensation_reprise FROM mode_compensation_reprise WHERE code='BON_ACHAT'), 45.00,  0.00),
('REP-0002', 2, 3, 2, (SELECT id_statut_reprise FROM statut_reprise WHERE code='VALIDEE'),  (SELECT id_mode_compensation_reprise FROM mode_compensation_reprise WHERE code='ESPECES'),   60.00, 55.00),
('REP-0003', 3, 4, 2, (SELECT id_statut_reprise FROM statut_reprise WHERE code='BROUILLON'),(SELECT id_mode_compensation_reprise FROM mode_compensation_reprise WHERE code='BON_ACHAT'), 25.00,  0.00),
('REP-0004', 4, 5, 3, (SELECT id_statut_reprise FROM statut_reprise WHERE code='PAYEE'),    (SELECT id_mode_compensation_reprise FROM mode_compensation_reprise WHERE code='ESPECES'),   80.00, 80.00);

INSERT INTO reprise_ligne (id_reprise, id_variant, quantite, prix_estime_unitaire, prix_valide_unitaire) VALUES
(1, (SELECT id_variant FROM produit_variant WHERE sku LIKE 'MARVEL-SPIDER-MAN%-NEUF'   LIMIT 1), 1, 15.00, NULL),
(1, (SELECT id_variant FROM produit_variant WHERE sku LIKE 'GOD-OF-WAR%-NEUF'          LIMIT 1), 1, 30.00, NULL),
(2, (SELECT id_variant FROM produit_variant WHERE sku LIKE 'MARIO-KART-8-DELUXE%-NEUF' LIMIT 1), 1, 25.00, 20.00),
(2, (SELECT id_variant FROM produit_variant WHERE sku LIKE 'SUPER-MARIO-ODYSSEY%-NEUF' LIMIT 1), 1, 35.00, 35.00),
(4, (SELECT id_variant FROM produit_variant WHERE sku LIKE 'HALO-INFINITE%-NEUF'       LIMIT 1), 1, 40.00, 40.00),
(4, (SELECT id_variant FROM produit_variant WHERE sku LIKE 'FORZA-HORIZON-5%-NEUF'     LIMIT 1), 1, 40.00, 40.00);

-- ------------------------------------------------------------
-- COMMANDES + FACTURES
-- ------------------------------------------------------------

INSERT INTO commande (reference_commande, id_client, id_statut_commande, id_mode_livraison, id_canal_vente, id_entrepot_expedition, id_magasin_retrait, sous_total, montant_remise, frais_livraison, montant_total) VALUES
('CMD-1001', 1, (SELECT id_statut_commande FROM statut_commande WHERE code='PAYEE'),    (SELECT id_mode_livraison FROM mode_livraison WHERE code='DOMICILE'),       (SELECT id_canal_vente FROM canal_vente WHERE code='WEB'), 1,    NULL,  69.99,  0.00, 4.99,  74.98),
('CMD-1002', 2, (SELECT id_statut_commande FROM statut_commande WHERE code='PAYEE'),    (SELECT id_mode_livraison FROM mode_livraison WHERE code='RETRAIT_MAGASIN'),(SELECT id_canal_vente FROM canal_vente WHERE code='WEB'), NULL, 2,     59.99,  0.00, 0.00,  59.99),
('CMD-1003', 4, (SELECT id_statut_commande FROM statut_commande WHERE code='EXPEDIEE'), (SELECT id_mode_livraison FROM mode_livraison WHERE code='POINT_RELAIS'),   (SELECT id_canal_vente FROM canal_vente WHERE code='WEB'), 1,    NULL, 129.99, 10.00, 5.99, 124.98);

INSERT INTO ligne_commande (id_commande, id_variant, quantite, prix_unitaire, montant_ligne) VALUES
(1, (SELECT id_variant FROM produit_variant WHERE nom_commercial LIKE 'Marvel Spider-Man%'   LIMIT 1), 1, 69.99, 69.99),
(2, (SELECT id_variant FROM produit_variant WHERE nom_commercial LIKE 'Mario Kart 8 Deluxe%' LIMIT 1), 1, 59.99, 59.99),
(3, (SELECT id_variant FROM produit_variant WHERE nom_commercial LIKE 'Halo Infinite%'        LIMIT 1), 1, 59.99, 59.99),
(3, (SELECT id_variant FROM produit_variant WHERE nom_commercial LIKE 'Forza Horizon 5%'      LIMIT 1), 1, 69.99, 69.99);

INSERT INTO facture (reference_facture, id_commande, id_client, id_magasin, id_mode_paiement, id_statut_facture, id_contexte_vente, montant_total, montant_remise, montant_final) VALUES
('FAC-1001', 1, 1, 1, (SELECT id_mode_paiement FROM mode_paiement WHERE code='CB'),    (SELECT id_statut_facture FROM statut_facture WHERE code='EMISE'), (SELECT id_contexte_vente FROM contexte_vente WHERE code='EN_LIGNE'),  69.99,  0.00,  69.99),
('FAC-1002', 2, 2, 2, (SELECT id_mode_paiement FROM mode_paiement WHERE code='PAYPAL'),(SELECT id_statut_facture FROM statut_facture WHERE code='EMISE'), (SELECT id_contexte_vente FROM contexte_vente WHERE code='EN_LIGNE'),  59.99,  0.00,  59.99),
('FAC-1003', 3, 4, 3, (SELECT id_mode_paiement FROM mode_paiement WHERE code='CB'),    (SELECT id_statut_facture FROM statut_facture WHERE code='EMISE'), (SELECT id_contexte_vente FROM contexte_vente WHERE code='EN_LIGNE'), 129.99, 10.00, 119.99);

INSERT INTO ligne_facture (id_facture, id_variant, quantite, prix_unitaire, montant_ligne) VALUES
(1, (SELECT id_variant FROM produit_variant WHERE nom_commercial LIKE 'Marvel Spider-Man%'   LIMIT 1), 1, 69.99, 69.99),
(2, (SELECT id_variant FROM produit_variant WHERE nom_commercial LIKE 'Mario Kart 8 Deluxe%' LIMIT 1), 1, 59.99, 59.99),
(3, (SELECT id_variant FROM produit_variant WHERE nom_commercial LIKE 'Halo Infinite%'        LIMIT 1), 1, 59.99, 59.99),
(3, (SELECT id_variant FROM produit_variant WHERE nom_commercial LIKE 'Forza Horizon 5%'      LIMIT 1), 1, 69.99, 69.99);

-- ------------------------------------------------------------
-- STOCK PAR MAGASIN ET ENTREPOT
-- ------------------------------------------------------------

INSERT INTO stock_magasin (id_magasin, id_variant, quantite_neuf, quantite_occasion, quantite_reprise, quantite_reservee)
SELECT 1, id_variant, 10, 2, 1, 0 FROM produit_variant;

INSERT INTO stock_magasin (id_magasin, id_variant, quantite_neuf, quantite_occasion, quantite_reprise, quantite_reservee)
SELECT 2, id_variant, 6,  1, 0, 0 FROM produit_variant;

INSERT INTO stock_magasin (id_magasin, id_variant, quantite_neuf, quantite_occasion, quantite_reprise, quantite_reservee)
SELECT 3, id_variant, 8,  3, 1, 0 FROM produit_variant;

INSERT INTO stock_entrepot (id_entrepot, id_variant, quantite_neuf, quantite_reservee)
SELECT 1, id_variant, 50, 0 FROM produit_variant;

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
