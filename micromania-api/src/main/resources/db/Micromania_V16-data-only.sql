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

-- ------------------------------------------------------------
-- MAGASINS COMPLEMENTAIRES (314 ajoutes pour atteindre 317 magasins)
-- ------------------------------------------------------------

INSERT INTO magasin (nom, telephone, email) VALUES
('Micromania Toulouse Centre', '0440000003', 'magasin004@micromania.local'),
('Micromania Nice Centre', '0540000004', 'magasin005@micromania.local'),
('Micromania Nantes Centre', '0140000005', 'magasin006@micromania.local'),
('Micromania Strasbourg Centre', '0240000006', 'magasin007@micromania.local'),
('Micromania Montpellier Centre', '0340000007', 'magasin008@micromania.local'),
('Micromania Bordeaux Centre', '0440000008', 'magasin009@micromania.local'),
('Micromania Lille Centre', '0540000009', 'magasin010@micromania.local'),
('Micromania Rennes Centre', '0140000010', 'magasin011@micromania.local'),
('Micromania Reims Centre', '0240000011', 'magasin012@micromania.local'),
('Micromania Le Havre Centre', '0340000012', 'magasin013@micromania.local'),
('Micromania Saint-Etienne Centre', '0440000013', 'magasin014@micromania.local'),
('Micromania Toulon Centre', '0540000014', 'magasin015@micromania.local'),
('Micromania Grenoble Centre', '0140000015', 'magasin016@micromania.local'),
('Micromania Dijon Centre', '0240000016', 'magasin017@micromania.local'),
('Micromania Angers Centre', '0340000017', 'magasin018@micromania.local'),
('Micromania Nimes Centre', '0440000018', 'magasin019@micromania.local'),
('Micromania Villeurbanne Centre', '0540000019', 'magasin020@micromania.local'),
('Micromania Clermont-Ferrand Centre', '0140000020', 'magasin021@micromania.local'),
('Micromania Le Mans Centre', '0240000021', 'magasin022@micromania.local'),
('Micromania Aix-en-Provence Centre', '0340000022', 'magasin023@micromania.local'),
('Micromania Brest Centre', '0440000023', 'magasin024@micromania.local'),
('Micromania Tours Centre', '0540000024', 'magasin025@micromania.local'),
('Micromania Amiens Centre', '0140000025', 'magasin026@micromania.local'),
('Micromania Limoges Centre', '0240000026', 'magasin027@micromania.local'),
('Micromania Annecy Centre', '0340000027', 'magasin028@micromania.local'),
('Micromania Perpignan Centre', '0440000028', 'magasin029@micromania.local'),
('Micromania Metz Centre', '0540000029', 'magasin030@micromania.local'),
('Micromania Besancon Centre', '0140000030', 'magasin031@micromania.local'),
('Micromania Orleans Centre', '0240000031', 'magasin032@micromania.local'),
('Micromania Mulhouse Centre', '0340000032', 'magasin033@micromania.local'),
('Micromania Rouen Centre', '0440000033', 'magasin034@micromania.local'),
('Micromania Caen Centre', '0540000034', 'magasin035@micromania.local'),
('Micromania Nancy Centre', '0140000035', 'magasin036@micromania.local'),
('Micromania Roubaix Centre', '0240000036', 'magasin037@micromania.local'),
('Micromania Tourcoing Centre', '0340000037', 'magasin038@micromania.local'),
('Micromania Avignon Centre', '0440000038', 'magasin039@micromania.local'),
('Micromania Poitiers Centre', '0540000039', 'magasin040@micromania.local'),
('Micromania Pau Centre', '0140000040', 'magasin041@micromania.local'),
('Micromania La Rochelle Centre', '0240000041', 'magasin042@micromania.local'),
('Micromania Calais Centre', '0340000042', 'magasin043@micromania.local'),
('Micromania Dunkerque Centre', '0440000043', 'magasin044@micromania.local'),
('Micromania Valence Centre', '0540000044', 'magasin045@micromania.local'),
('Micromania Chambery Centre', '0140000045', 'magasin046@micromania.local'),
('Micromania Colmar Centre', '0240000046', 'magasin047@micromania.local'),
('Micromania Ajaccio Centre', '0340000047', 'magasin048@micromania.local'),
('Micromania Bastia Centre', '0440000048', 'magasin049@micromania.local'),
('Micromania Nanterre Centre', '0540000049', 'magasin050@micromania.local'),
('Micromania Creteil Centre', '0140000050', 'magasin051@micromania.local'),
('Micromania Versailles Centre', '0240000051', 'magasin052@micromania.local'),
('Micromania Cergy Centre', '0340000052', 'magasin053@micromania.local'),
('Micromania Evry-Courcouronnes Centre', '0440000053', 'magasin054@micromania.local'),
('Micromania Meaux Centre', '0540000054', 'magasin055@micromania.local'),
('Micromania Melun Centre', '0140000055', 'magasin056@micromania.local'),
('Micromania Troyes Centre', '0240000056', 'magasin057@micromania.local'),
('Micromania Chalon-sur-Saone Centre', '0340000057', 'magasin058@micromania.local'),
('Micromania Macon Centre', '0440000058', 'magasin059@micromania.local'),
('Micromania Bayonne Centre', '0540000059', 'magasin060@micromania.local'),
('Micromania Biarritz Centre', '0140000060', 'magasin061@micromania.local'),
('Micromania Tarbes Centre', '0240000061', 'magasin062@micromania.local'),
('Micromania Vannes Centre', '0340000062', 'magasin063@micromania.local'),
('Micromania Quimper Centre', '0440000063', 'magasin064@micromania.local'),
('Micromania Lorient Centre', '0540000064', 'magasin065@micromania.local'),
('Micromania Saint-Malo Centre', '0140000065', 'magasin066@micromania.local'),
('Micromania Laval Centre', '0240000066', 'magasin067@micromania.local'),
('Micromania Niort Centre', '0340000067', 'magasin068@micromania.local'),
('Micromania Cholet Centre', '0440000068', 'magasin069@micromania.local'),
('Micromania Brive-la-Gaillarde Centre', '0540000069', 'magasin070@micromania.local'),
('Micromania Bourges Centre', '0140000070', 'magasin071@micromania.local'),
('Micromania Chartres Centre', '0240000071', 'magasin072@micromania.local'),
('Micromania Beauvais Centre', '0340000072', 'magasin073@micromania.local'),
('Micromania Compiegne Centre', '0440000073', 'magasin074@micromania.local'),
('Micromania Narbonne Centre', '0540000074', 'magasin075@micromania.local'),
('Micromania Beziers Centre', '0140000075', 'magasin076@micromania.local'),
('Micromania Cannes Centre', '0240000076', 'magasin077@micromania.local'),
('Micromania Antibes Centre', '0340000077', 'magasin078@micromania.local'),
('Micromania Frejus Centre', '0440000078', 'magasin079@micromania.local'),
('Micromania La Seyne-sur-Mer Centre', '0540000079', 'magasin080@micromania.local'),
('Micromania Saint-Nazaire Centre', '0140000080', 'magasin081@micromania.local'),
('Micromania La Roche-sur-Yon Centre', '0240000081', 'magasin082@micromania.local'),
('Micromania Montauban Centre', '0340000082', 'magasin083@micromania.local'),
('Micromania Albi Centre', '0440000083', 'magasin084@micromania.local'),
('Micromania Carcassonne Centre', '0540000084', 'magasin085@micromania.local'),
('Micromania Agen Centre', '0140000085', 'magasin086@micromania.local'),
('Micromania Bergerac Centre', '0240000086', 'magasin087@micromania.local'),
('Micromania Mont-de-Marsan Centre', '0340000087', 'magasin088@micromania.local'),
('Micromania Gap Centre', '0440000088', 'magasin089@micromania.local'),
('Micromania La Valette-du-Var Centre', '0540000089', 'magasin090@micromania.local'),
('Micromania Cagnes-sur-Mer Centre', '0140000090', 'magasin091@micromania.local'),
('Micromania Argenteuil Centre', '0240000091', 'magasin092@micromania.local'),
('Micromania Montreuil Centre', '0340000092', 'magasin093@micromania.local'),
('Micromania Boulogne-Billancourt Centre', '0440000093', 'magasin094@micromania.local'),
('Micromania Noisy-le-Grand Centre', '0540000094', 'magasin095@micromania.local'),
('Micromania Saint-Denis Centre', '0140000095', 'magasin096@micromania.local'),
('Micromania Levallois-Perret Centre', '0240000096', 'magasin097@micromania.local'),
('Micromania Ivry-sur-Seine Centre', '0340000097', 'magasin098@micromania.local'),
('Micromania Aulnay-sous-Bois Centre', '0440000098', 'magasin099@micromania.local'),
('Micromania Drancy Centre', '0540000099', 'magasin100@micromania.local'),
('Micromania Massy Centre', '0140000100', 'magasin101@micromania.local'),
('Micromania Paris Gare', '0240000101', 'magasin102@micromania.local'),
('Micromania Marseille Gare', '0340000102', 'magasin103@micromania.local'),
('Micromania Lyon Gare', '0440000103', 'magasin104@micromania.local'),
('Micromania Toulouse Gare', '0540000104', 'magasin105@micromania.local'),
('Micromania Nice Gare', '0140000105', 'magasin106@micromania.local'),
('Micromania Nantes Gare', '0240000106', 'magasin107@micromania.local'),
('Micromania Strasbourg Gare', '0340000107', 'magasin108@micromania.local'),
('Micromania Montpellier Gare', '0440000108', 'magasin109@micromania.local'),
('Micromania Bordeaux Gare', '0540000109', 'magasin110@micromania.local'),
('Micromania Lille Gare', '0140000110', 'magasin111@micromania.local'),
('Micromania Rennes Gare', '0240000111', 'magasin112@micromania.local'),
('Micromania Reims Gare', '0340000112', 'magasin113@micromania.local'),
('Micromania Le Havre Gare', '0440000113', 'magasin114@micromania.local'),
('Micromania Saint-Etienne Gare', '0540000114', 'magasin115@micromania.local'),
('Micromania Toulon Gare', '0140000115', 'magasin116@micromania.local'),
('Micromania Grenoble Gare', '0240000116', 'magasin117@micromania.local'),
('Micromania Dijon Gare', '0340000117', 'magasin118@micromania.local'),
('Micromania Angers Gare', '0440000118', 'magasin119@micromania.local'),
('Micromania Nimes Gare', '0540000119', 'magasin120@micromania.local'),
('Micromania Villeurbanne Gare', '0140000120', 'magasin121@micromania.local'),
('Micromania Clermont-Ferrand Gare', '0240000121', 'magasin122@micromania.local'),
('Micromania Le Mans Gare', '0340000122', 'magasin123@micromania.local'),
('Micromania Aix-en-Provence Gare', '0440000123', 'magasin124@micromania.local'),
('Micromania Brest Gare', '0540000124', 'magasin125@micromania.local'),
('Micromania Tours Gare', '0140000125', 'magasin126@micromania.local'),
('Micromania Amiens Gare', '0240000126', 'magasin127@micromania.local'),
('Micromania Limoges Gare', '0340000127', 'magasin128@micromania.local'),
('Micromania Annecy Gare', '0440000128', 'magasin129@micromania.local'),
('Micromania Perpignan Gare', '0540000129', 'magasin130@micromania.local'),
('Micromania Metz Gare', '0140000130', 'magasin131@micromania.local'),
('Micromania Besancon Gare', '0240000131', 'magasin132@micromania.local'),
('Micromania Orleans Gare', '0340000132', 'magasin133@micromania.local'),
('Micromania Mulhouse Gare', '0440000133', 'magasin134@micromania.local'),
('Micromania Rouen Gare', '0540000134', 'magasin135@micromania.local'),
('Micromania Caen Gare', '0140000135', 'magasin136@micromania.local'),
('Micromania Nancy Gare', '0240000136', 'magasin137@micromania.local'),
('Micromania Roubaix Gare', '0340000137', 'magasin138@micromania.local'),
('Micromania Tourcoing Gare', '0440000138', 'magasin139@micromania.local'),
('Micromania Avignon Gare', '0540000139', 'magasin140@micromania.local'),
('Micromania Poitiers Gare', '0140000140', 'magasin141@micromania.local'),
('Micromania Pau Gare', '0240000141', 'magasin142@micromania.local'),
('Micromania La Rochelle Gare', '0340000142', 'magasin143@micromania.local'),
('Micromania Calais Gare', '0440000143', 'magasin144@micromania.local'),
('Micromania Dunkerque Gare', '0540000144', 'magasin145@micromania.local'),
('Micromania Valence Gare', '0140000145', 'magasin146@micromania.local'),
('Micromania Chambery Gare', '0240000146', 'magasin147@micromania.local'),
('Micromania Colmar Gare', '0340000147', 'magasin148@micromania.local'),
('Micromania Ajaccio Gare', '0440000148', 'magasin149@micromania.local'),
('Micromania Bastia Gare', '0540000149', 'magasin150@micromania.local'),
('Micromania Nanterre Gare', '0140000150', 'magasin151@micromania.local'),
('Micromania Creteil Gare', '0240000151', 'magasin152@micromania.local'),
('Micromania Versailles Gare', '0340000152', 'magasin153@micromania.local'),
('Micromania Cergy Gare', '0440000153', 'magasin154@micromania.local'),
('Micromania Evry-Courcouronnes Gare', '0540000154', 'magasin155@micromania.local'),
('Micromania Meaux Gare', '0140000155', 'magasin156@micromania.local'),
('Micromania Melun Gare', '0240000156', 'magasin157@micromania.local'),
('Micromania Troyes Gare', '0340000157', 'magasin158@micromania.local'),
('Micromania Chalon-sur-Saone Gare', '0440000158', 'magasin159@micromania.local'),
('Micromania Macon Gare', '0540000159', 'magasin160@micromania.local'),
('Micromania Bayonne Gare', '0140000160', 'magasin161@micromania.local'),
('Micromania Biarritz Gare', '0240000161', 'magasin162@micromania.local'),
('Micromania Tarbes Gare', '0340000162', 'magasin163@micromania.local'),
('Micromania Vannes Gare', '0440000163', 'magasin164@micromania.local'),
('Micromania Quimper Gare', '0540000164', 'magasin165@micromania.local'),
('Micromania Lorient Gare', '0140000165', 'magasin166@micromania.local'),
('Micromania Saint-Malo Gare', '0240000166', 'magasin167@micromania.local'),
('Micromania Laval Gare', '0340000167', 'magasin168@micromania.local'),
('Micromania Niort Gare', '0440000168', 'magasin169@micromania.local'),
('Micromania Cholet Gare', '0540000169', 'magasin170@micromania.local'),
('Micromania Brive-la-Gaillarde Gare', '0140000170', 'magasin171@micromania.local'),
('Micromania Bourges Gare', '0240000171', 'magasin172@micromania.local'),
('Micromania Chartres Gare', '0340000172', 'magasin173@micromania.local'),
('Micromania Beauvais Gare', '0440000173', 'magasin174@micromania.local'),
('Micromania Compiegne Gare', '0540000174', 'magasin175@micromania.local'),
('Micromania Narbonne Gare', '0140000175', 'magasin176@micromania.local'),
('Micromania Beziers Gare', '0240000176', 'magasin177@micromania.local'),
('Micromania Cannes Gare', '0340000177', 'magasin178@micromania.local'),
('Micromania Antibes Gare', '0440000178', 'magasin179@micromania.local'),
('Micromania Frejus Gare', '0540000179', 'magasin180@micromania.local'),
('Micromania La Seyne-sur-Mer Gare', '0140000180', 'magasin181@micromania.local'),
('Micromania Saint-Nazaire Gare', '0240000181', 'magasin182@micromania.local'),
('Micromania La Roche-sur-Yon Gare', '0340000182', 'magasin183@micromania.local'),
('Micromania Montauban Gare', '0440000183', 'magasin184@micromania.local'),
('Micromania Albi Gare', '0540000184', 'magasin185@micromania.local'),
('Micromania Carcassonne Gare', '0140000185', 'magasin186@micromania.local'),
('Micromania Agen Gare', '0240000186', 'magasin187@micromania.local'),
('Micromania Bergerac Gare', '0340000187', 'magasin188@micromania.local'),
('Micromania Mont-de-Marsan Gare', '0440000188', 'magasin189@micromania.local'),
('Micromania Gap Gare', '0540000189', 'magasin190@micromania.local'),
('Micromania La Valette-du-Var Gare', '0140000190', 'magasin191@micromania.local'),
('Micromania Cagnes-sur-Mer Gare', '0240000191', 'magasin192@micromania.local'),
('Micromania Argenteuil Gare', '0340000192', 'magasin193@micromania.local'),
('Micromania Montreuil Gare', '0440000193', 'magasin194@micromania.local'),
('Micromania Boulogne-Billancourt Gare', '0540000194', 'magasin195@micromania.local'),
('Micromania Noisy-le-Grand Gare', '0140000195', 'magasin196@micromania.local'),
('Micromania Saint-Denis Gare', '0240000196', 'magasin197@micromania.local'),
('Micromania Levallois-Perret Gare', '0340000197', 'magasin198@micromania.local'),
('Micromania Ivry-sur-Seine Gare', '0440000198', 'magasin199@micromania.local'),
('Micromania Aulnay-sous-Bois Gare', '0540000199', 'magasin200@micromania.local'),
('Micromania Drancy Gare', '0140000200', 'magasin201@micromania.local'),
('Micromania Massy Gare', '0240000201', 'magasin202@micromania.local'),
('Micromania Paris Nord', '0340000202', 'magasin203@micromania.local'),
('Micromania Marseille Nord', '0440000203', 'magasin204@micromania.local'),
('Micromania Lyon Nord', '0540000204', 'magasin205@micromania.local'),
('Micromania Toulouse Nord', '0140000205', 'magasin206@micromania.local'),
('Micromania Nice Nord', '0240000206', 'magasin207@micromania.local'),
('Micromania Nantes Nord', '0340000207', 'magasin208@micromania.local'),
('Micromania Strasbourg Nord', '0440000208', 'magasin209@micromania.local'),
('Micromania Montpellier Nord', '0540000209', 'magasin210@micromania.local'),
('Micromania Bordeaux Nord', '0140000210', 'magasin211@micromania.local'),
('Micromania Lille Nord', '0240000211', 'magasin212@micromania.local'),
('Micromania Rennes Nord', '0340000212', 'magasin213@micromania.local'),
('Micromania Reims Nord', '0440000213', 'magasin214@micromania.local'),
('Micromania Le Havre Nord', '0540000214', 'magasin215@micromania.local'),
('Micromania Saint-Etienne Nord', '0140000215', 'magasin216@micromania.local'),
('Micromania Toulon Nord', '0240000216', 'magasin217@micromania.local'),
('Micromania Grenoble Nord', '0340000217', 'magasin218@micromania.local'),
('Micromania Dijon Nord', '0440000218', 'magasin219@micromania.local'),
('Micromania Angers Nord', '0540000219', 'magasin220@micromania.local'),
('Micromania Nimes Nord', '0140000220', 'magasin221@micromania.local'),
('Micromania Villeurbanne Nord', '0240000221', 'magasin222@micromania.local'),
('Micromania Clermont-Ferrand Nord', '0340000222', 'magasin223@micromania.local'),
('Micromania Le Mans Nord', '0440000223', 'magasin224@micromania.local'),
('Micromania Aix-en-Provence Nord', '0540000224', 'magasin225@micromania.local'),
('Micromania Brest Nord', '0140000225', 'magasin226@micromania.local'),
('Micromania Tours Nord', '0240000226', 'magasin227@micromania.local'),
('Micromania Amiens Nord', '0340000227', 'magasin228@micromania.local'),
('Micromania Limoges Nord', '0440000228', 'magasin229@micromania.local'),
('Micromania Annecy Nord', '0540000229', 'magasin230@micromania.local'),
('Micromania Perpignan Nord', '0140000230', 'magasin231@micromania.local'),
('Micromania Metz Nord', '0240000231', 'magasin232@micromania.local'),
('Micromania Besancon Nord', '0340000232', 'magasin233@micromania.local'),
('Micromania Orleans Nord', '0440000233', 'magasin234@micromania.local'),
('Micromania Mulhouse Nord', '0540000234', 'magasin235@micromania.local'),
('Micromania Rouen Nord', '0140000235', 'magasin236@micromania.local'),
('Micromania Caen Nord', '0240000236', 'magasin237@micromania.local'),
('Micromania Nancy Nord', '0340000237', 'magasin238@micromania.local'),
('Micromania Roubaix Nord', '0440000238', 'magasin239@micromania.local'),
('Micromania Tourcoing Nord', '0540000239', 'magasin240@micromania.local'),
('Micromania Avignon Nord', '0140000240', 'magasin241@micromania.local'),
('Micromania Poitiers Nord', '0240000241', 'magasin242@micromania.local'),
('Micromania Pau Nord', '0340000242', 'magasin243@micromania.local'),
('Micromania La Rochelle Nord', '0440000243', 'magasin244@micromania.local'),
('Micromania Calais Nord', '0540000244', 'magasin245@micromania.local'),
('Micromania Dunkerque Nord', '0140000245', 'magasin246@micromania.local'),
('Micromania Valence Nord', '0240000246', 'magasin247@micromania.local'),
('Micromania Chambery Nord', '0340000247', 'magasin248@micromania.local'),
('Micromania Colmar Nord', '0440000248', 'magasin249@micromania.local'),
('Micromania Ajaccio Nord', '0540000249', 'magasin250@micromania.local'),
('Micromania Bastia Nord', '0140000250', 'magasin251@micromania.local'),
('Micromania Nanterre Nord', '0240000251', 'magasin252@micromania.local'),
('Micromania Creteil Nord', '0340000252', 'magasin253@micromania.local'),
('Micromania Versailles Nord', '0440000253', 'magasin254@micromania.local'),
('Micromania Cergy Nord', '0540000254', 'magasin255@micromania.local'),
('Micromania Evry-Courcouronnes Nord', '0140000255', 'magasin256@micromania.local'),
('Micromania Meaux Nord', '0240000256', 'magasin257@micromania.local'),
('Micromania Melun Nord', '0340000257', 'magasin258@micromania.local'),
('Micromania Troyes Nord', '0440000258', 'magasin259@micromania.local'),
('Micromania Chalon-sur-Saone Nord', '0540000259', 'magasin260@micromania.local'),
('Micromania Macon Nord', '0140000260', 'magasin261@micromania.local'),
('Micromania Bayonne Nord', '0240000261', 'magasin262@micromania.local'),
('Micromania Biarritz Nord', '0340000262', 'magasin263@micromania.local'),
('Micromania Tarbes Nord', '0440000263', 'magasin264@micromania.local'),
('Micromania Vannes Nord', '0540000264', 'magasin265@micromania.local'),
('Micromania Quimper Nord', '0140000265', 'magasin266@micromania.local'),
('Micromania Lorient Nord', '0240000266', 'magasin267@micromania.local'),
('Micromania Saint-Malo Nord', '0340000267', 'magasin268@micromania.local'),
('Micromania Laval Nord', '0440000268', 'magasin269@micromania.local'),
('Micromania Niort Nord', '0540000269', 'magasin270@micromania.local'),
('Micromania Cholet Nord', '0140000270', 'magasin271@micromania.local'),
('Micromania Brive-la-Gaillarde Nord', '0240000271', 'magasin272@micromania.local'),
('Micromania Bourges Nord', '0340000272', 'magasin273@micromania.local'),
('Micromania Chartres Nord', '0440000273', 'magasin274@micromania.local'),
('Micromania Beauvais Nord', '0540000274', 'magasin275@micromania.local'),
('Micromania Compiegne Nord', '0140000275', 'magasin276@micromania.local'),
('Micromania Narbonne Nord', '0240000276', 'magasin277@micromania.local'),
('Micromania Beziers Nord', '0340000277', 'magasin278@micromania.local'),
('Micromania Cannes Nord', '0440000278', 'magasin279@micromania.local'),
('Micromania Antibes Nord', '0540000279', 'magasin280@micromania.local'),
('Micromania Frejus Nord', '0140000280', 'magasin281@micromania.local'),
('Micromania La Seyne-sur-Mer Nord', '0240000281', 'magasin282@micromania.local'),
('Micromania Saint-Nazaire Nord', '0340000282', 'magasin283@micromania.local'),
('Micromania La Roche-sur-Yon Nord', '0440000283', 'magasin284@micromania.local'),
('Micromania Montauban Nord', '0540000284', 'magasin285@micromania.local'),
('Micromania Albi Nord', '0140000285', 'magasin286@micromania.local'),
('Micromania Carcassonne Nord', '0240000286', 'magasin287@micromania.local'),
('Micromania Agen Nord', '0340000287', 'magasin288@micromania.local'),
('Micromania Bergerac Nord', '0440000288', 'magasin289@micromania.local'),
('Micromania Mont-de-Marsan Nord', '0540000289', 'magasin290@micromania.local'),
('Micromania Gap Nord', '0140000290', 'magasin291@micromania.local'),
('Micromania La Valette-du-Var Nord', '0240000291', 'magasin292@micromania.local'),
('Micromania Cagnes-sur-Mer Nord', '0340000292', 'magasin293@micromania.local'),
('Micromania Argenteuil Nord', '0440000293', 'magasin294@micromania.local'),
('Micromania Montreuil Nord', '0540000294', 'magasin295@micromania.local'),
('Micromania Boulogne-Billancourt Nord', '0140000295', 'magasin296@micromania.local'),
('Micromania Noisy-le-Grand Nord', '0240000296', 'magasin297@micromania.local'),
('Micromania Saint-Denis Nord', '0340000297', 'magasin298@micromania.local'),
('Micromania Levallois-Perret Nord', '0440000298', 'magasin299@micromania.local'),
('Micromania Ivry-sur-Seine Nord', '0540000299', 'magasin300@micromania.local'),
('Micromania Aulnay-sous-Bois Nord', '0140000300', 'magasin301@micromania.local'),
('Micromania Drancy Nord', '0240000301', 'magasin302@micromania.local'),
('Micromania Massy Nord', '0340000302', 'magasin303@micromania.local'),
('Micromania Paris Sud', '0440000303', 'magasin304@micromania.local'),
('Micromania Marseille Sud', '0540000304', 'magasin305@micromania.local'),
('Micromania Lyon Sud', '0140000305', 'magasin306@micromania.local'),
('Micromania Toulouse Sud', '0240000306', 'magasin307@micromania.local'),
('Micromania Nice Sud', '0340000307', 'magasin308@micromania.local'),
('Micromania Nantes Sud', '0440000308', 'magasin309@micromania.local'),
('Micromania Strasbourg Sud', '0540000309', 'magasin310@micromania.local'),
('Micromania Montpellier Sud', '0140000310', 'magasin311@micromania.local'),
('Micromania Bordeaux Sud', '0240000311', 'magasin312@micromania.local'),
('Micromania Lille Sud', '0340000312', 'magasin313@micromania.local'),
('Micromania Rennes Sud', '0440000313', 'magasin314@micromania.local'),
('Micromania Reims Sud', '0540000314', 'magasin315@micromania.local'),
('Micromania Le Havre Sud', '0140000315', 'magasin316@micromania.local'),
('Micromania Saint-Etienne Sud', '0240000316', 'magasin317@micromania.local');

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

