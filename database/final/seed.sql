-- ============================================================
-- Script d'insertion de données de test (Madagascar, Ariary)
-- À exécuter après la création des tables.
-- (Sans inventaire_bovin ni inventaire_bovin_detail)
-- ============================================================

-- Nettoyage des données existantes (ordre inverse des dépendances)
DELETE FROM facture_detail;
DELETE FROM facture;
DELETE FROM inventaire_detail;
DELETE FROM inventaire;
DELETE FROM mvt_stock_paiement;
DELETE FROM mouvement_stock;
DELETE FROM materiel;
DELETE FROM type_materiel;
DELETE FROM vente_detail;
DELETE FROM vente_bovin;
DELETE FROM client;
DELETE FROM pese_bovin;
DELETE FROM bovin;
DELETE FROM mvt_caisse;
DELETE FROM cause_caisse;
DELETE FROM caisse;
DELETE FROM payement_employee;
DELETE FROM contrat;
DELETE FROM employee;
DELETE FROM race;
DELETE FROM type_payement_employee;

-- Réinitialisation des séquences (optionnel)
ALTER SEQUENCE caisse_id_seq RESTART WITH 1;
ALTER SEQUENCE cause_caisse_id_seq RESTART WITH 1;
ALTER SEQUENCE race_id_seq RESTART WITH 1;
ALTER SEQUENCE bovin_id_seq RESTART WITH 1;
ALTER SEQUENCE pese_bovin_id_seq RESTART WITH 1;
ALTER SEQUENCE client_id_seq RESTART WITH 1;
ALTER SEQUENCE vente_bovin_id_seq RESTART WITH 1;
ALTER SEQUENCE vente_detail_id_seq RESTART WITH 1;
ALTER SEQUENCE type_materiel_id_seq RESTART WITH 1;
ALTER SEQUENCE materiel_id_seq RESTART WITH 1;
ALTER SEQUENCE mouvement_stock_id_seq RESTART WITH 1;
ALTER SEQUENCE inventaire_id_seq RESTART WITH 1;
ALTER SEQUENCE inventaire_detail_id_seq RESTART WITH 1;
ALTER SEQUENCE employee_id_seq RESTART WITH 1;
ALTER SEQUENCE contrat_id_seq RESTART WITH 1;
ALTER SEQUENCE payement_employee_id_seq RESTART WITH 1;
ALTER SEQUENCE facture_id_seq RESTART WITH 1;
ALTER SEQUENCE facture_detail_id_seq RESTART WITH 1;

-- ============================================================
-- 1. Types de paiement employé
-- ============================================================
INSERT INTO type_payement_employee (libelle) VALUES
    ('Salaire'),
    ('Avance'),
    ('Sanction');

-- ============================================================
-- 2. Races
-- ============================================================
INSERT INTO race (nom, descriptions) VALUES
    ('Holstein', 'Race laitière d''origine néerlandaise'),
    ('Charolaise', 'Race à viande originaire de France'),
    ('Limousine', 'Race à viande réputée pour sa qualité'),
    ('Blonde d''Aquitaine', 'Race à viande du sud-ouest de la France'),
    ('Normande', 'Race mixte laitière et viande'),
    ('Salers', 'Race à viande et laitière du Massif Central'),
    ('Montbéliarde', 'Race laitière de l''est de la France'),
    ('Abondance', 'Race laitière des Alpes');

-- ============================================================
-- 3. Caisses (montants en Ariary)
-- ============================================================
INSERT INTO caisse (libelle, montant_actuelle) VALUES
    ('Caisse principale', 15000000.00),    -- 15 millions Ar
    ('Caisse d''épargne', 8000000.00),     -- 8 millions Ar
    ('Fonds d''investissement', 20000000.00); -- 20 millions Ar

-- ============================================================
-- 4. Causes de mouvements de caisse
-- ============================================================
INSERT INTO cause_caisse (libelle) VALUES
    ('STOCK'),
    ('ACHAT_BOVIN'),
    ('ACHAT_MATERIEL'),
    ('PAYEMENT_EMPLOYE'),
    ('VENTE'),
    ('AUTRE');

