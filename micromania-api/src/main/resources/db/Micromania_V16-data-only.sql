-- ============================================================
-- Micromania_V16-data.sql
-- ============================================================

USE Micromania;
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

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
('Martin',  'Lucas', 'lucas.martin@micromania.fr',   '0600000001', '$2a$10$LF6JZvQ7EYSAKY6F7lmGP.RGzWBtWv2RItg3ULT1w0BlQ1zOINx7q', 1, 1),
('Durand',  'Emma',  'emma.durand@micromania.fr',    '0600000002', '$2a$10$LF6JZvQ7EYSAKY6F7lmGP.RGzWBtWv2RItg3ULT1w0BlQ1zOINx7q', 1, 1),
('Bernard', 'Nathan','nathan.bernard@micromania.fr', '0600000003', '$2a$10$LF6JZvQ7EYSAKY6F7lmGP.RGzWBtWv2RItg3ULT1w0BlQ1zOINx7q', 2, 2),
('Petit',   'Chloe', 'chloe.petit@micromania.fr',    '0600000004', '$2a$10$LF6JZvQ7EYSAKY6F7lmGP.RGzWBtWv2RItg3ULT1w0BlQ1zOINx7q', 2, 2),
('Moreau',  'Hugo',  'hugo.moreau@micromania.fr',    '0600000005', '$2a$10$LF6JZvQ7EYSAKY6F7lmGP.RGzWBtWv2RItg3ULT1w0BlQ1zOINx7q', 3, 3);

-- ------------------------------------------------------------
-- CLIENTS
-- ------------------------------------------------------------

INSERT INTO client (
    pseudo, nom, prenom, date_naissance, email, telephone, mot_de_passe,
    id_type_fidelite, id_avatar, email_verifie, date_verification_email,
    token_verification_email, token_verification_expire_le,
    telephone_verifie, date_verification_telephone,
    token_verification_telephone, token_verification_telephone_expire_le,
    compte_active, cree_par_employe, id_employe_createur,
    doit_definir_mot_de_passe, date_derniere_connexion
) VALUES
('gamermax',      'Dupont', 'Maxime', '1995-04-12', 'maxime.dupont@email.fr', '0610000001', '$2a$10$LF6JZvQ7EYSAKY6F7lmGP.RGzWBtWv2RItg3ULT1w0BlQ1zOINx7q', 1, 1, TRUE,  NOW(), NULL,                          NULL,                              FALSE, NULL, NULL, NULL, TRUE,  FALSE, NULL, FALSE, NULL),
('nintendofan',   'Leroy',  'Julie',  '1998-09-21', 'julie.leroy@email.fr',   '0610000002', '$2a$10$LF6JZvQ7EYSAKY6F7lmGP.RGzWBtWv2RItg3ULT1w0BlQ1zOINx7q', 1, 2, TRUE,  NOW(), NULL,                          NULL,                              FALSE, NULL, NULL, NULL, TRUE,  FALSE, NULL, FALSE, NULL),
('retroplayer',   'Garcia', 'Thomas', '1990-02-05', 'thomas.garcia@email.fr', '0610000003', '$2a$10$LF6JZvQ7EYSAKY6F7lmGP.RGzWBtWv2RItg3ULT1w0BlQ1zOINx7q', 1, 1, FALSE, NULL,  'verify-retroplayer-token',    DATE_ADD(NOW(), INTERVAL 24 HOUR), FALSE, NULL, NULL, NULL, FALSE, FALSE, NULL, TRUE,  NULL),
('cardmaster',    'Roux',   'Alex',   '1997-07-11', 'alex.roux@email.fr',     '0610000004', '$2a$10$LF6JZvQ7EYSAKY6F7lmGP.RGzWBtWv2RItg3ULT1w0BlQ1zOINx7q', 1, 3, TRUE,  NOW(), NULL,                          NULL,                              FALSE, NULL, NULL, NULL, TRUE,  FALSE, NULL, FALSE, NULL),
('consoleking',   'Faure',  'Leo',    '2000-12-01', 'leo.faure@email.fr',     '0610000005', '$2a$10$LF6JZvQ7EYSAKY6F7lmGP.RGzWBtWv2RItg3ULT1w0BlQ1zOINx7q', 1, 1, FALSE, NULL,  'verify-consoleking-token',    DATE_ADD(NOW(), INTERVAL 24 HOUR), FALSE, NULL, NULL, NULL, FALSE, FALSE, NULL, TRUE,  NULL),
('clientmagasin', 'Martin', 'Paul',   '1990-01-01', 'paul.martin@email.fr',   '0610000006', '$2a$10$LF6JZvQ7EYSAKY6F7lmGP.RGzWBtWv2RItg3ULT1w0BlQ1zOINx7q', 1, 1, FALSE, NULL,  'verify-clientmagasin-token',  DATE_ADD(NOW(), INTERVAL 24 HOUR), FALSE, NULL, NULL, NULL, FALSE, TRUE,  1,    TRUE,  NULL);

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
       CONCAT('http://localhost:8080/images/catalogue/jeux/', slug, '.jpg'),
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
SELECT id_produit, CONCAT('http://localhost:8080/images/catalogue/consoles/', slug, '.jpg'), CONCAT('Visuel ', nom), TRUE, 1
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
SELECT id_produit, CONCAT('http://localhost:8080/images/catalogue/accessoires/', slug, '.jpg'), CONCAT('Visuel ', nom), TRUE, 1
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


