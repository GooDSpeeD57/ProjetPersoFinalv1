-- ══════════════════════════════════════════════════════════════════════════════
-- FIX V20 — Correction des chemins produit_image
-- Nouvelle structure : /images/catalogue/{type}/{plateforme}/{fichier}
--   type      : jeu | console | accessoire | tcg
--   plateforme: ps4 | ps5 | xbox-one | xbox-series | switch | switch-2 | switch-oled | switch-lite | pc
-- ══════════════════════════════════════════════════════════════════════════════

USE micromania;

-- ─────────────────────────────────────────────────────────────────────────────
-- 1. JEUX PS4 (id 1-5 déjà OK)
--    id 6-10 : /images/catalogue/jeux/ps4/ → /images/catalogue/jeu/ps4/
-- ─────────────────────────────────────────────────────────────────────────────
UPDATE produit_image
SET url = REPLACE(url, '/images/catalogue/jeux/ps4/', '/images/catalogue/jeu/ps4/')
WHERE id_image IN (6, 7, 8, 9, 10);

-- ─────────────────────────────────────────────────────────────────────────────
-- 2. JEUX PS5 (id 11-20)
--    /images/catalogue/jeux/ps5/ → /images/catalogue/jeu/ps5/
-- ─────────────────────────────────────────────────────────────────────────────
UPDATE produit_image
SET url = REPLACE(url, '/images/catalogue/jeux/ps5/', '/images/catalogue/jeu/ps5/')
WHERE id_image IN (11, 12, 13, 14, 15, 16, 17, 18, 19, 20);

-- ─────────────────────────────────────────────────────────────────────────────
-- 3. JEUX XBOX ONE (id 21-30)
--    /images/catalogue/jeux/ → /images/catalogue/jeu/xbox-one/
-- ─────────────────────────────────────────────────────────────────────────────
UPDATE produit_image
SET url = REPLACE(url, '/images/catalogue/jeux/', '/images/catalogue/jeu/xbox-one/')
WHERE id_image IN (21, 22, 23, 24, 25, 26, 27, 28, 29, 30);

-- ─────────────────────────────────────────────────────────────────────────────
-- 4. JEUX XBOX SERIES (id 31-40)
--    /images/catalogue/jeux/ → /images/catalogue/jeu/xbox-series/
-- ─────────────────────────────────────────────────────────────────────────────
UPDATE produit_image
SET url = REPLACE(url, '/images/catalogue/jeux/', '/images/catalogue/jeu/xbox-series/')
WHERE id_image IN (31, 32, 33, 34, 35, 36, 37, 38, 39, 40);

-- ─────────────────────────────────────────────────────────────────────────────
-- 5. JEUX SWITCH (id 41-50)
--    /images/catalogue/jeux/ → /images/catalogue/jeu/switch/
-- ─────────────────────────────────────────────────────────────────────────────
UPDATE produit_image
SET url = REPLACE(url, '/images/catalogue/jeux/', '/images/catalogue/jeu/switch/')
WHERE id_image IN (41, 42, 43, 44, 45, 46, 47, 48, 49, 50);

-- ─────────────────────────────────────────────────────────────────────────────
-- 6. JEUX SWITCH 2 (id 51-60)
--    /images/catalogue/jeux/ → /images/catalogue/jeu/switch-2/
-- ─────────────────────────────────────────────────────────────────────────────
UPDATE produit_image
SET url = REPLACE(url, '/images/catalogue/jeux/', '/images/catalogue/jeu/switch-2/')
WHERE id_image IN (51, 52, 53, 54, 55, 56, 57, 58, 59, 60);