-- ============================================================
-- 5. Mouvements initiaux de caisse (pour solde initial)
-- ============================================================
INSERT INTO mvt_caisse (date, montant, id_caisse, id_cause_caisse)
SELECT
    CURRENT_DATE,
    c.montant_actuelle,
    c.id,
    cc.id
FROM caisse c
JOIN cause_caisse cc ON cc.libelle = 'STOCK';

-- ============================================================
-- 6. Bovins (prix en Ariary, poids en kg)
-- ============================================================
INSERT INTO bovin (id_race, date_achat, date_vente, prix_achat, prix_vente, poids_achat, poids_vente) VALUES
    (1, '2020-03-12', NULL, 1500000.00, NULL, 100, NULL),
    (2, '2019-07-05', '2021-06-15', 1800000.00, 5000000.00, 120, NULL),
    (3, '2021-01-20', '2022-03-10', 1200000.00, 4500000.00, 95, NULL),
    (4, '2022-06-14', NULL, 950000.00, NULL, 88, NULL),
    (5, '2023-09-01', NULL, 1100000.00, NULL, 92, NULL),
    (6, '2020-11-20', '2021-12-01', 1400000.00, 5200000.00, 105, NULL),
    (7, '2018-08-25', NULL, 2000000.00, NULL, 130, NULL),
    (8, '2021-05-10', '2023-02-28', 1600000.00, 6000000.00, 115, NULL),
    (1, '2022-10-03', NULL, 780000.00, NULL, 82, NULL),
    (2, '2023-02-15', NULL, 1300000.00, NULL, 98, NULL),
    (3, '2020-09-05', '2022-08-12', 1750000.00, 7000000.00, 125, NULL),
    (4, '2023-01-20', NULL, 1200000.00, NULL, 96, NULL),
    (5, '2021-11-15', '2023-05-01', 900000.00, 3800000.00, 90, NULL),
    (6, '2019-12-10', NULL, 1600000.00, NULL, 110, NULL),
    (7, '2022-04-22', NULL, 1100000.00, NULL, 94, NULL),
    (8, '2020-07-08', '2022-11-20', 1400000.00, 5500000.00, 108, NULL);

