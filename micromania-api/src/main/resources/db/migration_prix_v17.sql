-- ══════════════════════════════════════════════════════════════════════════════
-- MIGRATION V17 — Refactoring produit_prix (script idempotent)
-- État actuel supposé :
--   • produit_prix  : prix_neuf/occasion/reprise/location déjà ajoutées (NULL)
--                     id_canal_vente et prix déjà supprimés
--   • produit_variant : prix_reprise peut encore exister ou non
--   • v_prix_actuel   : peut encore exister ou non
-- ══════════════════════════════════════════════════════════════════════════════

USE micromania;

SET FOREIGN_KEY_CHECKS = 0;
SET SQL_SAFE_UPDATES   = 0;

-- ══════════════════════════════════════════════════════════════════════════════
-- ÉTAPE 1 — Vue (DROP IF EXISTS = toujours safe)
-- ══════════════════════════════════════════════════════════════════════════════
DROP VIEW IF EXISTS v_prix_actuel;

-- ══════════════════════════════════════════════════════════════════════════════
-- ÉTAPE 2 — Ajouter les colonnes si elles n'existent pas encore
--           (remplacées par des no-op si déjà présentes grâce à la procédure)
-- ══════════════════════════════════════════════════════════════════════════════
DROP PROCEDURE IF EXISTS ajout_colonnes_prix;
DELIMITER $$
CREATE PROCEDURE ajout_colonnes_prix()
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.COLUMNS
        WHERE TABLE_SCHEMA = DATABASE()
          AND TABLE_NAME   = 'produit_prix'
          AND COLUMN_NAME  = 'prix_neuf'
    ) THEN
        ALTER TABLE produit_prix ADD COLUMN prix_neuf     DECIMAL(10,2) NULL AFTER id_variant;
        ALTER TABLE produit_prix ADD COLUMN prix_occasion DECIMAL(10,2) NULL AFTER prix_neuf;
        ALTER TABLE produit_prix ADD COLUMN prix_reprise  DECIMAL(10,2) NULL AFTER prix_occasion;
        ALTER TABLE produit_prix ADD COLUMN prix_location DECIMAL(10,2) NULL AFTER prix_reprise;
    END IF;
END$$
DELIMITER ;
CALL ajout_colonnes_prix();
DROP PROCEDURE IF EXISTS ajout_colonnes_prix;

-- ══════════════════════════════════════════════════════════════════════════════
-- ÉTAPE 3 — Supprimer les colonnes obsolètes si elles existent encore
--           (id_canal_vente, prix sur produit_prix)
-- ══════════════════════════════════════════════════════════════════════════════
DROP PROCEDURE IF EXISTS suppr_colonnes_obsoletes;
DELIMITER $$
CREATE PROCEDURE suppr_colonnes_obsoletes()
BEGIN
    -- FK canal (peut déjà être absente)
    IF EXISTS (
        SELECT 1 FROM information_schema.TABLE_CONSTRAINTS
        WHERE TABLE_SCHEMA    = DATABASE()
          AND TABLE_NAME      = 'produit_prix'
          AND CONSTRAINT_NAME = 'fk_produit_prix_canal'
    ) THEN
        ALTER TABLE produit_prix DROP FOREIGN KEY fk_produit_prix_canal;
    END IF;

    -- INDEX canal
    IF EXISTS (
        SELECT 1 FROM information_schema.STATISTICS
        WHERE TABLE_SCHEMA = DATABASE()
          AND TABLE_NAME   = 'produit_prix'
          AND INDEX_NAME   = 'idx_prix_canal'
    ) THEN
        ALTER TABLE produit_prix DROP INDEX idx_prix_canal;
    END IF;

    -- Colonnes id_canal_vente et prix
    IF EXISTS (
        SELECT 1 FROM information_schema.COLUMNS
        WHERE TABLE_SCHEMA = DATABASE()
          AND TABLE_NAME   = 'produit_prix'
          AND COLUMN_NAME  = 'id_canal_vente'
    ) THEN
        ALTER TABLE produit_prix DROP COLUMN id_canal_vente;
    END IF;

    IF EXISTS (
        SELECT 1 FROM information_schema.COLUMNS
        WHERE TABLE_SCHEMA = DATABASE()
          AND TABLE_NAME   = 'produit_prix'
          AND COLUMN_NAME  = 'prix'
    ) THEN
        ALTER TABLE produit_prix DROP COLUMN prix;
    END IF;
END$$
DELIMITER ;
CALL suppr_colonnes_obsoletes();
DROP PROCEDURE IF EXISTS suppr_colonnes_obsoletes;

-- ══════════════════════════════════════════════════════════════════════════════
-- ÉTAPE 4 — Supprimer les doublons (garder 1 ligne par variant)
--           S'il reste plusieurs lignes par variant, on en garde la plus récente
-- ══════════════════════════════════════════════════════════════════════════════
DELETE pp1 FROM produit_prix pp1
INNER JOIN produit_prix pp2
    ON pp1.id_variant = pp2.id_variant
   AND pp1.id_prix    < pp2.id_prix;