-- ─────────────────────────────────────────────────────────────────────────────
-- 7. JEUX RÉCENTS PRAGMATA (id 140-143)
--    /images/catalogue/jeux/ → plateforme selon variant
-- ─────────────────────────────────────────────────────────────────────────────
UPDATE produit_image SET url = '/images/catalogue/jeu/ps5/PRAGMATA-PS5.jpg'           WHERE id_image = 140;
UPDATE produit_image SET url = '/images/catalogue/jeu/xbox-series/PRAGMATA-Xbox.jpg'  WHERE id_image = 141;
UPDATE produit_image SET url = '/images/catalogue/jeu/switch-2/PRAGMATASwitch2.jpg'   WHERE id_image = 142;
UPDATE produit_image SET url = '/images/catalogue/jeu/pc/PRAGMATApc.jpg'              WHERE id_image = 143;

-- ─────────────────────────────────────────────────────────────────────────────
-- 8. CONSOLES
--    /images/catalogue/consoles/ → /images/catalogue/console/{plateforme}/
-- ─────────────────────────────────────────────────────────────────────────────
-- PS4
UPDATE produit_image SET url = '/images/catalogue/console/ps4/playstation-4-500-go.jpg'      WHERE id_image = 64;
UPDATE produit_image SET url = '/images/catalogue/console/ps4/playstation-4-pro-1-to.jpg'    WHERE id_image = 65;
-- PS5
UPDATE produit_image SET url = '/images/catalogue/console/ps5/playstation-5-slim-standard.jpg' WHERE id_image = 66;
UPDATE produit_image SET url = '/images/catalogue/console/ps5/playstation-5-pro-2-to.jpg'      WHERE id_image = 67;
-- Switch
UPDATE produit_image SET url = '/images/catalogue/console/switch/nintendo-switch-standard.jpg'       WHERE id_image = 68;
UPDATE produit_image SET url = '/images/catalogue/console/switch/nintendo-switch-oled-blanche.jpg'   WHERE id_image = 69;
UPDATE produit_image SET url = '/images/catalogue/console/switch/nintendo-switch-lite-turquoise.jpg' WHERE id_image = 70;
-- Switch 2
UPDATE produit_image SET url = '/images/catalogue/console/switch-2/nintendo-switch-2-standard.jpg'  WHERE id_image = 71;
-- Xbox Series
UPDATE produit_image SET url = '/images/catalogue/console/xbox-series/xbox-series-s-1-to.jpg'       WHERE id_image = 72;
UPDATE produit_image SET url = '/images/catalogue/console/xbox-series/xbox-series-x-1-to.jpg'       WHERE id_image = 73;