-- ============================================================
-- 7. Pesées initiales pour chaque bovin (à la date d'achat + 1 jour)
-- ============================================================
INSERT INTO pese_bovin (id_bovin, date_pese, poids_apres)
SELECT
    b.id,
    b.date_achat + INTERVAL '1 day',
    b.poids_achat + (random() * 5 - 2) -- léger écart
FROM bovin b;

-- ============================================================
-- 8. Clients
-- ============================================================
INSERT INTO client (nom, prenom, contact) VALUES
    ('Rakotondrazaka', 'Hery', '0341203345'),
    ('Rasoanaivo', 'Miora', '0324517789'),
    ('Andriamihaja', 'Tiana', '0331459982'),
    ('Ravelomanantsoa', 'Feno', '0348852211'),
    ('Razafindrakoto', 'Nirina', '0327704412'),
    ('Rabemananjara', 'Toky', '0335521900'),
    ('Ratsimbazafy', 'Voahangy', '0346198227'),
    ('Andrianarison', 'Lova', '0324441788'),
    ('Ranaivoarisoa', 'Harena', '0338076614'),
    ('Rafidimanana', 'Aina', '0342785409');

-- ============================================================
-- 9. Ventes de bovins (avec date_vente)
-- ============================================================
INSERT INTO vente_bovin (id_client, description, date_vente) VALUES
    (1, 'Achat pour élevage familial à Ambatondrazaka', '2025-01-18'),
    (3, 'Revente pour boucherie locale Antsirabe', '2025-02-09'),
    (5, 'Achat pour fête traditionnelle', '2025-03-21'),
    (2, 'Constitution de troupeau de reproduction', '2025-04-12'),
    (8, 'Achat de bovins pour engraissement', '2025-05-04'),
    (10, 'Approvisionnement de restaurant viande', '2025-05-29'),
    (6, 'Achat mixte production et revente', '2025-06-14'),
    (4, 'Vente directe marché de gros', '2025-06-30');

-- ============================================================
-- 10. Détails des ventes (bovins vendus)
-- ============================================================
INSERT INTO vente_detail (id_vente, id_bovin) VALUES
    (1, 2),
    (1, 3),
    (2, 6),
    (3, 8),
    (4, 11),
    (4, 13),
    (5, 15),
    (6, 16),
    (7, 5),
    (8, 10);

-- Mise à jour des dates de vente et prix_vente des bovins concernés
UPDATE bovin SET date_vente = v.date_vente, prix_vente = 
    CASE b.id
        WHEN 2 THEN 5000000
        WHEN 3 THEN 4500000
        WHEN 6 THEN 5200000
        WHEN 8 THEN 6000000
        WHEN 11 THEN 7000000
        WHEN 13 THEN 3800000
        WHEN 15 THEN 4100000
        WHEN 16 THEN 5500000
        WHEN 5 THEN 4800000
        WHEN 10 THEN 4200000
    END
FROM vente_bovin v, vente_detail vd, bovin b
WHERE v.id = vd.id_vente AND vd.id_bovin = b.id
AND b.id IN (2,3,6,8,11,13,15,16,5,10);

-- ============================================================
-- 11. Mouvements de caisse liés aux ventes (paiements reçus)
-- ============================================================
INSERT INTO mvt_caisse (date, montant, id_caisse, id_cause_caisse)
SELECT
    v.date_vente,
    b.prix_vente,
    (SELECT id FROM caisse WHERE libelle = 'Caisse principale'),
    (SELECT id FROM cause_caisse WHERE libelle = 'VENTE')
FROM vente_bovin v
JOIN vente_detail vd ON vd.id_vente = v.id
JOIN bovin b ON b.id = vd.id_bovin
WHERE b.prix_vente IS NOT NULL;

-- ============================================================
-- 12. Matériel (avec unité dans le libellé)
-- ============================================================
INSERT INTO type_materiel (libelle) VALUES
    ('Aliment'),
    ('Vaccin'),
    ('Médicament'),
    ('Ustensile'),
    ('Autre');

INSERT INTO materiel (libelle, id_type_materiel, type_gestion) VALUES
    ('Provende (kg)', 1, 'FIFO'),
    ('Foin (botte)', 1, 'FIFO'),
    ('Vaccin Fièvre Aphteuse (dose)', 2, 'FIFO'),
    ('Vaccin Pasteurellose (dose)', 2, 'FIFO'),
    ('Démarrage (sac de 50kg)', 1, 'FIFO'),
    ('Antibiotique (flacon)', 3, 'FIFO'),
    ('Paille (balle)', 1, 'FIFO'),
    ('Brosse (unité)', 4, 'LIFO'),
    ('Seau (unité)', 4, 'LIFO'),
    ('Corde (mètre)', 4, 'LIFO');

-- ============================================================
-- 13. Mouvements de stock (entrées initiales)
-- ============================================================
INSERT INTO mouvement_stock (id_materiel, date_mouvement, type_mouvement, quantite, prix_unitaire, qte_restant)
SELECT
    m.id,
    CURRENT_DATE - (random() * 30)::int * INTERVAL '1 day',
    'ENTREE',
    (random() * 100 + 10)::int,
    (random() * 5000 + 500)::numeric(10,2),
    (random() * 100 + 10)::int
FROM materiel m;

-- ============================================================
-- 14. Paiements des mouvements de stock (depuis caisse)
-- ============================================================
INSERT INTO mvt_stock_paiement (id_mouvement_stock, id_caisse, montant)
SELECT
    ms.id,
    (SELECT id FROM caisse ORDER BY random() LIMIT 1),
    ms.quantite * ms.prix_unitaire
FROM mouvement_stock ms;

-- ============================================================
-- 15. Inventaire de matériel (état initial)
-- ============================================================
INSERT INTO inventaire (date_inventaire, libelle) VALUES
    (CURRENT_DATE, 'Inventaire initial');

INSERT INTO inventaire_detail (id_inventaire, id_materiel, quantite_initiale, quantite_finale, observations)
SELECT
    (SELECT id FROM inventaire WHERE libelle = 'Inventaire initial'),
    m.id,
    (SELECT qte_restant FROM mouvement_stock WHERE id_materiel = m.id ORDER BY date_mouvement DESC LIMIT 1),
    (SELECT qte_restant FROM mouvement_stock WHERE id_materiel = m.id ORDER BY date_mouvement DESC LIMIT 1),
    'Stock initial'
FROM materiel m;

-- ============================================================
-- 16. Employés et contrats (date_fin non NULL)
-- ============================================================
INSERT INTO employee (nom, prenom, date_naissance, date_entree) VALUES
    ('Rakoto', 'Jean', '1990-05-15', '2020-01-10'),
    ('Rasoa', 'Marie', '1985-08-22', '2019-06-01'),
    ('Andrianina', 'David', '1995-11-30', '2021-03-15');

-- Contrats avec date_fin obligatoire
INSERT INTO contrat (date_debut, date_fin, id_employee, salaire) VALUES
    ('2020-01-10', '2022-01-09', 1, 350000.00),   -- Contrat terminé
    ('2019-06-01', '2023-05-31', 2, 400000.00),   -- Contrat terminé
    ('2021-03-15', '2026-03-14', 3, 300000.00);   -- Contrat en cours

-- ============================================================
-- 17. Paiements employés (exemple)
-- ============================================================
INSERT INTO payement_employee (id_employee, id_type_payement_employee, date_payement, reste_paye, mois, montant)
SELECT
    e.id,
    (SELECT id FROM type_payement_employee WHERE libelle = 'Salaire'),
    CURRENT_DATE - (random() * 30)::int * INTERVAL '1 day',
    0,
    date_trunc('month', CURRENT_DATE - (random() * 30)::int * INTERVAL '1 day')::date,
    c.salaire
FROM employee e
JOIN contrat c ON c.id_employee = e.id
WHERE c.date_fin >= CURRENT_DATE; -- Seulement les contrats en cours pour les paiements récents

-- Ajout de quelques paiements pour les anciens contrats (exemple)
INSERT INTO payement_employee (id_employee, id_type_payement_employee, date_payement, reste_paye, mois, montant)
SELECT
    e.id,
    (SELECT id FROM type_payement_employee WHERE libelle = 'Salaire'),
    c.date_debut + INTERVAL '1 month',
    0,
    date_trunc('month', c.date_debut + INTERVAL '1 month')::date,
    c.salaire
FROM employee e
JOIN contrat c ON c.id_employee = e.id
WHERE c.date_fin < CURRENT_DATE
AND e.id IN (1,2); -- pour les deux anciens employés

-- ============================================================
-- 18. Factures (pour les ventes)
-- ============================================================
INSERT INTO facture (id_vente, numero_facture, code_facture, date_facture, montant_total)
SELECT
    v.id,
    'FACT-' || LPAD(v.id::text, 6, '0'),
    'CODE-' || LPAD(v.id::text, 6, '0'),
    v.date_vente,
    (SELECT SUM(b.prix_vente) FROM vente_detail vd JOIN bovin b ON b.id = vd.id_bovin WHERE vd.id_vente = v.id)
FROM vente_bovin v;

INSERT INTO facture_detail (id_facture, id_vente_detail, prix_unitaire, quantite)
SELECT
    f.id,
    vd.id,
    b.prix_vente,
    1
FROM facture f
JOIN vente_bovin v ON v.id = f.id_vente
JOIN vente_detail vd ON vd.id_vente = v.id
JOIN bovin b ON b.id = vd.id_bovin;

-- ============================================================
-- Fin du script
-- ============================================================