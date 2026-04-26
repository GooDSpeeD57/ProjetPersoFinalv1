-- V23 : Recrée les lignes de categorie dans le bon ordre avec IDs stables
-- (correspondance exacte avec type_categorie : 1=JEU, 2=CONSOLE, 3=ACCESSOIRE, 4=GOODIES, 5=CARTE_CADEAU, 6=TCG)

SET FOREIGN_KEY_CHECKS = 0;

TRUNCATE TABLE categorie;

INSERT INTO categorie (id_categorie, id_type_categorie, nom, description, actif) VALUES
(1, 1, 'Jeux Video',   'Jeux toutes plateformes',  TRUE),
(2, 2, 'Consoles',     'Consoles de jeux',          TRUE),
(3, 3, 'Accessoires',  'Accessoires gaming',        TRUE),
(4, 4, 'Goodies',      'Produits derives gaming',   TRUE),
(5, 5, 'Carte Cadeau', 'Cartes cadeaux',            TRUE),
(6, 6, 'Cartes TCG',   'Trading card games',        TRUE);

SET FOREIGN_KEY_CHECKS = 1;
