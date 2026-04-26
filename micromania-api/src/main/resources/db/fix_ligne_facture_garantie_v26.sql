ALTER TABLE ligne_facture
    ADD COLUMN garantie_label VARCHAR(200) NULL,
    ADD COLUMN garantie_prix  DECIMAL(10,2) NULL;

DROP TRIGGER IF EXISTS trg_facture_total_after_insert;
DROP TRIGGER IF EXISTS trg_facture_total_after_update;
DROP TRIGGER IF EXISTS trg_facture_total_after_delete;

DELIMITER $$

CREATE TRIGGER trg_facture_total_after_insert
AFTER INSERT ON ligne_facture FOR EACH ROW
BEGIN
    UPDATE facture
    SET montant_total     = (SELECT IFNULL(SUM(montant_ligne + IFNULL(garantie_prix,0)),0) FROM ligne_facture WHERE id_facture = NEW.id_facture),
        montant_ht_total  = (SELECT IFNULL(SUM(montant_ht_ligne),0)                        FROM ligne_facture WHERE id_facture = NEW.id_facture),
        montant_tva_total = (SELECT IFNULL(SUM(montant_tva_ligne),0)                       FROM ligne_facture WHERE id_facture = NEW.id_facture),
        montant_final     = GREATEST(0,(SELECT IFNULL(SUM(montant_ligne + IFNULL(garantie_prix,0)),0) FROM ligne_facture WHERE id_facture = NEW.id_facture) - montant_remise)
    WHERE id_facture = NEW.id_facture;
END$$

CREATE TRIGGER trg_facture_total_after_update
AFTER UPDATE ON ligne_facture FOR EACH ROW
BEGIN
    UPDATE facture
    SET montant_total     = (SELECT IFNULL(SUM(montant_ligne + IFNULL(garantie_prix,0)),0) FROM ligne_facture WHERE id_facture = NEW.id_facture),
        montant_ht_total  = (SELECT IFNULL(SUM(montant_ht_ligne),0)                        FROM ligne_facture WHERE id_facture = NEW.id_facture),
        montant_tva_total = (SELECT IFNULL(SUM(montant_tva_ligne),0)                       FROM ligne_facture WHERE id_facture = NEW.id_facture),
        montant_final     = GREATEST(0,(SELECT IFNULL(SUM(montant_ligne + IFNULL(garantie_prix,0)),0) FROM ligne_facture WHERE id_facture = NEW.id_facture) - montant_remise)
    WHERE id_facture = NEW.id_facture;
END$$

CREATE TRIGGER trg_facture_total_after_delete
AFTER DELETE ON ligne_facture FOR EACH ROW
BEGIN
    UPDATE facture
    SET montant_total     = (SELECT IFNULL(SUM(montant_ligne + IFNULL(garantie_prix,0)),0) FROM ligne_facture WHERE id_facture = OLD.id_facture),
        montant_ht_total  = (SELECT IFNULL(SUM(montant_ht_ligne),0)                        FROM ligne_facture WHERE id_facture = OLD.id_facture),
        montant_tva_total = (SELECT IFNULL(SUM(montant_tva_ligne),0)                       FROM ligne_facture WHERE id_facture = OLD.id_facture),
        montant_final     = GREATEST(0,(SELECT IFNULL(SUM(montant_ligne + IFNULL(garantie_prix,0)),0) FROM ligne_facture WHERE id_facture = OLD.id_facture) - montant_remise)
    WHERE id_facture = OLD.id_facture;
END$$

DELIMITER ;