-- ══════════════════════════════════════════════════════════════════════════════
-- ÉTAPE 5 — Remplir les prix depuis les données métier
--           (recalcul complet, sans dépendance à canal_vente)
-- ══════════════════════════════════════════════════════════════════════════════
UPDATE produit_prix pp
JOIN produit_variant pv ON pv.id_variant   = pp.id_variant
JOIN produit p          ON p.id_produit    = pv.id_produit
JOIN categorie c        ON c.id_categorie  = p.id_categorie
JOIN type_categorie tc  ON tc.id_type_categorie = c.id_type_categorie
JOIN statut_produit sp  ON sp.id_statut_produit = pv.id_statut_produit
LEFT JOIN plateforme pf ON pf.id_plateforme = pv.id_plateforme
SET
  pp.prix_neuf = CASE WHEN sp.code IN ('NEUF','PRECOMMANDE') THEN
    CASE
      WHEN tc.code = 'JEU' THEN
        CASE pf.code
          WHEN 'SWITCH2'                          THEN 79.99
          WHEN 'PS5'                              THEN 69.99
          WHEN 'XBOX_SERIES'                      THEN 69.99
          WHEN 'SWITCH'                           THEN 59.99
          WHEN 'PS4'                              THEN 49.99
          WHEN 'XBOX_ONE'                         THEN 49.99
          ELSE 59.99
        END
      WHEN tc.code = 'CONSOLE' THEN
        CASE p.slug
          WHEN 'playstation-5-pro-2-to'           THEN 799.99
          WHEN 'playstation-5-slim-standard'      THEN 549.99
          WHEN 'nintendo-switch-2-standard'       THEN 469.99
          WHEN 'xbox-series-x-1-to'               THEN 549.99
          WHEN 'xbox-series-s-1-to'               THEN 349.99
          WHEN 'nintendo-switch-oled-blanche'     THEN 349.99
          WHEN 'nintendo-switch-standard'         THEN 299.99
          WHEN 'nintendo-switch-lite-turquoise'   THEN 229.99
          WHEN 'playstation-4-pro-1-to'           THEN 299.99
          WHEN 'playstation-4-500-go'             THEN 219.99
          ELSE 299.99
        END
      WHEN tc.code = 'ACCESSOIRE' THEN
        CASE
          WHEN p.slug LIKE '%carte-extension-stockage%'   THEN 199.99
          WHEN p.slug LIKE '%casque%'                     THEN 99.99
          WHEN p.slug LIKE '%manette-pro-switch%'         THEN 74.99
          WHEN p.slug LIKE '%manette-dualsense%'          THEN 74.99
          WHEN p.slug LIKE '%manette-xbox-wireless%'      THEN 64.99
          WHEN p.slug LIKE '%manette-xbox-one%'           THEN 54.99
          WHEN p.slug LIKE '%manette-ps4%'                THEN 59.99
          WHEN p.slug LIKE '%joy-con%'                    THEN 79.99
          WHEN p.slug LIKE '%dock-nintendo-switch%'       THEN 89.99
          WHEN p.slug LIKE '%station-de-recharge%'
            OR p.slug LIKE '%station-de-charge%'          THEN 29.99
          WHEN p.slug LIKE '%camera%'                     THEN 59.99
          WHEN p.slug LIKE '%telecommande%'               THEN 29.99
          WHEN p.slug LIKE '%batterie%'                   THEN 24.99
          WHEN p.slug LIKE '%support-vertical%'           THEN 24.99
          WHEN p.slug LIKE '%disque-dur%'                 THEN 89.99
          WHEN p.slug LIKE '%etui%'
            OR p.slug LIKE '%protection%'                 THEN 19.99
          ELSE 39.99
        END
      WHEN tc.code = 'GOODIES'      THEN 19.99
      WHEN tc.code = 'CARTE_CADEAU' THEN 20.00
      WHEN tc.code = 'TCG' THEN
        CASE
          WHEN pv.est_tcg_unitaire = TRUE THEN 1.99
          WHEN pv.scelle = TRUE           THEN 6.99
          ELSE 4.99
        END
      ELSE 19.99
    END
  ELSE NULL END,

  pp.prix_occasion = CASE WHEN sp.code = 'OCCASION' THEN
    CASE
      WHEN tc.code = 'JEU' THEN
        CASE pf.code
          WHEN 'SWITCH2'                          THEN 59.99
          WHEN 'PS5'                              THEN 49.99
          WHEN 'XBOX_SERIES'                      THEN 49.99
          WHEN 'SWITCH'                           THEN 44.99
          WHEN 'PS4'                              THEN 29.99
          WHEN 'XBOX_ONE'                         THEN 29.99
          ELSE 39.99
        END
      WHEN tc.code = 'CONSOLE' THEN
        CASE p.slug
          WHEN 'playstation-5-pro-2-to'           THEN 649.99
          WHEN 'playstation-5-slim-standard'      THEN 449.99
          WHEN 'nintendo-switch-2-standard'       THEN 399.99
          WHEN 'xbox-series-x-1-to'               THEN 449.99
          WHEN 'xbox-series-s-1-to'               THEN 279.99
          WHEN 'nintendo-switch-oled-blanche'     THEN 279.99
          WHEN 'nintendo-switch-standard'         THEN 229.99
          WHEN 'nintendo-switch-lite-turquoise'   THEN 169.99
          WHEN 'playstation-4-pro-1-to'           THEN 229.99
          WHEN 'playstation-4-500-go'             THEN 159.99
          ELSE 199.99
        END
      WHEN tc.code = 'ACCESSOIRE' THEN
        CASE
          WHEN p.slug LIKE '%carte-extension-stockage%'   THEN 149.99
          WHEN p.slug LIKE '%casque%'                     THEN 69.99
          WHEN p.slug LIKE '%manette-pro-switch%'         THEN 54.99
          WHEN p.slug LIKE '%manette-dualsense%'          THEN 54.99
          WHEN p.slug LIKE '%manette-xbox-wireless%'      THEN 44.99
          WHEN p.slug LIKE '%manette-xbox-one%'           THEN 39.99
          WHEN p.slug LIKE '%manette-ps4%'                THEN 39.99
          WHEN p.slug LIKE '%joy-con%'                    THEN 59.99
          WHEN p.slug LIKE '%dock-nintendo-switch%'       THEN 59.99
          WHEN p.slug LIKE '%station-de-recharge%'
            OR p.slug LIKE '%station-de-charge%'          THEN 19.99
          WHEN p.slug LIKE '%camera%'                     THEN 39.99
          WHEN p.slug LIKE '%telecommande%'               THEN 19.99
          WHEN p.slug LIKE '%batterie%'                   THEN 14.99
          WHEN p.slug LIKE '%support-vertical%'           THEN 14.99
          WHEN p.slug LIKE '%disque-dur%'                 THEN 59.99
          WHEN p.slug LIKE '%etui%'
            OR p.slug LIKE '%protection%'                 THEN 9.99
          ELSE 24.99
        END
      WHEN tc.code = 'GOODIES' THEN 9.99
      WHEN tc.code = 'TCG' THEN
        CASE
          WHEN pv.est_tcg_unitaire = TRUE THEN 0.99
          WHEN pv.scelle = TRUE           THEN 4.99
          ELSE 2.99
        END
      ELSE 14.99
    END
  ELSE NULL END,

  -- Pas de barème fixe : le prix de reprise est évalué manuellement par l'employé.
  -- La règle -10% espèces/avoir est gérée côté application (RepriseServiceImpl).
  pp.prix_reprise = NULL;

