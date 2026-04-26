-- V25 : Ajout de la garantie optionnelle sur ligne_panier
ALTER TABLE ligne_panier
    ADD COLUMN id_type_garantie BIGINT NULL,
    ADD CONSTRAINT fk_ligne_panier_garantie
        FOREIGN KEY (id_type_garantie) REFERENCES type_garantie(id_type_garantie) ON DELETE SET NULL;