-- ------------------------------------------------------------
-- ADRESSES MAGASINS COMPLEMENTAIRES (GPS inclus)
-- ------------------------------------------------------------

INSERT INTO adresse (id_magasin, id_type_adresse, rue, ville, code_postal, pays, latitude, longitude, est_defaut) VALUES
(4, 4, '29 Rue de la Republique', 'Toulouse', '31000', 'France', 43.605900, 1.444200, TRUE),
(5, 4, '36 Rue de la Republique', 'Nice', '06000', 'France', 43.712600, 7.263100, TRUE),
(6, 4, '43 Rue de la Republique', 'Nantes', '44000', 'France', 47.216000, -1.551400, TRUE),
(7, 4, '50 Rue de la Republique', 'Strasbourg', '67000', 'France', 48.572200, 7.755400, TRUE),
(8, 4, '57 Rue de la Republique', 'Montpellier', '34000', 'France', 43.611900, 3.873900, TRUE),
(9, 4, '64 Rue de la Republique', 'Bordeaux', '33000', 'France', 44.839000, -0.581400, TRUE),
(10, 4, '71 Rue de la Republique', 'Lille', '59000', 'France', 50.631600, 3.056200, TRUE),
(11, 4, '78 Rue de la Republique', 'Rennes', '35000', 'France', 48.114900, -1.677800, TRUE),
(12, 4, '85 Rue de la Republique', 'Reims', '51100', 'France', 49.257100, 4.032800, TRUE),
(13, 4, '92 Rue de la Republique', 'Le Havre', '76600', 'France', 49.494400, 0.110100, TRUE),
(14, 4, '99 Rue de la Republique', 'Saint-Etienne', '42000', 'France', 45.440900, 4.390500, TRUE),
(15, 4, '106 Rue de la Republique', 'Toulon', '83000', 'France', 43.126600, 5.924700, TRUE),
(16, 4, '113 Rue de la Republique', 'Grenoble', '38000', 'France', 45.186100, 5.722300, TRUE),
(17, 4, '120 Rue de la Republique', 'Dijon', '21000', 'France', 47.320800, 5.040400, TRUE),
(18, 4, '127 Rue de la Republique', 'Angers', '49000', 'France', 47.478400, -0.563200, TRUE),
(19, 4, '134 Rue de la Republique', 'Nimes', '30000', 'France', 43.837900, 4.361200, TRUE),
(20, 4, '141 Rue de la Republique', 'Villeurbanne', '69100', 'France', 45.768400, 4.882200, TRUE),
(21, 4, '148 Rue de la Republique', 'Clermont-Ferrand', '63000', 'France', 45.774800, 3.090300, TRUE),
(22, 4, '13 Rue de la Republique', 'Le Mans', '72000', 'France', 48.004900, 0.196300, TRUE),
(23, 4, '20 Rue de la Republique', 'Aix-en-Provence', '13100', 'France', 43.529700, 5.445200, TRUE),
(24, 4, '27 Rue de la Republique', 'Brest', '29200', 'France', 48.391600, -4.487200, TRUE),
(25, 4, '34 Rue de la Republique', 'Tours', '37000', 'France', 47.396500, 0.684800, TRUE),
(26, 4, '41 Rue de la Republique', 'Amiens', '80000', 'France', 49.891700, 2.296900, TRUE),
(27, 4, '48 Rue de la Republique', 'Limoges', '87000', 'France', 45.832400, 1.263300, TRUE),
(28, 4, '55 Rue de la Republique', 'Annecy', '74000', 'France', 45.899200, 6.132700, TRUE),
(29, 4, '62 Rue de la Republique', 'Perpignan', '66000', 'France', 42.689900, 2.891500, TRUE),
(30, 4, '69 Rue de la Republique', 'Metz', '57000', 'France', 49.121700, 6.173500, TRUE),
(31, 4, '76 Rue de la Republique', 'Besancon', '25000', 'France', 47.235400, 6.023000, TRUE),
(32, 4, '83 Rue de la Republique', 'Orleans', '45000', 'France', 47.901700, 1.909300, TRUE),
(33, 4, '90 Rue de la Republique', 'Mulhouse', '68100', 'France', 47.750800, 7.337000, TRUE),
(34, 4, '97 Rue de la Republique', 'Rouen', '76000', 'France', 49.444300, 1.101500, TRUE),
(35, 4, '104 Rue de la Republique', 'Caen', '14000', 'France', 49.185300, -0.367400, TRUE),
(36, 4, '111 Rue de la Republique', 'Nancy', '54000', 'France', 48.689700, 6.181100, TRUE),
(37, 4, '118 Rue de la Republique', 'Roubaix', '59100', 'France', 50.691500, 3.172400, TRUE),
(38, 4, '125 Rue de la Republique', 'Tourcoing', '59200', 'France', 50.723900, 3.160100, TRUE),
(39, 4, '132 Rue de la Republique', 'Avignon', '84000', 'France', 43.950500, 4.805500, TRUE),
(40, 4, '139 Rue de la Republique', 'Poitiers', '86000', 'France', 46.582600, 0.341500, TRUE),
(41, 4, '146 Rue de la Republique', 'Pau', '64000', 'France', 43.292700, -0.368600, TRUE),
(42, 4, '11 Rue de la Republique', 'La Rochelle', '17000', 'France', 46.159100, -1.147800, TRUE),
(43, 4, '18 Rue de la Republique', 'Calais', '62100', 'France', 50.951300, 1.855400, TRUE),
(44, 4, '25 Rue de la Republique', 'Dunkerque', '59140', 'France', 51.035600, 2.374600, TRUE),
(45, 4, '32 Rue de la Republique', 'Valence', '26000', 'France', 44.935800, 4.891300, TRUE),
(46, 4, '39 Rue de la Republique', 'Chambery', '73000', 'France', 45.562200, 5.917800, TRUE),
(47, 4, '46 Rue de la Republique', 'Colmar', '68000', 'France', 48.078200, 7.359600, TRUE),
(48, 4, '53 Rue de la Republique', 'Ajaccio', '20000', 'France', 41.919200, 8.740800, TRUE),
(49, 4, '60 Rue de la Republique', 'Bastia', '20200', 'France', 42.698500, 9.454200, TRUE),
(50, 4, '67 Rue de la Republique', 'Nanterre', '92000', 'France', 48.894800, 2.203300, TRUE),
(51, 4, '74 Rue de la Republique', 'Creteil', '94000', 'France', 48.788000, 2.453400, TRUE),
(52, 4, '81 Rue de la Republique', 'Versailles', '78000', 'France', 48.803700, 2.119300, TRUE),
(53, 4, '88 Rue de la Republique', 'Cergy', '95000', 'France', 49.036400, 2.076100, TRUE),
(54, 4, '95 Rue de la Republique', 'Evry-Courcouronnes', '91000', 'France', 48.634000, 2.441100, TRUE),
(55, 4, '102 Rue de la Republique', 'Meaux', '77100', 'France', 48.962800, 2.890500, TRUE),
(56, 4, '109 Rue de la Republique', 'Melun', '77000', 'France', 48.536600, 2.663600, TRUE),
(57, 4, '116 Rue de la Republique', 'Troyes', '10000', 'France', 48.296100, 4.071100, TRUE),
(58, 4, '123 Rue de la Republique', 'Chalon-sur-Saone', '71100', 'France', 46.781100, 4.851500, TRUE),
(59, 4, '130 Rue de la Republique', 'Macon', '71000', 'France', 46.307200, 4.827600, TRUE),
(60, 4, '137 Rue de la Republique', 'Bayonne', '64100', 'France', 43.495300, -1.474800, TRUE),
(61, 4, '144 Rue de la Republique', 'Biarritz', '64200', 'France', 43.480800, -1.557500, TRUE),
(62, 4, '9 Rue de la Republique', 'Tarbes', '65000', 'France', 43.231300, 0.080300, TRUE),
(63, 4, '16 Rue de la Republique', 'Vannes', '56000', 'France', 47.658200, -2.757500, TRUE),
(64, 4, '23 Rue de la Republique', 'Quimper', '29000', 'France', 47.997300, -4.105800, TRUE),
(65, 4, '30 Rue de la Republique', 'Lorient', '56100', 'France', 47.750700, -3.372400, TRUE),
(66, 4, '37 Rue de la Republique', 'Saint-Malo', '35400', 'France', 48.646900, -2.026800, TRUE),
(67, 4, '44 Rue de la Republique', 'Laval', '53000', 'France', 48.068800, -0.773000, TRUE),
(68, 4, '51 Rue de la Republique', 'Niort', '79000', 'France', 46.323700, -0.457700, TRUE),
(69, 4, '58 Rue de la Republique', 'Cholet', '49300', 'France', 47.060000, -0.877500, TRUE),
(70, 4, '65 Rue de la Republique', 'Brive-la-Gaillarde', '19100', 'France', 45.161400, 1.536400, TRUE),
(71, 4, '72 Rue de la Republique', 'Bourges', '18000', 'France', 47.078600, 2.395500, TRUE),
(72, 4, '79 Rue de la Republique', 'Chartres', '28000', 'France', 48.442700, 1.486800, TRUE),
(73, 4, '86 Rue de la Republique', 'Beauvais', '60000', 'France', 49.429500, 2.079600, TRUE),
(74, 4, '93 Rue de la Republique', 'Compiegne', '60200', 'France', 49.419100, 2.826100, TRUE),
(75, 4, '100 Rue de la Republique', 'Narbonne', '11100', 'France', 43.186700, 3.004200, TRUE),
(76, 4, '107 Rue de la Republique', 'Beziers', '34500', 'France', 43.341800, 3.218000, TRUE),
(77, 4, '114 Rue de la Republique', 'Cannes', '06400', 'France', 43.551600, 7.020700, TRUE),
(78, 4, '121 Rue de la Republique', 'Antibes', '06600', 'France', 43.580400, 7.121800, TRUE),
(79, 4, '128 Rue de la Republique', 'Frejus', '83600', 'France', 43.434400, 6.734800, TRUE),
(80, 4, '135 Rue de la Republique', 'La Seyne-sur-Mer', '83500', 'France', 43.105500, 5.877700, TRUE),
(81, 4, '142 Rue de la Republique', 'Saint-Nazaire', '44600', 'France', 47.271100, -2.213700, TRUE),
(82, 4, '149 Rue de la Republique', 'La Roche-sur-Yon', '85000', 'France', 46.669300, -1.425200, TRUE),
(83, 4, '14 Rue de la Republique', 'Montauban', '82000', 'France', 44.017600, 1.356200, TRUE),
(84, 4, '21 Rue de la Republique', 'Albi', '81000', 'France', 43.926200, 2.151300, TRUE),
(85, 4, '28 Rue de la Republique', 'Carcassonne', '11000', 'France', 43.215400, 2.345800, TRUE),
(86, 4, '35 Rue de la Republique', 'Agen', '47000', 'France', 44.202500, 0.619000, TRUE),
(87, 4, '42 Rue de la Republique', 'Bergerac', '24100', 'France', 44.849900, 0.482700, TRUE),
(88, 4, '49 Rue de la Republique', 'Mont-de-Marsan', '40000', 'France', 43.890200, -0.497100, TRUE),
(89, 4, '56 Rue de la Republique', 'Gap', '05000', 'France', 44.559200, 6.079700, TRUE),
(90, 4, '63 Rue de la Republique', 'La Valette-du-Var', '83160', 'France', 43.139400, 5.986600, TRUE),
(91, 4, '70 Rue de la Republique', 'Cagnes-sur-Mer', '06800', 'France', 43.661100, 7.151200, TRUE),
(92, 4, '77 Rue de la Republique', 'Argenteuil', '95100', 'France', 48.946000, 2.243400, TRUE),
(93, 4, '84 Rue de la Republique', 'Montreuil', '93100', 'France', 48.863800, 2.446300, TRUE),
(94, 4, '91 Rue de la Republique', 'Boulogne-Billancourt', '92100', 'France', 48.836400, 2.239800, TRUE),
(95, 4, '98 Rue de la Republique', 'Noisy-le-Grand', '93160', 'France', 48.851000, 2.562600, TRUE),
(96, 4, '105 Rue de la Republique', 'Saint-Denis', '93200', 'France', 48.933800, 2.358500, TRUE),
(97, 4, '112 Rue de la Republique', 'Levallois-Perret', '92300', 'France', 48.892000, 2.290800, TRUE),
(98, 4, '119 Rue de la Republique', 'Ivry-sur-Seine', '94200', 'France', 48.813100, 2.391500, TRUE),
(99, 4, '126 Rue de la Republique', 'Aulnay-sous-Bois', '93600', 'France', 48.939300, 2.491600, TRUE),
(100, 4, '133 Rue de la Republique', 'Drancy', '93700', 'France', 48.929100, 2.442800, TRUE),
(101, 4, '140 Rue de la Republique', 'Massy', '91300', 'France', 48.728500, 2.269900, TRUE),
(102, 4, '147 Avenue de la Gare', 'Paris', '75001', 'France', 48.863400, 2.346200, TRUE),
(103, 4, '12 Avenue de la Gare', 'Marseille', '13001', 'France', 43.304500, 5.364900, TRUE),
(104, 4, '19 Avenue de la Gare', 'Lyon', '69002', 'France', 45.773200, 4.831900, TRUE),
(105, 4, '26 Avenue de la Gare', 'Toulouse', '31000', 'France', 43.615100, 1.441500, TRUE),
(106, 4, '33 Avenue de la Gare', 'Nice', '06000', 'France', 43.715800, 7.252700, TRUE),
(107, 4, '40 Avenue de la Gare', 'Nantes', '44000', 'France', 47.225200, -1.561800, TRUE),
(108, 4, '47 Avenue de la Gare', 'Strasbourg', '67000', 'France', 48.581400, 7.745000, TRUE),
(109, 4, '54 Avenue de la Gare', 'Montpellier', '34000', 'France', 43.621100, 3.871200, TRUE),
(110, 4, '61 Avenue de la Gare', 'Bordeaux', '33000', 'France', 44.848200, -0.584100, TRUE),
(111, 4, '68 Avenue de la Gare', 'Lille', '59000', 'France', 50.634800, 3.053500, TRUE),
(112, 4, '75 Avenue de la Gare', 'Rennes', '35000', 'France', 48.124100, -1.680500, TRUE),
(113, 4, '82 Avenue de la Gare', 'Reims', '51100', 'France', 49.266300, 4.022400, TRUE),
(114, 4, '89 Avenue de la Gare', 'Le Havre', '76600', 'France', 49.503600, 0.099700, TRUE),
(115, 4, '96 Avenue de la Gare', 'Saint-Etienne', '42000', 'France', 45.450100, 4.380100, TRUE),
(116, 4, '103 Avenue de la Gare', 'Toulon', '83000', 'France', 43.129800, 5.922000, TRUE),
(117, 4, '110 Avenue de la Gare', 'Grenoble', '38000', 'France', 45.195300, 5.719600, TRUE),
(118, 4, '117 Avenue de la Gare', 'Dijon', '21000', 'France', 47.330000, 5.037700, TRUE),
(119, 4, '124 Avenue de la Gare', 'Angers', '49000', 'France', 47.487600, -0.565900, TRUE),
(120, 4, '131 Avenue de la Gare', 'Nimes', '30000', 'France', 43.847100, 4.350800, TRUE),
(121, 4, '138 Avenue de la Gare', 'Villeurbanne', '69100', 'France', 45.771600, 4.871800, TRUE),
(122, 4, '145 Avenue de la Gare', 'Clermont-Ferrand', '63000', 'France', 45.784000, 3.079900, TRUE),
(123, 4, '10 Avenue de la Gare', 'Le Mans', '72000', 'France', 48.014100, 0.193600, TRUE),
(124, 4, '17 Avenue de la Gare', 'Aix-en-Provence', '13100', 'France', 43.538900, 5.442500, TRUE),
(125, 4, '24 Avenue de la Gare', 'Brest', '29200', 'France', 48.400800, -4.489900, TRUE),
(126, 4, '31 Avenue de la Gare', 'Tours', '37000', 'France', 47.399700, 0.682100, TRUE),
(127, 4, '38 Avenue de la Gare', 'Amiens', '80000', 'France', 49.900900, 2.286500, TRUE),
(128, 4, '45 Avenue de la Gare', 'Limoges', '87000', 'France', 45.841600, 1.252900, TRUE),
(129, 4, '52 Avenue de la Gare', 'Annecy', '74000', 'France', 45.908400, 6.122300, TRUE),
(130, 4, '59 Avenue de la Gare', 'Perpignan', '66000', 'France', 42.699100, 2.888800, TRUE),
(131, 4, '66 Avenue de la Gare', 'Metz', '57000', 'France', 49.124900, 6.170800, TRUE),
(132, 4, '73 Avenue de la Gare', 'Besancon', '25000', 'France', 47.244600, 6.020300, TRUE),
(133, 4, '80 Avenue de la Gare', 'Orleans', '45000', 'France', 47.910900, 1.906600, TRUE),
(134, 4, '87 Avenue de la Gare', 'Mulhouse', '68100', 'France', 47.760000, 7.326600, TRUE),
(135, 4, '94 Avenue de la Gare', 'Rouen', '76000', 'France', 49.453500, 1.091100, TRUE),
(136, 4, '101 Avenue de la Gare', 'Caen', '14000', 'France', 49.188500, -0.377800, TRUE),
(137, 4, '108 Avenue de la Gare', 'Nancy', '54000', 'France', 48.698900, 6.178400, TRUE),
(138, 4, '115 Avenue de la Gare', 'Roubaix', '59100', 'France', 50.700700, 3.169700, TRUE),
(139, 4, '122 Avenue de la Gare', 'Tourcoing', '59200', 'France', 50.733100, 3.157400, TRUE),
(140, 4, '129 Avenue de la Gare', 'Avignon', '84000', 'France', 43.959700, 4.802800, TRUE),
(141, 4, '136 Avenue de la Gare', 'Poitiers', '86000', 'France', 46.585800, 0.331100, TRUE),
(142, 4, '143 Avenue de la Gare', 'Pau', '64000', 'France', 43.301900, -0.379000, TRUE),
(143, 4, '8 Avenue de la Gare', 'La Rochelle', '17000', 'France', 46.168300, -1.158200, TRUE),
(144, 4, '15 Avenue de la Gare', 'Calais', '62100', 'France', 50.960500, 1.852700, TRUE),
(145, 4, '22 Avenue de la Gare', 'Dunkerque', '59140', 'France', 51.044800, 2.371900, TRUE),
(146, 4, '29 Avenue de la Gare', 'Valence', '26000', 'France', 44.939000, 4.888600, TRUE),
(147, 4, '36 Avenue de la Gare', 'Chambery', '73000', 'France', 45.571400, 5.915100, TRUE),
(148, 4, '43 Avenue de la Gare', 'Colmar', '68000', 'France', 48.087400, 7.349200, TRUE),
(149, 4, '50 Avenue de la Gare', 'Ajaccio', '20000', 'France', 41.928400, 8.730400, TRUE),
(150, 4, '57 Avenue de la Gare', 'Bastia', '20200', 'France', 42.707700, 9.443800, TRUE),
(151, 4, '64 Avenue de la Gare', 'Nanterre', '92000', 'France', 48.898000, 2.200600, TRUE),
(152, 4, '71 Avenue de la Gare', 'Creteil', '94000', 'France', 48.797200, 2.450700, TRUE),
(153, 4, '78 Avenue de la Gare', 'Versailles', '78000', 'France', 48.812900, 2.116600, TRUE),
(154, 4, '85 Avenue de la Gare', 'Cergy', '95000', 'France', 49.045600, 2.073400, TRUE),
(155, 4, '92 Avenue de la Gare', 'Evry-Courcouronnes', '91000', 'France', 48.643200, 2.430700, TRUE),
(156, 4, '99 Avenue de la Gare', 'Meaux', '77100', 'France', 48.966000, 2.880100, TRUE),
(157, 4, '106 Avenue de la Gare', 'Melun', '77000', 'France', 48.545800, 2.653200, TRUE),
(158, 4, '113 Avenue de la Gare', 'Troyes', '10000', 'France', 48.305300, 4.068400, TRUE),
(159, 4, '120 Avenue de la Gare', 'Chalon-sur-Saone', '71100', 'France', 46.790300, 4.848800, TRUE),
(160, 4, '127 Avenue de la Gare', 'Macon', '71000', 'France', 46.316400, 4.824900, TRUE),
(161, 4, '134 Avenue de la Gare', 'Bayonne', '64100', 'France', 43.498500, -1.477500, TRUE),
(162, 4, '141 Avenue de la Gare', 'Biarritz', '64200', 'France', 43.490000, -1.567900, TRUE),
(163, 4, '148 Avenue de la Gare', 'Tarbes', '65000', 'France', 43.240500, 0.069900, TRUE),
(164, 4, '13 Avenue de la Gare', 'Vannes', '56000', 'France', 47.667400, -2.767900, TRUE),
(165, 4, '20 Avenue de la Gare', 'Quimper', '29000', 'France', 48.006500, -4.108500, TRUE),
(166, 4, '27 Avenue de la Gare', 'Lorient', '56100', 'France', 47.753900, -3.375100, TRUE),
(167, 4, '34 Avenue de la Gare', 'Saint-Malo', '35400', 'France', 48.656100, -2.029500, TRUE),
(168, 4, '41 Avenue de la Gare', 'Laval', '53000', 'France', 48.078000, -0.775700, TRUE),
(169, 4, '48 Avenue de la Gare', 'Niort', '79000', 'France', 46.332900, -0.468100, TRUE),
(170, 4, '55 Avenue de la Gare', 'Cholet', '49300', 'France', 47.069200, -0.887900, TRUE),
(171, 4, '62 Avenue de la Gare', 'Brive-la-Gaillarde', '19100', 'France', 45.164600, 1.526000, TRUE),
(172, 4, '69 Avenue de la Gare', 'Bourges', '18000', 'France', 47.087800, 2.392800, TRUE),
(173, 4, '76 Avenue de la Gare', 'Chartres', '28000', 'France', 48.451900, 1.484100, TRUE),
(174, 4, '83 Avenue de la Gare', 'Beauvais', '60000', 'France', 49.438700, 2.076900, TRUE),
(175, 4, '90 Avenue de la Gare', 'Compiegne', '60200', 'France', 49.428300, 2.823400, TRUE),
(176, 4, '97 Avenue de la Gare', 'Narbonne', '11100', 'France', 43.189900, 2.993800, TRUE),
(177, 4, '104 Avenue de la Gare', 'Beziers', '34500', 'France', 43.351000, 3.207600, TRUE),
(178, 4, '111 Avenue de la Gare', 'Cannes', '06400', 'France', 43.560800, 7.010300, TRUE),
(179, 4, '118 Avenue de la Gare', 'Antibes', '06600', 'France', 43.589600, 7.119100, TRUE),
(180, 4, '125 Avenue de la Gare', 'Frejus', '83600', 'France', 43.443600, 6.732100, TRUE),
(181, 4, '132 Avenue de la Gare', 'La Seyne-sur-Mer', '83500', 'France', 43.108700, 5.875000, TRUE),
(182, 4, '139 Avenue de la Gare', 'Saint-Nazaire', '44600', 'France', 47.280300, -2.216400, TRUE),
(183, 4, '146 Avenue de la Gare', 'La Roche-sur-Yon', '85000', 'France', 46.678500, -1.435600, TRUE),
(184, 4, '11 Avenue de la Gare', 'Montauban', '82000', 'France', 44.026800, 1.345800, TRUE),
(185, 4, '18 Avenue de la Gare', 'Albi', '81000', 'France', 43.935400, 2.140900, TRUE),
(186, 4, '25 Avenue de la Gare', 'Carcassonne', '11000', 'France', 43.218600, 2.343100, TRUE),
(187, 4, '32 Avenue de la Gare', 'Agen', '47000', 'France', 44.211700, 0.616300, TRUE),
(188, 4, '39 Avenue de la Gare', 'Bergerac', '24100', 'France', 44.859100, 0.480000, TRUE),
(189, 4, '46 Avenue de la Gare', 'Mont-de-Marsan', '40000', 'France', 43.899400, -0.499800, TRUE),
(190, 4, '53 Avenue de la Gare', 'Gap', '05000', 'France', 44.568400, 6.069300, TRUE),
(191, 4, '60 Avenue de la Gare', 'La Valette-du-Var', '83160', 'France', 43.142600, 5.976200, TRUE),
(192, 4, '67 Avenue de la Gare', 'Cagnes-sur-Mer', '06800', 'France', 43.670300, 7.140800, TRUE),
(193, 4, '74 Avenue de la Gare', 'Argenteuil', '95100', 'France', 48.955200, 2.240700, TRUE),
(194, 4, '81 Avenue de la Gare', 'Montreuil', '93100', 'France', 48.873000, 2.443600, TRUE),
(195, 4, '88 Avenue de la Gare', 'Boulogne-Billancourt', '92100', 'France', 48.845600, 2.237100, TRUE),
(196, 4, '95 Avenue de la Gare', 'Noisy-le-Grand', '93160', 'France', 48.854200, 2.559900, TRUE),
(197, 4, '102 Avenue de la Gare', 'Saint-Denis', '93200', 'France', 48.943000, 2.348100, TRUE),
(198, 4, '109 Avenue de la Gare', 'Levallois-Perret', '92300', 'France', 48.901200, 2.280400, TRUE),
(199, 4, '116 Avenue de la Gare', 'Ivry-sur-Seine', '94200', 'France', 48.822300, 2.381100, TRUE),
(200, 4, '123 Avenue de la Gare', 'Aulnay-sous-Bois', '93600', 'France', 48.948500, 2.488900, TRUE),
(201, 4, '130 Avenue de la Gare', 'Drancy', '93700', 'France', 48.932300, 2.440100, TRUE),
(202, 4, '137 Avenue de la Gare', 'Massy', '91300', 'France', 48.737700, 2.267200, TRUE),
(203, 4, '144 Rue du Commerce', 'Paris', '75001', 'France', 48.874600, 2.355500, TRUE),
(204, 4, '9 Rue du Commerce', 'Marseille', '13001', 'France', 43.315700, 5.366500, TRUE),
(205, 4, '16 Rue du Commerce', 'Lyon', '69002', 'France', 45.784400, 4.833500, TRUE),
(206, 4, '23 Rue du Commerce', 'Toulouse', '31000', 'France', 43.620300, 1.443100, TRUE),
(207, 4, '30 Rue du Commerce', 'Nice', '06000', 'France', 43.727000, 7.262000, TRUE),
(208, 4, '37 Rue du Commerce', 'Nantes', '44000', 'France', 47.236400, -1.552500, TRUE),
(209, 4, '44 Rue du Commerce', 'Strasbourg', '67000', 'France', 48.592600, 7.754300, TRUE),
(210, 4, '51 Rue du Commerce', 'Montpellier', '34000', 'France', 43.632300, 3.880500, TRUE),
(211, 4, '58 Rue du Commerce', 'Bordeaux', '33000', 'France', 44.853400, -0.582500, TRUE),
(212, 4, '65 Rue du Commerce', 'Lille', '59000', 'France', 50.646000, 3.055100, TRUE),
(213, 4, '72 Rue du Commerce', 'Rennes', '35000', 'France', 48.135300, -1.678900, TRUE),
(214, 4, '79 Rue du Commerce', 'Reims', '51100', 'France', 49.277500, 4.031700, TRUE),
(215, 4, '86 Rue du Commerce', 'Le Havre', '76600', 'France', 49.514800, 0.109000, TRUE),
(216, 4, '93 Rue du Commerce', 'Saint-Etienne', '42000', 'France', 45.455300, 4.389400, TRUE),
(217, 4, '100 Rue du Commerce', 'Toulon', '83000', 'France', 43.141000, 5.931300, TRUE),
(218, 4, '107 Rue du Commerce', 'Grenoble', '38000', 'France', 45.206500, 5.721200, TRUE),
(219, 4, '114 Rue du Commerce', 'Dijon', '21000', 'France', 47.341200, 5.039300, TRUE),
(220, 4, '121 Rue du Commerce', 'Angers', '49000', 'France', 47.498800, -0.564300, TRUE),
(221, 4, '128 Rue du Commerce', 'Nimes', '30000', 'France', 43.852300, 4.360100, TRUE),
(222, 4, '135 Rue du Commerce', 'Villeurbanne', '69100', 'France', 45.782800, 4.881100, TRUE),
(223, 4, '142 Rue du Commerce', 'Clermont-Ferrand', '63000', 'France', 45.795200, 3.089200, TRUE),
(224, 4, '149 Rue du Commerce', 'Le Mans', '72000', 'France', 48.025300, 0.202900, TRUE),
(225, 4, '14 Rue du Commerce', 'Aix-en-Provence', '13100', 'France', 43.550100, 5.444100, TRUE),
(226, 4, '21 Rue du Commerce', 'Brest', '29200', 'France', 48.406000, -4.488300, TRUE),
(227, 4, '28 Rue du Commerce', 'Tours', '37000', 'France', 47.410900, 0.683700, TRUE),
(228, 4, '35 Rue du Commerce', 'Amiens', '80000', 'France', 49.912100, 2.295800, TRUE),
(229, 4, '42 Rue du Commerce', 'Limoges', '87000', 'France', 45.852800, 1.262200, TRUE),
(230, 4, '49 Rue du Commerce', 'Annecy', '74000', 'France', 45.919600, 6.131600, TRUE),
(231, 4, '56 Rue du Commerce', 'Perpignan', '66000', 'France', 42.704300, 2.898100, TRUE),
(232, 4, '63 Rue du Commerce', 'Metz', '57000', 'France', 49.136100, 6.172400, TRUE),
(233, 4, '70 Rue du Commerce', 'Besancon', '25000', 'France', 47.255800, 6.021900, TRUE),
(234, 4, '77 Rue du Commerce', 'Orleans', '45000', 'France', 47.922100, 1.908200, TRUE),
(235, 4, '84 Rue du Commerce', 'Mulhouse', '68100', 'France', 47.771200, 7.335900, TRUE),
(236, 4, '91 Rue du Commerce', 'Rouen', '76000', 'France', 49.458700, 1.100400, TRUE),
(237, 4, '98 Rue du Commerce', 'Caen', '14000', 'France', 49.199700, -0.368500, TRUE),
(238, 4, '105 Rue du Commerce', 'Nancy', '54000', 'France', 48.710100, 6.187700, TRUE),
(239, 4, '112 Rue du Commerce', 'Roubaix', '59100', 'France', 50.711900, 3.171300, TRUE),
(240, 4, '119 Rue du Commerce', 'Tourcoing', '59200', 'France', 50.744300, 3.159000, TRUE),
(241, 4, '126 Rue du Commerce', 'Avignon', '84000', 'France', 43.964900, 4.804400, TRUE),
(242, 4, '133 Rue du Commerce', 'Poitiers', '86000', 'France', 46.597000, 0.340400, TRUE),
(243, 4, '140 Rue du Commerce', 'Pau', '64000', 'France', 43.313100, -0.369700, TRUE),
(244, 4, '147 Rue du Commerce', 'La Rochelle', '17000', 'France', 46.179500, -1.148900, TRUE),
(245, 4, '12 Rue du Commerce', 'Calais', '62100', 'France', 50.971700, 1.862000, TRUE),
(246, 4, '19 Rue du Commerce', 'Dunkerque', '59140', 'France', 51.050000, 2.373500, TRUE),
(247, 4, '26 Rue du Commerce', 'Valence', '26000', 'France', 44.950200, 4.890200, TRUE),
(248, 4, '33 Rue du Commerce', 'Chambery', '73000', 'France', 45.582600, 5.916700, TRUE),
(249, 4, '40 Rue du Commerce', 'Colmar', '68000', 'France', 48.098600, 7.358500, TRUE),
(250, 4, '47 Rue du Commerce', 'Ajaccio', '20000', 'France', 41.939600, 8.739700, TRUE),
(251, 4, '54 Rue du Commerce', 'Bastia', '20200', 'France', 42.712900, 9.453100, TRUE),
(252, 4, '61 Rue du Commerce', 'Nanterre', '92000', 'France', 48.909200, 2.209900, TRUE),
(253, 4, '68 Rue du Commerce', 'Creteil', '94000', 'France', 48.808400, 2.452300, TRUE),
(254, 4, '75 Rue du Commerce', 'Versailles', '78000', 'France', 48.824100, 2.118200, TRUE),
(255, 4, '82 Rue du Commerce', 'Cergy', '95000', 'France', 49.056800, 2.075000, TRUE),
(256, 4, '89 Rue du Commerce', 'Evry-Courcouronnes', '91000', 'France', 48.648400, 2.440000, TRUE),
(257, 4, '96 Rue du Commerce', 'Meaux', '77100', 'France', 48.977200, 2.889400, TRUE),
(258, 4, '103 Rue du Commerce', 'Melun', '77000', 'France', 48.557000, 2.662500, TRUE),
(259, 4, '110 Rue du Commerce', 'Troyes', '10000', 'France', 48.316500, 4.077700, TRUE),
(260, 4, '117 Rue du Commerce', 'Chalon-sur-Saone', '71100', 'France', 46.801500, 4.850400, TRUE),
(261, 4, '124 Rue du Commerce', 'Macon', '71000', 'France', 46.321600, 4.826500, TRUE),
(262, 4, '131 Rue du Commerce', 'Bayonne', '64100', 'France', 43.509700, -1.475900, TRUE),
(263, 4, '138 Rue du Commerce', 'Biarritz', '64200', 'France', 43.501200, -1.558600, TRUE),
(264, 4, '145 Rue du Commerce', 'Tarbes', '65000', 'France', 43.251700, 0.079200, TRUE),
(265, 4, '10 Rue du Commerce', 'Vannes', '56000', 'France', 47.678600, -2.758600, TRUE),
(266, 4, '17 Rue du Commerce', 'Quimper', '29000', 'France', 48.011700, -4.099200, TRUE),
(267, 4, '24 Rue du Commerce', 'Lorient', '56100', 'France', 47.765100, -3.373500, TRUE),
(268, 4, '31 Rue du Commerce', 'Saint-Malo', '35400', 'France', 48.667300, -2.027900, TRUE),
(269, 4, '38 Rue du Commerce', 'Laval', '53000', 'France', 48.089200, -0.774100, TRUE),
(270, 4, '45 Rue du Commerce', 'Niort', '79000', 'France', 46.344100, -0.458800, TRUE),
(271, 4, '52 Rue du Commerce', 'Cholet', '49300', 'France', 47.074400, -0.878600, TRUE),
(272, 4, '59 Rue du Commerce', 'Brive-la-Gaillarde', '19100', 'France', 45.175800, 1.535300, TRUE),
(273, 4, '66 Rue du Commerce', 'Bourges', '18000', 'France', 47.099000, 2.402100, TRUE),
(274, 4, '73 Rue du Commerce', 'Chartres', '28000', 'France', 48.463100, 1.485700, TRUE),
(275, 4, '80 Rue du Commerce', 'Beauvais', '60000', 'France', 49.449900, 2.078500, TRUE),
(276, 4, '87 Rue du Commerce', 'Compiegne', '60200', 'France', 49.433500, 2.825000, TRUE),
(277, 4, '94 Rue du Commerce', 'Narbonne', '11100', 'France', 43.201100, 3.003100, TRUE),
(278, 4, '101 Rue du Commerce', 'Beziers', '34500', 'France', 43.362200, 3.216900, TRUE),
(279, 4, '108 Rue du Commerce', 'Cannes', '06400', 'France', 43.572000, 7.019600, TRUE),
(280, 4, '115 Rue du Commerce', 'Antibes', '06600', 'France', 43.600800, 7.128400, TRUE),
(281, 4, '122 Rue du Commerce', 'Frejus', '83600', 'France', 43.448800, 6.733700, TRUE),
(282, 4, '129 Rue du Commerce', 'La Seyne-sur-Mer', '83500', 'France', 43.119900, 5.876600, TRUE),
(283, 4, '136 Rue du Commerce', 'Saint-Nazaire', '44600', 'France', 47.291500, -2.214800, TRUE),
(284, 4, '143 Rue du Commerce', 'La Roche-sur-Yon', '85000', 'France', 46.689700, -1.426300, TRUE),
(285, 4, '8 Rue du Commerce', 'Montauban', '82000', 'France', 44.038000, 1.355100, TRUE),
(286, 4, '15 Rue du Commerce', 'Albi', '81000', 'France', 43.940600, 2.150200, TRUE),
(287, 4, '22 Rue du Commerce', 'Carcassonne', '11000', 'France', 43.229800, 2.352400, TRUE),
(288, 4, '29 Rue du Commerce', 'Agen', '47000', 'France', 44.222900, 0.617900, TRUE),
(289, 4, '36 Rue du Commerce', 'Bergerac', '24100', 'France', 44.870300, 0.481600, TRUE),
(290, 4, '43 Rue du Commerce', 'Mont-de-Marsan', '40000', 'France', 43.910600, -0.498200, TRUE),
(291, 4, '50 Rue du Commerce', 'Gap', '05000', 'France', 44.573600, 6.078600, TRUE),
(292, 4, '57 Rue du Commerce', 'La Valette-du-Var', '83160', 'France', 43.153800, 5.985500, TRUE),
(293, 4, '64 Rue du Commerce', 'Cagnes-sur-Mer', '06800', 'France', 43.681500, 7.150100, TRUE),
(294, 4, '71 Rue du Commerce', 'Argenteuil', '95100', 'France', 48.966400, 2.250000, TRUE),
(295, 4, '78 Rue du Commerce', 'Montreuil', '93100', 'France', 48.884200, 2.445200, TRUE),
(296, 4, '85 Rue du Commerce', 'Boulogne-Billancourt', '92100', 'France', 48.850800, 2.238700, TRUE),
(297, 4, '92 Rue du Commerce', 'Noisy-le-Grand', '93160', 'France', 48.865400, 2.561500, TRUE),
(298, 4, '99 Rue du Commerce', 'Saint-Denis', '93200', 'France', 48.954200, 2.357400, TRUE),
(299, 4, '106 Rue du Commerce', 'Levallois-Perret', '92300', 'France', 48.912400, 2.289700, TRUE),
(300, 4, '113 Rue du Commerce', 'Ivry-sur-Seine', '94200', 'France', 48.833500, 2.390400, TRUE),
(301, 4, '120 Rue du Commerce', 'Aulnay-sous-Bois', '93600', 'France', 48.953700, 2.498200, TRUE),
(302, 4, '127 Rue du Commerce', 'Drancy', '93700', 'France', 48.943500, 2.441700, TRUE),
(303, 4, '134 Rue du Commerce', 'Massy', '91300', 'France', 48.748900, 2.268800, TRUE),
(304, 4, '141 Boulevard du General de Gaulle', 'Paris', '75001', 'France', 48.839800, 2.355100, TRUE),
(305, 4, '148 Boulevard du General de Gaulle', 'Marseille', '13001', 'France', 43.280900, 5.373800, TRUE),
(306, 4, '13 Boulevard du General de Gaulle', 'Lyon', '69002', 'France', 45.743600, 4.840800, TRUE),
(307, 4, '20 Boulevard du General de Gaulle', 'Toulouse', '31000', 'France', 43.585500, 1.450400, TRUE),
(308, 4, '27 Boulevard du General de Gaulle', 'Nice', '06000', 'France', 43.692200, 7.269300, TRUE),
(309, 4, '34 Boulevard du General de Gaulle', 'Nantes', '44000', 'France', 47.201600, -1.552900, TRUE),
(310, 4, '41 Boulevard du General de Gaulle', 'Strasbourg', '67000', 'France', 48.557800, 7.753900, TRUE),
(311, 4, '48 Boulevard du General de Gaulle', 'Montpellier', '34000', 'France', 43.591500, 3.880100, TRUE),
(312, 4, '55 Boulevard du General de Gaulle', 'Bordeaux', '33000', 'France', 44.818600, -0.575200, TRUE),
(313, 4, '62 Boulevard du General de Gaulle', 'Lille', '59000', 'France', 50.611200, 3.062400, TRUE),
(314, 4, '69 Boulevard du General de Gaulle', 'Rennes', '35000', 'France', 48.100500, -1.671600, TRUE),
(315, 4, '76 Boulevard du General de Gaulle', 'Reims', '51100', 'France', 49.242700, 4.039000, TRUE),
(316, 4, '83 Boulevard du General de Gaulle', 'Le Havre', '76600', 'France', 49.474000, 0.108600, TRUE),
(317, 4, '90 Boulevard du General de Gaulle', 'Saint-Etienne', '42000', 'France', 45.420500, 4.389000, TRUE);

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
-- AVIS PRODUITS
-- ------------------------------------------------------------

