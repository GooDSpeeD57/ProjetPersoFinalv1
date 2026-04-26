-- V24 : Insertion des types de garantie avec liaison catégorie
-- id_categorie : 1=Jeux Video | 2=Consoles | 3=Accessoires

INSERT INTO type_garantie (id_type_garantie, code, description, duree_mois, prix_extension, id_categorie) VALUES
(1, 'STANDARD_CONSOLE',     'Garantie standard console',         24, NULL,  2),
(2, 'STANDARD_ACCESSOIRE',  'Garantie standard accessoire',      12, NULL,  3),
(3, 'ETENDUE_CONSOLE',      'Extension console',                 12, 70.00, 2),
(4, 'ETENDUE_ACCESSOIRE',   'Extension accessoire',              12,  9.99, 3),
(5, 'OCCASION_CONSOLE',     'Garantie occasion console',          6, NULL,  2),
(6, 'ANTI_CASSE_JEU_NEUF',  'Garantie anti-casse jeu neuf',      12,  3.00, 1),
(7, 'ANTI_CASSE_JEU_OCC',   'Garantie anti-casse jeu occasion',   6,  3.00, 1);
