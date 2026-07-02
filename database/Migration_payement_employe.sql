-- ============================================================
-- Migration nécessaire suite à l'ajout de 2 champs sur PayementEmployee
-- (le fichier z!caisse.sql n'était pas présent dans l'archive fournie,
-- donc ce script est écrit "à la main" à partir des entités JPA).
--
-- IMPORTANT : spring.jpa.hibernate.ddl-auto=update va tenter d'ajouter
-- les colonnes automatiquement au démarrage, MAIS s'il y a déjà des
-- lignes dans payement_employee, l'ajout en NOT NULL échouera.
-- => Exécuter ce script AVANT de relancer l'application.
-- ============================================================

-- 1. Nouveau champ : mois concerné par le paiement (1er jour du mois),
--    distinct de date_payement qui reste la date réelle de la transaction.
ALTER TABLE payement_employee
    ADD COLUMN IF NOT EXISTS mois DATE;

-- Rétro-remplissage : pour les paiements déjà existants, on suppose que
-- le mois payé correspond au mois de la date de paiement (à ajuster si
-- vous avez des cas de paiements en retard déjà enregistrés).
UPDATE payement_employee
SET mois = date_trunc('month', date_payement)::date
WHERE mois IS NULL;

ALTER TABLE payement_employee
    ALTER COLUMN mois SET NOT NULL;

-- 2. Nouveau champ : montant réellement versé lors de la transaction
--    (jusqu'ici, ce montant n'était nulle part enregistré !)
ALTER TABLE payement_employee
    ADD COLUMN IF NOT EXISTS montant NUMERIC(12,2);

-- Rétro-remplissage approximatif pour les lignes existantes : on ne peut
-- pas reconstituer le montant exact historique, donc on met 0 par défaut.
-- Adaptez cette valeur si vous avez une autre source de vérité.
UPDATE payement_employee
SET montant = 0
WHERE montant IS NULL;

ALTER TABLE payement_employee
    ALTER COLUMN montant SET NOT NULL;

-- 3. Seed indispensable : la logique métier (Salaire / Avance / Sanction)
--    se base sur le LIBELLE exact (insensible à la casse). Vérifiez que
--    ces 3 lignes existent (adaptez si elles existent déjà sous d'autres id) :
INSERT INTO type_payement_employee (libelle)
SELECT 'Salaire'
WHERE NOT EXISTS (SELECT 1 FROM type_payement_employee WHERE lower(libelle) = 'salaire');

INSERT INTO type_payement_employee (libelle)
SELECT 'Avance'
WHERE NOT EXISTS (SELECT 1 FROM type_payement_employee WHERE lower(libelle) = 'avance');

INSERT INTO type_payement_employee (libelle)
SELECT 'Sanction'
WHERE NOT EXISTS (SELECT 1 FROM type_payement_employee WHERE lower(libelle) = 'sanction');