-- ------------------------------------------------------------
-- PRIX CATALOGUE (WEB / MOBILE / MAGASIN)
-- ------------------------------------------------------------

INSERT INTO produit_prix (id_variant, id_canal_vente, prix, date_debut, date_fin, actif)
SELECT
    pv.id_variant,
    cv.id_canal_vente,
    CASE
        WHEN tc.code = 'JEU' THEN
            CASE
                WHEN sp.code = 'NEUF' THEN
                    CASE
                        WHEN pf.code = 'SWITCH2' THEN 79.99
                        WHEN pf.code IN ('PS5', 'XBOX_SERIES') THEN 69.99
                        WHEN pf.code = 'SWITCH' THEN 59.99
                        WHEN pf.code IN ('PS4', 'XBOX_ONE') THEN 49.99
                        ELSE 59.99
                    END
                WHEN sp.code = 'OCCASION' THEN
                    CASE
                        WHEN pf.code = 'SWITCH2' THEN 59.99
                        WHEN pf.code IN ('PS5', 'XBOX_SERIES') THEN 49.99
                        WHEN pf.code = 'SWITCH' THEN 44.99
                        WHEN pf.code IN ('PS4', 'XBOX_ONE') THEN 29.99
                        ELSE 39.99
                    END
                ELSE 29.99
            END

        WHEN tc.code = 'CONSOLE' THEN
            CASE
                WHEN sp.code = 'NEUF' THEN
                    CASE
                        WHEN p.slug = 'playstation-5-pro-2-to' THEN 799.99
                        WHEN p.slug = 'playstation-5-slim-standard' THEN 549.99
                        WHEN p.slug = 'nintendo-switch-2-standard' THEN 469.99
                        WHEN p.slug = 'xbox-series-x-1-to' THEN 549.99
                        WHEN p.slug = 'xbox-series-s-1-to' THEN 349.99
                        WHEN p.slug = 'nintendo-switch-oled-blanche' THEN 349.99
                        WHEN p.slug = 'nintendo-switch-standard' THEN 299.99
                        WHEN p.slug = 'nintendo-switch-lite-turquoise' THEN 229.99
                        WHEN p.slug = 'playstation-4-pro-1-to' THEN 299.99
                        WHEN p.slug = 'playstation-4-500-go' THEN 219.99
                        ELSE 299.99
                    END
                WHEN sp.code = 'OCCASION' THEN
                    CASE
                        WHEN p.slug = 'playstation-5-pro-2-to' THEN 649.99
                        WHEN p.slug = 'playstation-5-slim-standard' THEN 449.99
                        WHEN p.slug = 'nintendo-switch-2-standard' THEN 399.99
                        WHEN p.slug = 'xbox-series-x-1-to' THEN 449.99
                        WHEN p.slug = 'xbox-series-s-1-to' THEN 279.99
                        WHEN p.slug = 'nintendo-switch-oled-blanche' THEN 279.99
                        WHEN p.slug = 'nintendo-switch-standard' THEN 229.99
                        WHEN p.slug = 'nintendo-switch-lite-turquoise' THEN 169.99
                        WHEN p.slug = 'playstation-4-pro-1-to' THEN 229.99
                        WHEN p.slug = 'playstation-4-500-go' THEN 159.99
                        ELSE 199.99
                    END
                ELSE 149.99
            END

        WHEN tc.code = 'ACCESSOIRE' THEN
            CASE
                WHEN sp.code = 'NEUF' THEN
                    CASE
                        WHEN p.slug LIKE '%carte-extension-stockage%' THEN 199.99
                        WHEN p.slug LIKE '%casque%' THEN 99.99
                        WHEN p.slug LIKE '%manette-pro-switch%' THEN 74.99
                        WHEN p.slug LIKE '%manette-dualsense%' THEN 74.99
                        WHEN p.slug LIKE '%manette-xbox-wireless%' THEN 64.99
                        WHEN p.slug LIKE '%manette-xbox-one%' THEN 54.99
                        WHEN p.slug LIKE '%manette-ps4%' THEN 59.99
                        WHEN p.slug LIKE '%joy-con%' THEN 79.99
                        WHEN p.slug LIKE '%dock-nintendo-switch%' THEN 89.99
                        WHEN p.slug LIKE '%station-de-recharge%' OR p.slug LIKE '%station-de-charge%' THEN 29.99
                        WHEN p.slug LIKE '%camera%' THEN 59.99
                        WHEN p.slug LIKE '%telecommande%' THEN 29.99
                        WHEN p.slug LIKE '%batterie%' THEN 24.99
                        WHEN p.slug LIKE '%support-vertical%' THEN 24.99
                        WHEN p.slug LIKE '%disque-dur%' THEN 89.99
                        WHEN p.slug LIKE '%etui%' OR p.slug LIKE '%protection%' THEN 19.99
                        ELSE 39.99
                    END
                WHEN sp.code = 'OCCASION' THEN
                    CASE
                        WHEN p.slug LIKE '%carte-extension-stockage%' THEN 149.99
                        WHEN p.slug LIKE '%casque%' THEN 69.99
                        WHEN p.slug LIKE '%manette-pro-switch%' THEN 54.99
                        WHEN p.slug LIKE '%manette-dualsense%' THEN 54.99
                        WHEN p.slug LIKE '%manette-xbox-wireless%' THEN 44.99
                        WHEN p.slug LIKE '%manette-xbox-one%' THEN 39.99
                        WHEN p.slug LIKE '%manette-ps4%' THEN 39.99
                        WHEN p.slug LIKE '%joy-con%' THEN 59.99
                        WHEN p.slug LIKE '%dock-nintendo-switch%' THEN 59.99
                        WHEN p.slug LIKE '%station-de-recharge%' OR p.slug LIKE '%station-de-charge%' THEN 19.99
                        WHEN p.slug LIKE '%camera%' THEN 39.99
                        WHEN p.slug LIKE '%telecommande%' THEN 19.99
                        WHEN p.slug LIKE '%batterie%' THEN 14.99
                        WHEN p.slug LIKE '%support-vertical%' THEN 14.99
                        WHEN p.slug LIKE '%disque-dur%' THEN 59.99
                        WHEN p.slug LIKE '%etui%' OR p.slug LIKE '%protection%' THEN 9.99
                        ELSE 24.99
                    END
                ELSE 19.99
            END

        WHEN tc.code = 'GOODIES' THEN
            CASE WHEN sp.code = 'OCCASION' THEN 9.99 ELSE 19.99 END

        WHEN tc.code = 'CARTE_CADEAU' THEN 20.00

        WHEN tc.code = 'TCG' THEN
            CASE
                WHEN pv.est_tcg_unitaire = TRUE THEN
                    CASE WHEN sp.code = 'OCCASION' THEN 0.99 ELSE 1.99 END
                WHEN pv.scelle = TRUE THEN
                    CASE WHEN sp.code = 'OCCASION' THEN 4.99 ELSE 6.99 END
                ELSE
                    CASE WHEN sp.code = 'OCCASION' THEN 2.99 ELSE 4.99 END
            END

        ELSE 19.99
    END AS prix,
    NOW(),
    NULL,
    TRUE
