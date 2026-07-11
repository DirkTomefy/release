-- Active: 1779074118545@@127.0.0.1@5432@bovin_db
-- ============================================================
-- Migration : ajout de la cause (raison) sur chaque mouvement de caisse.
-- La table cause_caisse existe déjà (voir database/final/final.sql),
-- mais aucune colonne ne la reliait à mvt_caisse.
--
-- IMPORTANT : spring.jpa.hibernate.ddl-auto=none, donc ce script doit
-- être exécuté manuellement AVANT de relancer l'application avec le
-- nouveau code (MvtCaisse.causeCaisse est désormais @JoinColumn non-null).
-- ============================================================

-- 1. Seed obligatoire : le code recherche la cause par son LIBELLE exact
--    (insensible à la casse). Vérifiez que ces lignes existent (adaptez
--    si elles existent déjà sous d'autres id) :
INSERT INTO cause_caisse (libelle)
SELECT 'STOCK'
WHERE NOT EXISTS (SELECT 1 FROM cause_caisse WHERE lower(libelle) = 'stock');

INSERT INTO cause_caisse (libelle)
SELECT 'ACHAT_BOVIN'
WHERE NOT EXISTS (SELECT 1 FROM cause_caisse WHERE lower(libelle) = 'achat_bovin');

INSERT INTO cause_caisse (libelle)
SELECT 'ACHAT'
WHERE NOT EXISTS (SELECT 1 FROM cause_caisse WHERE lower(libelle) = 'achat');

INSERT INTO cause_caisse (libelle)
SELECT 'PAYEMENT'
WHERE NOT EXISTS (SELECT 1 FROM cause_caisse WHERE lower(libelle) = 'payement');

INSERT INTO cause_caisse (libelle)
SELECT 'VENTE'
WHERE NOT EXISTS (SELECT 1 FROM cause_caisse WHERE lower(libelle) = 'vente');

INSERT INTO cause_caisse (libelle)
SELECT 'AUTRE'
WHERE NOT EXISTS (SELECT 1 FROM cause_caisse WHERE lower(libelle) = 'autre');

-- 2. Nouvelle colonne sur mvt_caisse : la cause du mouvement
ALTER TABLE mvt_caisse
    ADD COLUMN IF NOT EXISTS id_cause_caisse INTEGER;

-- Rétro-remplissage : pour les mouvements déjà existants, on ne peut pas
-- reconstituer la cause d'origine exacte, donc on les rattache à 'AUTRE'.
-- Adaptez cette valeur si vous avez une autre source de vérité.
UPDATE mvt_caisse
SET id_cause_caisse = (SELECT id FROM cause_caisse WHERE lower(libelle) = 'autre')
WHERE id_cause_caisse IS NULL;

ALTER TABLE mvt_caisse
    ALTER COLUMN id_cause_caisse SET NOT NULL;

ALTER TABLE mvt_caisse
    ADD CONSTRAINT fk_mvt_caisse_cause FOREIGN KEY (id_cause_caisse)
        REFERENCES cause_caisse(id);
