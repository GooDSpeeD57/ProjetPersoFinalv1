-- ══════════════════════════════════════════════════════════════════════════════
-- MIGRATION V18 — Initialisation des prix de reprise dans produit_prix
-- Contexte : V17 a déplacé prix_reprise de produit_variant → produit_prix
--            mais a mis toutes les valeurs à NULL.
-- Ce script calcule des prix de reprise de base à partir du prix neuf,
-- selon le type de catégorie. L'admin peut ensuite affiner via le back-office.
-- ══════════════════════════════════════════════════════════════════════════════

USE micromania;

SET SQL_SAFE_UPDATES = 0;

-- ── Jeux ────────────────────────────────────────────────────────────────────
-- Reprise ≈ 40 % du prix neuf
UPDATE produit_prix pp
JOIN produit_variant pv ON pv.id_variant    = pp.id_variant
JOIN produit p          ON p.id_produit     = pv.id_produit
JOIN categorie c        ON c.id_categorie   = p.id_categorie
JOIN type_categorie tc  ON tc.id_type_categorie = c.id_type_categorie
SET pp.prix_reprise = ROUND(pp.prix_neuf * 0.40, 2)
WHERE pp.prix_reprise IS NULL
  AND pp.prix_neuf    IS NOT NULL
  AND pp.actif        = TRUE
  AND tc.code         = 'JEU';

-- ── Consoles ─────────────────────────────────────────────────────────────────
-- Reprise ≈ 35 % du prix neuf
UPDATE produit_prix pp
JOIN produit_variant pv ON pv.id_variant    = pp.id_variant
JOIN produit p          ON p.id_produit     = pv.id_produit
JOIN categorie c        ON c.id_categorie   = p.id_categorie
JOIN type_categorie tc  ON tc.id_type_categorie = c.id_type_categorie
SET pp.prix_reprise = ROUND(pp.prix_neuf * 0.35, 2)
WHERE pp.prix_reprise IS NULL
  AND pp.prix_neuf    IS NOT NULL
  AND pp.actif        = TRUE
  AND tc.code         = 'CONSOLE';

-- ── Accessoires ──────────────────────────────────────────────────────────────
-- Reprise ≈ 25 % du prix neuf
UPDATE produit_prix pp
JOIN produit_variant pv ON pv.id_variant    = pp.id_variant
JOIN produit p          ON p.id_produit     = pv.id_produit
JOIN categorie c        ON c.id_categorie   = p.id_categorie
JOIN type_categorie tc  ON tc.id_type_categorie = c.id_type_categorie
SET pp.prix_reprise = ROUND(pp.prix_neuf * 0.25, 2)
WHERE pp.prix_reprise IS NULL
  AND pp.prix_neuf    IS NOT NULL
  AND pp.actif        = TRUE
  AND tc.code         = 'ACCESSOIRE';

-- ── TCG ──────────────────────────────────────────────────────────────────────
-- Reprise ≈ 50 % (marché secondaire actif)
UPDATE produit_prix pp
JOIN produit_variant pv ON pv.id_variant    = pp.id_variant
JOIN produit p          ON p.id_produit     = pv.id_produit
JOIN categorie c        ON c.id_categorie   = p.id_categorie
JOIN type_categorie tc  ON tc.id_type_categorie = c.id_type_categorie
SET pp.prix_reprise = ROUND(pp.prix_neuf * 0.50, 2)
WHERE pp.prix_reprise IS NULL
  AND pp.prix_neuf    IS NOT NULL
  AND pp.actif        = TRUE
  AND tc.code         = 'TCG';

-- ── Autres catégories (Goodies, etc.) ────────────────────────────────────────
-- Reprise ≈ 20 %
UPDATE produit_prix pp
JOIN produit_variant pv ON pv.id_variant    = pp.id_variant
JOIN produit p          ON p.id_produit     = pv.id_produit
JOIN categorie c        ON c.id_categorie   = p.id_categorie
JOIN type_categorie tc  ON tc.id_type_categorie = c.id_type_categorie
SET pp.prix_reprise = ROUND(pp.prix_neuf * 0.20, 2)
WHERE pp.prix_reprise IS NULL
  AND pp.prix_neuf    IS NOT NULL
  AND pp.actif        = TRUE;

SET SQL_SAFE_UPDATES = 1;

-- ══════════════════════════════════════════════════════════════════════════════
-- VÉRIFICATION
-- SELECT pp.id_prix, pv.sku, tc.code, pp.prix_neuf, pp.prix_reprise
-- FROM produit_prix pp
-- JOIN produit_variant pv ON pv.id_variant = pp.id_variant
-- JOIN produit p ON p.id_produit = pv.id_produit
-- JOIN categorie c ON c.id_categorie = p.id_categorie
-- JOIN type_categorie tc ON tc.id_type_categorie = c.id_type_categorie
-- WHERE pp.actif = TRUE
-- ORDER BY tc.code
-- LIMIT 30;
-- ══════════════════════════════════════════════════════════════════════════════