FROM produit_variant pv
JOIN produit p ON p.id_produit = pv.id_produit
JOIN categorie c ON c.id_categorie = p.id_categorie
JOIN type_categorie tc ON tc.id_type_categorie = c.id_type_categorie
JOIN statut_produit sp ON sp.id_statut_produit = pv.id_statut_produit
LEFT JOIN plateforme pf ON pf.id_plateforme = pv.id_plateforme
JOIN canal_vente cv ON cv.code IN ('WEB', 'MOBILE', 'MAGASIN')
WHERE NOT EXISTS (
    SELECT 1
    FROM produit_prix pp
    WHERE pp.id_variant = pv.id_variant
      AND pp.id_canal_vente = cv.id_canal_vente
);

INSERT INTO commande (reference_commande, id_client, id_statut_commande, id_mode_livraison, id_canal_vente, id_entrepot_expedition, id_magasin_retrait, sous_total, montant_remise, frais_livraison, montant_total) VALUES
('CMD-1001', 1, (SELECT id_statut_commande FROM statut_commande WHERE code='PAYEE'),    (SELECT id_mode_livraison FROM mode_livraison WHERE code='DOMICILE'),       (SELECT id_canal_vente FROM canal_vente WHERE code='WEB'), 1,    NULL,  69.99,  0.00, 4.99,  74.98),
('CMD-1002', 2, (SELECT id_statut_commande FROM statut_commande WHERE code='PAYEE'),    (SELECT id_mode_livraison FROM mode_livraison WHERE code='RETRAIT_MAGASIN'),(SELECT id_canal_vente FROM canal_vente WHERE code='WEB'), NULL, 2,     59.99,  0.00, 0.00,  59.99),
('CMD-1003', 4, (SELECT id_statut_commande FROM statut_commande WHERE code='EXPEDIEE'), (SELECT id_mode_livraison FROM mode_livraison WHERE code='POINT_RELAIS'),   (SELECT id_canal_vente FROM canal_vente WHERE code='WEB'), 1,    NULL, 129.99, 10.00, 5.99, 124.98);

