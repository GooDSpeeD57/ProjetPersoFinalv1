-- ══════════════════════════════════════════════════════════════════════════════
-- MIGRATION V19 — Table produit_screenshot
-- Contexte : les screenshots / artworks sont communs à toutes les versions
--            d'un produit (PS4, PS5, Switch…). On les sépare des jaquettes
--            (produit_image → id_variant) dans une table dédiée au niveau produit,
--            sur le même modèle que produit_video.
-- ══════════════════════════════════════════════════════════════════════════════

USE micromania;

CREATE TABLE IF NOT EXISTS produit_screenshot (
    id_screenshot   BIGINT          NOT NULL AUTO_INCREMENT,
    id_produit      BIGINT          NOT NULL,
    url             VARCHAR(255)    NOT NULL,
    alt             VARCHAR(255)    NOT NULL DEFAULT '',
    ordre_affichage INT             NOT NULL DEFAULT 0,
    date_creation   DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,

    PRIMARY KEY (id_screenshot),
    CONSTRAINT fk_screenshot_produit
        FOREIGN KEY (id_produit) REFERENCES produit(id_produit)
        ON DELETE CASCADE
);

CREATE INDEX idx_screenshot_produit ON produit_screenshot(id_produit, ordre_affichage);

-- ══════════════════════════════════════════════════════════════════════════════
