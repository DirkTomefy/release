-- ============================================================
-- Migration : ajout du module Mortalité
--
-- La table mortalite ne référence PAS le bovin (celui-ci est
-- supprimé de la table bovin au moment de la déclaration de
-- mortalité) : on conserve donc un instantané de ses infos utiles
-- (race, prix d'achat, poids au moment du décès) + la date.
--
-- IMPORTANT : spring.jpa.hibernate.ddl-auto=none, donc ce script
-- doit être exécuté manuellement sur la base bovin_db AVANT de
-- relancer l'application avec le nouveau module Mortalité.
-- ============================================================

CREATE TABLE IF NOT EXISTS mortalite (
    id SERIAL PRIMARY KEY,
    id_race INTEGER NOT NULL,
    prix_achat DOUBLE PRECISION NOT NULL,
    poids_mort DOUBLE PRECISION NOT NULL,
    date DATE NOT NULL,
    CONSTRAINT fk_mortalite_race FOREIGN KEY (id_race) REFERENCES race(id)
);

CREATE INDEX IF NOT EXISTS idx_mortalite_date ON mortalite(date);
CREATE INDEX IF NOT EXISTS idx_mortalite_id_race ON mortalite(id_race);
