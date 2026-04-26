-- V22 : Lier type_garantie à une catégorie (nullable = applicable à toutes)
ALTER TABLE type_garantie
    ADD COLUMN id_categorie BIGINT NULL,
    ADD CONSTRAINT fk_tg_categorie
        FOREIGN KEY (id_categorie) REFERENCES categorie(id_categorie)
        ON DELETE SET NULL;

-- Index pour les requêtes filtrées
CREATE INDEX idx_type_garantie_categorie ON type_garantie(id_categorie);

-- Exemple de données : adapter les id_categorie à votre base
-- (laisser NULL = garantie proposée sur toutes les catégories)
-- UPDATE type_garantie SET id_categorie = (SELECT id_categorie FROM categorie WHERE nom = 'Jeux vidéo') WHERE code LIKE 'JEU%';
