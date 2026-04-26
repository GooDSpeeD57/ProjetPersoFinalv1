-- ══════════════════════════════════════════════════════════════════════════════
-- CLEANUP — Suppression des lignes produit_prix en doublon
-- Contexte : le PUT /variants/{id} créait une nouvelle ligne à chaque
--            mise à jour au lieu de modifier la ligne existante.
--            Ce script supprime toutes les lignes inactives (actif = 0)
--            ET, pour les variants qui auraient plusieurs lignes actives,
--            ne conserve que la plus récente (date_debut MAX).
-- À exécuter UNE SEULE FOIS, puis le bug est corrigé côté API.
-- ══════════════════════════════════════════════════════════════════════════════

USE micromania;

-- ── Étape 1 : supprimer toutes les lignes inactives ─────────────────────────
DELETE FROM produit_prix
WHERE actif = 0;

-- ── Étape 2 : si plusieurs lignes actives pour un même variant,
--             ne garder que celle avec la date_debut la plus récente ─────────
DELETE pp
FROM produit_prix pp
INNER JOIN (
    SELECT id_variant, MAX(date_debut) AS max_date
    FROM produit_prix
    WHERE actif = 1
    GROUP BY id_variant
    HAVING COUNT(*) > 1          -- seulement les variants avec doublons actifs
) keep_ref ON pp.id_variant = keep_ref.id_variant
WHERE pp.actif = 1
  AND pp.date_debut < keep_ref.max_date;

-- ── Vérification ──────────────────────────────────────────────────────────────
-- SELECT id_variant, COUNT(*) nb
-- FROM produit_prix
-- GROUP BY id_variant
-- HAVING nb > 1;
-- → doit retourner 0 lignes
-- ══════════════════════════════════════════════════════════════════════════════