-- ─────────────────────────────────────────────────────────────────────────────
-- 9. ACCESSOIRES
--    /images/catalogue/accessoires/ → /images/catalogue/accessoire/{plateforme}/
-- ─────────────────────────────────────────────────────────────────────────────
-- PS5
UPDATE produit_image SET url = '/images/catalogue/accessoire/ps5/manette-dualsense-blanche.jpg'          WHERE id_image = 79;
UPDATE produit_image SET url = '/images/catalogue/accessoire/ps5/manette-dualsense-midnight-black.jpg'   WHERE id_image = 80;
UPDATE produit_image SET url = '/images/catalogue/accessoire/ps5/station-de-recharge-dualsense.jpg'      WHERE id_image = 81;
UPDATE produit_image SET url = '/images/catalogue/accessoire/ps5/casque-pulse-3d.jpg'                    WHERE id_image = 82;
UPDATE produit_image SET url = '/images/catalogue/accessoire/ps5/camera-hd-ps5.jpg'                      WHERE id_image = 83;
UPDATE produit_image SET url = '/images/catalogue/accessoire/ps5/telecommande-multimedia-ps5.jpg'         WHERE id_image = 84;
-- PS4
UPDATE produit_image SET url = '/images/catalogue/accessoire/ps4/manette-ps4-v2-noire.jpg'               WHERE id_image = 85;
UPDATE produit_image SET url = '/images/catalogue/accessoire/ps4/station-de-recharge-manettes-ps4.jpg'   WHERE id_image = 86;
UPDATE produit_image SET url = '/images/catalogue/accessoire/ps4/casque-filaire-gaming-ps4.jpg'           WHERE id_image = 87;
UPDATE produit_image SET url = '/images/catalogue/accessoire/ps4/disque-dur-externe-ps4-2to.jpg'          WHERE id_image = 88;
-- Xbox Series
UPDATE produit_image SET url = '/images/catalogue/accessoire/xbox-series/manette-xbox-wireless-carbon-black.jpg' WHERE id_image = 89;
UPDATE produit_image SET url = '/images/catalogue/accessoire/xbox-series/manette-xbox-wireless-robot-white.jpg'  WHERE id_image = 90;
UPDATE produit_image SET url = '/images/catalogue/accessoire/xbox-series/batterie-rechargeable-xbox.jpg'         WHERE id_image = 91;
UPDATE produit_image SET url = '/images/catalogue/accessoire/xbox-series/station-de-charge-xbox.jpg'             WHERE id_image = 92;
UPDATE produit_image SET url = '/images/catalogue/accessoire/xbox-series/casque-sans-fil-xbox.jpg'               WHERE id_image = 93;
UPDATE produit_image SET url = '/images/catalogue/accessoire/xbox-series/support-vertical-xbox-series-x.jpg'     WHERE id_image = 94;
UPDATE produit_image SET url = '/images/catalogue/accessoire/xbox-series/carte-extension-stockage-xbox-1to.jpg'  WHERE id_image = 95;
-- Xbox One
UPDATE produit_image SET url = '/images/catalogue/accessoire/xbox-one/manette-xbox-one-noire.jpg'        WHERE id_image = 96;
UPDATE produit_image SET url = '/images/catalogue/accessoire/xbox-one/casque-stereo-xbox-one.jpg'         WHERE id_image = 97;
-- Switch
UPDATE produit_image SET url = '/images/catalogue/accessoire/switch/dock-nintendo-switch.jpg'             WHERE id_image = 98;
UPDATE produit_image SET url = '/images/catalogue/accessoire/switch/paire-joy-con-bleu-rouge.jpg'         WHERE id_image = 99;
UPDATE produit_image SET url = '/images/catalogue/accessoire/switch/manette-pro-switch.jpg'               WHERE id_image = 100;
UPDATE produit_image SET url = '/images/catalogue/accessoire/switch/etui-de-transport-switch.jpg'         WHERE id_image = 101;
UPDATE produit_image SET url = '/images/catalogue/accessoire/switch/chargeur-secteur-switch.jpg'          WHERE id_image = 102;
UPDATE produit_image SET url = '/images/catalogue/accessoire/switch/volant-joy-con-mario.jpg'             WHERE id_image = 103;
-- Switch OLED
UPDATE produit_image SET url = '/images/catalogue/accessoire/switch-oled/protection-ecran-switch-oled.jpg' WHERE id_image = 104;
-- Switch Lite
UPDATE produit_image SET url = '/images/catalogue/accessoire/switch-lite/etui-switch-lite.jpg'            WHERE id_image = 105;
-- Switch 2
UPDATE produit_image SET url = '/images/catalogue/accessoire/switch-2/paire-joy-con-2-switch-2.jpg'       WHERE id_image = 106;
UPDATE produit_image SET url = '/images/catalogue/accessoire/switch-2/manette-pro-switch-2.jpg'           WHERE id_image = 107;
UPDATE produit_image SET url = '/images/catalogue/accessoire/switch-2/camera-nintendo-switch-2.jpg'       WHERE id_image = 108;

-- ─────────────────────────────────────────────────────────────────────────────
-- TCG (id 109-138) : URLs externes pokemontcg.io → intactes
--                    Fichiers locaux /images/catalogue/tcg/ → déjà corrects
-- ─────────────────────────────────────────────────────────────────────────────
-- Rien à faire pour le TCG.

-- ══════════════════════════════════════════════════════════════════════════════
-- Vérification rapide
-- ══════════════════════════════════════════════════════════════════════════════
-- SELECT id_image, id_variant, url FROM produit_image ORDER BY id_image;