-- ══════════════════════════════════════════════════════════════════════════════
-- ÉTAPE 6 — Insérer une ligne prix pour les variants qui n'en ont pas
-- ══════════════════════════════════════════════════════════════════════════════
INSERT INTO produit_prix (id_variant, prix_neuf, prix_occasion, prix_reprise, prix_location, date_debut, actif)
SELECT pv.id_variant, NULL, NULL, NULL, NULL, NOW(), TRUE
FROM produit_variant pv
WHERE NOT EXISTS (
    SELECT 1 FROM produit_prix pp WHERE pp.id_variant = pv.id_variant
);

-- ══════════════════════════════════════════════════════════════════════════════
-- ÉTAPE 7 — Supprimer prix_reprise de produit_variant si elle existe encore
-- ══════════════════════════════════════════════════════════════════════════════
DROP PROCEDURE IF EXISTS suppr_prix_reprise_variant;
DELIMITER $$
CREATE PROCEDURE suppr_prix_reprise_variant()
BEGIN
    IF EXISTS (
        SELECT 1 FROM information_schema.COLUMNS
        WHERE TABLE_SCHEMA = DATABASE()
          AND TABLE_NAME   = 'produit_variant'
          AND COLUMN_NAME  = 'prix_reprise'
    ) THEN
        ALTER TABLE produit_variant DROP COLUMN prix_reprise;
    END IF;
END$$
DELIMITER ;
CALL suppr_prix_reprise_variant();
DROP PROCEDURE IF EXISTS suppr_prix_reprise_variant;

-- ══════════════════════════════════════════════════════════════════════════════
-- ÉTAPE 8 — Recréer la vue
-- ══════════════════════════════════════════════════════════════════════════════
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

-- ══════════════════════════════════════════════════════════════════════════════
SET FOREIGN_KEY_CHECKS = 1;
SET SQL_SAFE_UPDATES   = 1;
-- ══════════════════════════════════════════════════════════════════════════════
-- VÉRIFICATION
-- SELECT id_variant, prix_neuf, prix_occasion, prix_reprise FROM produit_prix LIMIT 20;
-- DESCRIBE produit_prix;
-- ══════════════════════════════════════════════════════════════════════════════