INSERT INTO ligne_commande (id_commande, id_variant, quantite, prix_unitaire) VALUES
(1, (SELECT id_variant FROM produit_variant WHERE nom_commercial LIKE 'Marvel Spider-Man%'   LIMIT 1), 1, 69.99),
(2, (SELECT id_variant FROM produit_variant WHERE nom_commercial LIKE 'Mario Kart 8 Deluxe%' LIMIT 1), 1, 59.99),
(3, (SELECT id_variant FROM produit_variant WHERE nom_commercial LIKE 'Halo Infinite%'        LIMIT 1), 1, 59.99),
(3, (SELECT id_variant FROM produit_variant WHERE nom_commercial LIKE 'Forza Horizon 5%'      LIMIT 1), 1, 69.99);

INSERT INTO facture (reference_facture, id_commande, id_client, id_magasin, id_mode_paiement, id_statut_facture, id_contexte_vente, montant_total, montant_remise, montant_final) VALUES
('FAC-1001', 1, 1, 1, (SELECT id_mode_paiement FROM mode_paiement WHERE code='CB'),    (SELECT id_statut_facture FROM statut_facture WHERE code='EMISE'), (SELECT id_contexte_vente FROM contexte_vente WHERE code='EN_LIGNE'),  69.99,  0.00,  69.99),
('FAC-1002', 2, 2, 2, (SELECT id_mode_paiement FROM mode_paiement WHERE code='PAYPAL'),(SELECT id_statut_facture FROM statut_facture WHERE code='EMISE'), (SELECT id_contexte_vente FROM contexte_vente WHERE code='EN_LIGNE'),  59.99,  0.00,  59.99),
('FAC-1003', 3, 4, 3, (SELECT id_mode_paiement FROM mode_paiement WHERE code='CB'),    (SELECT id_statut_facture FROM statut_facture WHERE code='EMISE'), (SELECT id_contexte_vente FROM contexte_vente WHERE code='EN_LIGNE'), 129.99, 10.00, 119.99);

INSERT INTO ligne_facture (id_facture, id_variant, quantite, prix_unitaire) VALUES
(1, (SELECT id_variant FROM produit_variant WHERE nom_commercial LIKE 'Marvel Spider-Man%'   LIMIT 1), 1, 69.99),
(2, (SELECT id_variant FROM produit_variant WHERE nom_commercial LIKE 'Mario Kart 8 Deluxe%' LIMIT 1), 1, 59.99),
(3, (SELECT id_variant FROM produit_variant WHERE nom_commercial LIKE 'Halo Infinite%'        LIMIT 1), 1, 59.99),
(3, (SELECT id_variant FROM produit_variant WHERE nom_commercial LIKE 'Forza Horizon 5%'      LIMIT 1), 1, 69.99);

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

SET FOREIGN_KEY_CHECKS = 1;