INSERT INTO avis_produit (
    id_client, id_produit, id_statut_avis, note, commentaire,
    id_employe_moderateur, date_creation, date_moderation
)
SELECT
    (SELECT id_client FROM client WHERE pseudo = 'gamermax' LIMIT 1),
    (SELECT id_produit FROM produit WHERE slug = 'marvel-spider-man-ps4' LIMIT 1),
    (SELECT id_statut_avis FROM statut_avis WHERE code = 'APPROUVE' LIMIT 1),
    5,
    'Excellent jeu, tres fluide et fun. Les combats sont vraiment reussis.',
    1,
    DATE_SUB(NOW(), INTERVAL 12 DAY),
    DATE_SUB(NOW(), INTERVAL 11 DAY)
WHERE NOT EXISTS (
    SELECT 1
    FROM avis_produit ap
    WHERE ap.id_client = (SELECT id_client FROM client WHERE pseudo = 'gamermax' LIMIT 1)
      AND ap.id_produit = (SELECT id_produit FROM produit WHERE slug = 'marvel-spider-man-ps4' LIMIT 1)
);

INSERT INTO avis_produit (
    id_client, id_produit, id_statut_avis, note, commentaire,
    id_employe_moderateur, date_creation, date_moderation
)
SELECT
    (SELECT id_client FROM client WHERE pseudo = 'nintendofan' LIMIT 1),
    (SELECT id_produit FROM produit WHERE slug = 'mario-kart-8-deluxe-switch' LIMIT 1),
    (SELECT id_statut_avis FROM statut_avis WHERE code = 'APPROUVE' LIMIT 1),
    4,
    'Tres bon jeu en multi, toujours aussi efficace en famille ou entre amis.',
    1,
    DATE_SUB(NOW(), INTERVAL 9 DAY),
    DATE_SUB(NOW(), INTERVAL 8 DAY)
