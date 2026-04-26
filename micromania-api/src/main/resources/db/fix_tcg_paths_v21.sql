-- ══════════════════════════════════════════════════════════════════════════════
-- FIX V21 — Correction chemins TCG dans produit_image
-- Nouvelle structure :
--   /images/catalogue/tcg/pokemon/    (logos téléchargés localement)
--   /images/catalogue/tcg/one-piece/
--   /images/catalogue/tcg/yugioh/
--   /images/catalogue/tcg/lorcana/
--   /images/catalogue/tcg/accessoire/
-- ══════════════════════════════════════════════════════════════════════════════

USE micromania;

-- ─────────────────────────────────────────────────────────────────────────────
-- 1. POKÉMON — URLs externes → fichiers locaux
--    Boosters simples
-- ─────────────────────────────────────────────────────────────────────────────
-- id 109 : Booster Couronne Stellaire    (sv7)
UPDATE produit_image SET url = '/images/catalogue/tcg/pokemon/sv7-logo.png'    WHERE id_image = 109;
-- id 110 : Booster Étincelles Déferlantes (sv8)
UPDATE produit_image SET url = '/images/catalogue/tcg/pokemon/sv8-logo.png'    WHERE id_image = 110;
-- id 111 : Booster Évolutions Prismatiques (sv8pt5)
UPDATE produit_image SET url = '/images/catalogue/tcg/pokemon/sv8pt5-logo.png' WHERE id_image = 111;
-- id 112 : Booster Unis par le Destin (sv9)
UPDATE produit_image SET url = '/images/catalogue/tcg/pokemon/sv9-logo.png'    WHERE id_image = 112;

-- Displays
-- id 113 : Display Couronne Stellaire (sv7)
UPDATE produit_image SET url = '/images/catalogue/tcg/pokemon/sv7-logo.png'    WHERE id_image = 113;
-- id 114 : Display Étincelles Déferlantes (sv8)
UPDATE produit_image SET url = '/images/catalogue/tcg/pokemon/sv8-logo.png'    WHERE id_image = 114;
-- id 115 : Display Évolutions Prismatiques (sv8pt5)
UPDATE produit_image SET url = '/images/catalogue/tcg/pokemon/sv8pt5-logo.png' WHERE id_image = 115;
-- id 116 : Display Unis par le Destin (sv9)
UPDATE produit_image SET url = '/images/catalogue/tcg/pokemon/sv9-logo.png'    WHERE id_image = 116;

-- Coffrets
-- id 117 : Coffret Dresseur Elite Couronne Stellaire (sv7)
UPDATE produit_image SET url = '/images/catalogue/tcg/pokemon/sv7-logo.png'    WHERE id_image = 117;
-- id 118 : Coffret Dresseur Elite Évolutions Prismatiques (sv8pt5)
UPDATE produit_image SET url = '/images/catalogue/tcg/pokemon/sv8pt5-logo.png' WHERE id_image = 118;
-- id 119 : Coffret Pokémon 151 Dracaufeu ex (sv3pt5)
UPDATE produit_image SET url = '/images/catalogue/tcg/pokemon/sv3pt5-logo.png' WHERE id_image = 119;

-- ─────────────────────────────────────────────────────────────────────────────
-- 2. ONE PIECE
-- ─────────────────────────────────────────────────────────────────────────────
UPDATE produit_image
SET url = REPLACE(url, '/images/catalogue/tcg/', '/images/catalogue/tcg/one-piece/')
WHERE id_image IN (120, 121, 122, 123, 124);

-- ─────────────────────────────────────────────────────────────────────────────
-- 3. YU-GI-OH
-- ─────────────────────────────────────────────────────────────────────────────
UPDATE produit_image
SET url = REPLACE(url, '/images/catalogue/tcg/', '/images/catalogue/tcg/yugioh/')
WHERE id_image IN (125, 126, 127);

-- ─────────────────────────────────────────────────────────────────────────────
-- 4. LORCANA
-- ─────────────────────────────────────────────────────────────────────────────
UPDATE produit_image
SET url = REPLACE(url, '/images/catalogue/tcg/', '/images/catalogue/tcg/lorcana/')
WHERE id_image IN (128, 129, 130);

-- ─────────────────────────────────────────────────────────────────────────────
-- 5. ACCESSOIRES TCG (Dragon Shield, Ultra Pro, BCW, Playmat)
-- ─────────────────────────────────────────────────────────────────────────────
UPDATE produit_image
SET url = REPLACE(url, '/images/catalogue/tcg/', '/images/catalogue/tcg/accessoire/')
WHERE id_image IN (131, 132, 133, 134, 135, 136, 137, 138);

-- ─────────────────────────────────────────────────────────────────────────────
-- Vérification
-- ─────────────────────────────────────────────────────────────────────────────
-- SELECT id_image, url FROM produit_image WHERE id_image BETWEEN 109 AND 138 ORDER BY id_image;