WHERE NOT EXISTS (
    SELECT 1
    FROM avis_produit ap
    WHERE ap.id_client = (SELECT id_client FROM client WHERE pseudo = 'nintendofan' LIMIT 1)
      AND ap.id_produit = (SELECT id_produit FROM produit WHERE slug = 'mario-kart-8-deluxe-switch' LIMIT 1)
);

INSERT INTO avis_produit (
    id_client, id_produit, id_statut_avis, note, commentaire,
    id_employe_moderateur, date_creation, date_moderation
)
SELECT
    (SELECT id_client FROM client WHERE pseudo = 'cardmaster' LIMIT 1),
    (SELECT id_produit FROM produit WHERE slug = 'halo-infinite-xbox-series' LIMIT 1),
    (SELECT id_statut_avis FROM statut_avis WHERE code = 'APPROUVE' LIMIT 1),
    4,
    'Campagne solide et multijoueur agreable. Tres bon rendu global.',
    1,
    DATE_SUB(NOW(), INTERVAL 6 DAY),
    DATE_SUB(NOW(), INTERVAL 5 DAY)
WHERE NOT EXISTS (
    SELECT 1
    FROM avis_produit ap
    WHERE ap.id_client = (SELECT id_client FROM client WHERE pseudo = 'cardmaster' LIMIT 1)
      AND ap.id_produit = (SELECT id_produit FROM produit WHERE slug = 'halo-infinite-xbox-series' LIMIT 1)
);

INSERT INTO avis_produit (
    id_client, id_produit, id_statut_avis, note, commentaire,
    id_employe_moderateur, date_creation, date_moderation
)
SELECT
    (SELECT id_client FROM client WHERE pseudo = 'cardmaster' LIMIT 1),
    (SELECT id_produit FROM produit WHERE slug = 'forza-horizon-5-xbox-series' LIMIT 1),
    (SELECT id_statut_avis FROM statut_avis WHERE code = 'APPROUVE' LIMIT 1),
    5,
    'Superbe jeu de course, tres beau visuellement et vraiment plaisant a jouer.',
    1,
    DATE_SUB(NOW(), INTERVAL 4 DAY),
    DATE_SUB(NOW(), INTERVAL 3 DAY)
WHERE NOT EXISTS (
    SELECT 1
    FROM avis_produit ap
    WHERE ap.id_client = (SELECT id_client FROM client WHERE pseudo = 'cardmaster' LIMIT 1)
      AND ap.id_produit = (SELECT id_produit FROM produit WHERE slug = 'forza-horizon-5-xbox-series' LIMIT 1)
);

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
