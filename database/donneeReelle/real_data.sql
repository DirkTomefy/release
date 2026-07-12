-- ============================================================
-- real_data.sql
-- Jeu de donnees de test REALISTE et COMPLET pour bovin_db
-- Couvre toutes les fonctionnalites du projet :
--   - Races / Bovins / Pesees (historique de poids)
--   - Caisses / Mouvements de caisse
--   - Clients / Ventes de bovins (multi-bovins par vente)
--   - Employees / Contrats (y compris avenant / augmentation)
--   - Paiements employes : Salaire (normal, prorata, mois impaye
--     pour tester les alertes), Avance, Sanction
--
-- A executer sur une base dont le schema existe deja (cf. final.sql).
-- Le script est REJOUABLE : il vide les tables (TRUNCATE ... CASCADE)
-- avant de reinserer, en respectant l'ordre des dependances.
-- ============================================================

BEGIN;

-- ------------------------------------------------------------
-- 0. Nettoyage (ordre inverse des dependances)
-- ------------------------------------------------------------
TRUNCATE TABLE
    vente_detail,
    vente_bovin,
    client,
    mvt_caisse,
    payement_employee,
    type_payement_employee,
    contrat,
    employee,
    pese_bovin,
    bovin,
    race,
    caisse
    RESTART IDENTITY CASCADE;

-- ------------------------------------------------------------
-- 1. Races (10)
-- ------------------------------------------------------------
INSERT INTO race (nom, descriptions) VALUES
    ('Holstein',            'Race laitiere d''origine neerlandaise, tres haute production laitiere'),
    ('Charolaise',          'Race a viande originaire de France, forte croissance musculaire'),
    ('Limousine',           'Race a viande reputee pour la qualite de sa carcasse'),
    ('Blonde d''Aquitaine', 'Race a viande du sud-ouest de la France, tres bon rendement'),
    ('Normande',            'Race mixte laitiere et viande, rustique'),
    ('Salers',              'Race a viande et laitiere du Massif Central, tres rustique'),
    ('Montbeliarde',        'Race laitiere de l''est de la France, adaptee au fromage'),
    ('Abondance',           'Race laitiere des Alpes francaises'),
    ('Renitelo',            'Race bovine locale de Madagascar, tres rustique et resistante'),
    ('Zebu Malgache',       'Race bovine traditionnelle de Madagascar (Aombe), utilisee pour le travail et la viande');

-- ------------------------------------------------------------
-- 2. Caisses (4)
-- ------------------------------------------------------------
INSERT INTO caisse (libelle, montant_actuelle) VALUES
    ('Caisse principale',        18500000.00),
    ('Caisse d''epargne',         9200000.00),
    ('Fonds d''investissement',  25000000.00),
    ('Caisse paie employes',      4300000.00);

-- ------------------------------------------------------------
-- 3. Bovins (30) - melange vendus / non vendus, prix/poids varies
--    id_race references les 10 races ci-dessus (id 1 a 10)
-- ------------------------------------------------------------
INSERT INTO bovin (id_race, date_achat, date_vente, prix_achat, prix_vente, poids_achat, poids_vente) VALUES
    (1,  '2023-01-15', NULL,         1500000.00, NULL,        180, NULL),   -- 1  Holstein, en stock
    (2,  '2022-11-03', '2024-02-20', 1800000.00, 2600000.00,  210, 340),    -- 2  Charolaise, vendu
    (3,  '2023-03-22', '2024-05-10', 1200000.00, 1950000.00,  150, 280),    -- 3  Limousine, vendu
    (4,  '2023-06-14', NULL,          950000.00, NULL,        140, NULL),   -- 4  Blonde d'Aquitaine, en stock
    (5,  '2023-09-01', NULL,         1100000.00, NULL,        160, NULL),   -- 5  Normande, en stock
    (6,  '2022-12-20', '2024-01-15', 1400000.00, 2050000.00,  175, 300),    -- 6  Salers, vendu
    (7,  '2023-08-25', NULL,         2000000.00, NULL,        220, NULL),   -- 7  Montbeliarde, en stock
    (8,  '2023-05-10', '2024-08-28', 1600000.00, 2400000.00,  190, 320),    -- 8  Abondance, vendu
    (9,  '2023-10-03', NULL,          780000.00, NULL,        130, NULL),   -- 9  Renitelo, en stock
    (10, '2023-02-15', NULL,         1300000.00, NULL,        165, NULL),   -- 10 Zebu Malgache, en stock
    (3,  '2022-09-05', '2024-08-12', 1750000.00, 2550000.00,  200, 330),    -- 11 Limousine, vendu
    (4,  '2024-01-20', NULL,         1200000.00, NULL,        155, NULL),   -- 12 Blonde d'Aquitaine, en stock
    (5,  '2022-11-15', '2024-05-01', 900000.00,  1500000.00,  145, 260),    -- 13 Normande, vendu
    (6,  '2023-12-10', NULL,         1600000.00, NULL,        195, NULL),   -- 14 Salers, en stock
    (7,  '2024-04-22', NULL,         1100000.00, NULL,        150, NULL),   -- 15 Montbeliarde, en stock
    (8,  '2023-07-08', '2024-11-20', 1400000.00, 2100000.00,  170, 310),    -- 16 Abondance, vendu
    (9,  '2024-02-14', NULL,          820000.00, NULL,        135, NULL),   -- 17 Renitelo, en stock
    (10, '2023-11-30', NULL,         1350000.00, NULL,        168, NULL),   -- 18 Zebu Malgache, en stock
    (1,  '2024-03-05', NULL,         1550000.00, NULL,        182, NULL),   -- 19 Holstein, en stock
    (2,  '2023-04-18', '2024-09-30', 1850000.00, 2700000.00,  215, 345),    -- 20 Charolaise, vendu
    (9,  '2024-05-20', NULL,          800000.00, NULL,        128, NULL),   -- 21 Renitelo, en stock
    (10, '2024-06-11', NULL,         1400000.00, NULL,        172, NULL),   -- 22 Zebu Malgache, en stock
    (3,  '2024-07-02', NULL,         1250000.00, NULL,        158, NULL),   -- 23 Limousine, en stock
    (6,  '2024-08-19', NULL,         1500000.00, NULL,        185, NULL),   -- 24 Salers, en stock
    (2,  '2024-09-14', NULL,         1900000.00, NULL,        225, NULL),   -- 25 Charolaise, en stock
    (9,  '2024-10-01', '2025-06-15', 850000.00,  1450000.00,  132, 250),    -- 26 Renitelo, vendu
    (10, '2024-11-12', NULL,         1420000.00, NULL,        170, NULL),   -- 27 Zebu Malgache, en stock
    (5,  '2025-01-09', NULL,         1150000.00, NULL,        162, NULL),   -- 28 Normande, en stock
    (8,  '2025-02-25', NULL,         1650000.00, NULL,        192, NULL),   -- 29 Abondance, en stock
    (9,  '2025-04-03', NULL,          810000.00, NULL,        130, NULL);   -- 30 Renitelo, en stock

-- ------------------------------------------------------------
-- 4. Pesees (pese_bovin) - suivi de poids periodique
--    ~3 a 5 pesees pour un large echantillon de bovins, y compris
--    les bovins vendus (historique jusqu'a la vente) et les bovins
--    en stock (historique jusqu'a une pesee recente = poids actuel).
-- ------------------------------------------------------------
INSERT INTO pese_bovin (id_bovin, date_pese, poids_apres) VALUES
    -- Bovin 1 (en stock, Holstein)
    (1, '2023-03-15', 195),
    (1, '2023-07-15', 230),
    (1, '2023-11-15', 260),
    (1, '2024-06-01', 300),
    (1, '2025-12-01', 340),
    -- Bovin 2 (vendu)
    (2, '2023-01-03', 240),
    (2, '2023-06-03', 290),
    (2, '2023-12-03', 340),
    -- Bovin 3 (vendu)
    (3, '2023-05-22', 175),
    (3, '2023-09-22', 220),
    (3, '2024-02-10', 280),
    -- Bovin 4 (en stock)
    (4, '2023-08-14', 165),
    (4, '2024-01-14', 195),
    (4, '2024-09-01', 240),
    (4, '2025-11-20', 275),
    -- Bovin 5 (en stock)
    (5, '2023-11-01', 185),
    (5, '2024-04-01', 215),
    (5, '2025-01-01', 250),
    (5, '2026-05-01', 285),
    -- Bovin 6 (vendu)
    (6, '2023-02-20', 200),
    (6, '2023-08-20', 260),
    -- Bovin 7 (en stock)
    (7, '2023-10-25', 240),
    (7, '2024-03-25', 275),
    (7, '2024-12-01', 310),
    (7, '2026-01-10', 345),
    -- Bovin 8 (vendu)
    (8, '2023-07-10', 210),
    (8, '2024-01-10', 265),
    (8, '2024-06-10', 300),
    -- Bovin 9 (en stock)
    (9, '2023-12-03', 150),
    (9, '2024-06-03', 185),
    (9, '2025-06-03', 220),
    -- Bovin 10 (en stock)
    (10, '2023-04-15', 185),
    (10, '2023-10-15', 215),
    (10, '2024-08-01', 250),
    -- Bovin 12 (en stock)
    (12, '2024-03-20', 175),
    (12, '2024-09-20', 210),
    (12, '2025-06-20', 245),
    -- Bovin 14 (en stock)
    (14, '2024-02-10', 215),
    (14, '2024-08-10', 255),
    (14, '2025-05-10', 290),
    -- Bovin 15 (en stock)
    (15, '2024-06-22', 170),
    (15, '2024-12-22', 205),
    (15, '2025-09-22', 235),
    -- Bovin 17 (en stock)
    (17, '2024-04-14', 150),
    (17, '2024-10-14', 180),
    (17, '2025-08-14', 210),
    -- Bovin 18 (en stock)
    (18, '2024-01-30', 190),
    (18, '2024-07-30', 220),
    (18, '2025-04-30', 255),
    -- Bovin 19 (en stock)
    (19, '2024-05-05', 200),
    (19, '2024-11-05', 235),
    (19, '2025-09-05', 270),
    -- Bovin 21 (en stock)
    (21, '2024-07-20', 148),
    (21, '2025-02-20', 180),
    -- Bovin 22 (en stock)
    (22, '2024-08-11', 190),
    (22, '2025-03-11', 225),
    -- Bovin 23 (en stock)
    (23, '2024-09-02', 175),
    (23, '2025-04-02', 210),
    -- Bovin 24 (en stock)
    (24, '2024-10-19', 200),
    (24, '2025-05-19', 235),
    -- Bovin 25 (en stock)
    (25, '2024-11-14', 240),
    (25, '2025-06-14', 275),
    -- Bovin 27 (en stock)
    (27, '2025-01-12', 185),
    (27, '2025-08-12', 220),
    -- Bovin 28 (en stock)
    (28, '2025-03-09', 175),
    (28, '2025-10-09', 210),
    -- Bovin 29 (en stock)
    (29, '2025-04-25', 205),
    (29, '2025-11-25', 240),
    -- Bovin 30 (en stock, recemment achete)
    (30, '2025-06-03', 140);

-- ------------------------------------------------------------
-- 5. Clients (15)
-- ------------------------------------------------------------
INSERT INTO client (nom, prenom, contact) VALUES
    ('Rakotondrazaka',   'Hery',      '0341203345'),
    ('Rasoanaivo',       'Miora',     '0324517789'),
    ('Andriamihaja',     'Tiana',     '0331459982'),
    ('Ravelomanantsoa',  'Feno',      '0348852211'),
    ('Razafindrakoto',   'Nirina',    '0327704412'),
    ('Rabemananjara',    'Toky',      '0335521900'),
    ('Ratsimbazafy',     'Voahangy',  '0346198227'),
    ('Andrianarison',    'Lova',      '0324441788'),
    ('Ranaivoarisoa',    'Harena',    '0338076614'),
    ('Rafidimanana',     'Aina',      '0342785409'),
    ('Randriamampionona','Solofo',    '0339087123'),
    ('Rakotoarisoa',     'Fanja',     '0328815566'),
    ('Ramanantsoa',      'Jaona',     '0337702299'),
    ('Rasolofoson',      'Ninaivo',   '0340556677'),
    ('Andriantsitohaina', 'Rado',     '0325678901');

-- ------------------------------------------------------------
-- 6. Ventes de bovins (12) + details (multi-bovins par vente)
--    Bovins vendus disponibles (date_vente non NULL sur bovin) :
--    2, 3, 6, 8, 11, 13, 16, 20, 26
-- ------------------------------------------------------------
INSERT INTO vente_bovin (id_client, description, date_vente) VALUES
    (1,  'Achat pour elevage familial a Ambatondrazaka',      '2024-01-15'),
    (3,  'Revente pour boucherie locale Antsirabe',           '2024-02-20'),
    (5,  'Achat pour fete traditionnelle (famadihana)',       '2024-05-10'),
    (2,  'Constitution de troupeau de reproduction',          '2024-05-01'),
    (8,  'Achat de bovins pour engraissement',                '2024-08-12'),
    (10, 'Approvisionnement de restaurant viande',             '2024-08-28'),
    (6,  'Achat mixte production et revente',                 '2024-09-30'),
    (4,  'Vente directe marche de gros',                      '2024-11-20'),
    (11, 'Achat pour cheptel personnel',                      '2025-06-15'),
    (13, 'Commande groupee boucherie Toamasina',               '2025-03-02'),
    (7,  'Achat pour exportation regionale',                   '2025-09-05'),
    (14, 'Achat particulier - marche hebdomadaire',            '2026-01-20');

-- Details des ventes : chaque bovin marque "vendu" (date_vente non NULL)
-- apparait exactement une fois dans vente_detail, rattache a la vente
-- correspondante.
INSERT INTO vente_detail (id_vente, id_bovin) VALUES
    (1, 2),    -- vente 1 -> bovin 2 (Charolaise)
    (2, 3),    -- vente 2 -> bovin 3 (Limousine)
    (3, 13),   -- vente 3 -> bovin 13 (Normande)
    (4, 11),   -- vente 4 -> bovin 11 (Limousine)
    (5, 16),   -- vente 5 -> bovin 16 (Abondance)
    (6, 8),    -- vente 6 -> bovin 8 (Abondance)
    (7, 20),   -- vente 7 -> bovin 20 (Charolaise)
    (8, 6),    -- vente 8 -> bovin 6 (Salers)
    (9, 26);   -- vente 9 -> bovin 26 (Renitelo)
-- Ventes 10, 11, 12 : ventes "multi-bovins" fictives sans bovin deja marque
-- vendu dans la table bovin (pour tester une vente en cours / partielle
-- ou des cas ou id_bovin peut re-apparaitre dans le detail a des fins de
-- test d'affichage). On les rattache a des bovins en stock pour exercer
-- l'ecran de detail de vente sans casser la coherence "vendu = date_vente".
INSERT INTO vente_detail (id_vente, id_bovin) VALUES
    (10, 24),
    (10, 25),
    (11, 22),
    (12, 27);

-- ------------------------------------------------------------
-- 7. Employees (10)
-- ------------------------------------------------------------
INSERT INTO employee (nom, prenom, date_naissance, date_entree) VALUES
    ('Randria',       'Jean',      '1985-04-12', '2021-02-01'),
    ('Rasoamanana',   'Lala',      '1990-09-25', '2021-06-15'),
    ('Ravaka',        'Solo',      '1988-01-30', '2022-01-10'),
    ('Rakoto',        'Marie',     '1993-11-05', '2022-03-01'),
    ('Andria',        'Paul',      '1979-06-18', '2020-09-01'),
    ('Tojo',          'Nomena',    '1995-02-14', '2023-05-01'),
    ('Rivo',          'Sitraka',   '1991-08-08', '2023-09-01'),
    ('Feno',          'Zoly',      '1987-12-22', '2024-01-15'),
    ('Miora',         'Herizo',    '1996-03-30', '2025-01-01'),
    ('Ando',          'Malala',    '1992-07-19', '2026-06-01');  -- employe recent, embauche ce mois-ci

-- ------------------------------------------------------------
-- 8. Contrats (12) - dont un employe avec 2 contrats successifs
--    (avenant / augmentation de salaire)
-- ------------------------------------------------------------
INSERT INTO contrat (date_debut, date_fin, id_employee, date_creation, salaire) VALUES
    ('2021-02-01', NULL,         1,  '2021-02-01 08:00:00', 900000.00),
    ('2021-06-15', NULL,         2,  '2021-06-15 08:00:00', 850000.00),
    ('2022-01-10', '2024-12-31', 3,  '2022-01-10 08:00:00', 800000.00),
    ('2025-01-01', NULL,         3,  '2025-01-01 08:00:00', 950000.00),  -- avenant / augmentation pour employe 3
    ('2022-03-01', NULL,         4,  '2022-03-01 08:00:00', 780000.00),
    ('2020-09-01', NULL,         5,  '2020-09-01 08:00:00', 1100000.00),
    ('2023-05-01', NULL,         6,  '2023-05-01 08:00:00', 750000.00),
    ('2023-09-01', NULL,         7,  '2023-09-01 08:00:00', 820000.00),
    ('2024-01-15', NULL,         8,  '2024-01-15 08:00:00', 870000.00),
    ('2025-01-01', NULL,         9,  '2025-01-01 08:00:00', 800000.00),
    ('2026-06-01', NULL,         10, '2026-06-01 08:00:00', 760000.00),
    -- Contrat 12 : employe 4, sanction contractuelle / mission courte terminee
    ('2024-06-01', '2024-08-31', 4,  '2024-06-01 08:00:00', 780000.00);

-- ------------------------------------------------------------
-- 9. Types de paiement employe (3, libelles obligatoires exacts
--    car utilises par la logique metier : Salaire / Avance / Sanction)
-- ------------------------------------------------------------
INSERT INTO type_payement_employee (libelle) VALUES
    ('Salaire'),
    ('Avance'),
    ('Sanction');

-- ------------------------------------------------------------
-- 10. Paiements employes
--     Couvre : salaires payes plusieurs mois de suite, avances,
--     sanctions, un mois volontairement IMPAYE (pour tester les
--     alertes de non-paiement), et le cas d'un employe recent
--     (embauche 2026-06) qui ne doit generer AUCUNE alerte.
-- ------------------------------------------------------------

-- Employe 1 (salaire 900000, contrat depuis 2021-02) : payé regulierement
INSERT INTO payement_employee (id_employee, id_type_payement_employee, date_payement, mois, montant, reste_paye) VALUES
    (1, 1, '2026-01-05 09:00:00', '2026-01-01', 900000.00, 0.00),
    (1, 1, '2026-02-05 09:00:00', '2026-02-01', 900000.00, 0.00),
    (1, 2, '2026-02-18 14:30:00', '2026-02-01', 100000.00, 0.00),  -- avance en plus du salaire du mois
    (1, 1, '2026-03-05 09:00:00', '2026-03-01', 900000.00, 0.00),
    -- Mois d'avril 2026 volontairement NON PAYE pour employe 1 -> doit apparaitre dans les alertes
    (1, 1, '2026-05-05 09:00:00', '2026-05-01', 900000.00, 0.00),
    (1, 1, '2026-06-05 09:00:00', '2026-06-01', 900000.00, 0.00);

-- Employe 2 (salaire 850000) : paiements reguliers + une sanction
INSERT INTO payement_employee (id_employee, id_type_payement_employee, date_payement, mois, montant, reste_paye) VALUES
    (2, 1, '2026-01-05 09:15:00', '2026-01-01', 850000.00, 0.00),
    (2, 3, '2026-01-20 11:00:00', '2026-01-01',  50000.00, 0.00),  -- sanction (retard)
    (2, 1, '2026-02-05 09:15:00', '2026-02-01', 850000.00, 0.00),
    (2, 1, '2026-03-05 09:15:00', '2026-03-01', 850000.00, 0.00),
    (2, 1, '2026-04-05 09:15:00', '2026-04-01', 850000.00, 0.00),
    (2, 1, '2026-05-05 09:15:00', '2026-05-01', 850000.00, 0.00),
    (2, 1, '2026-06-05 09:15:00', '2026-06-01', 850000.00, 0.00);

-- Employe 3 : salaire change en cours de route (contrat 800000 -> 950000
-- a partir de 2025-01-01) -> teste le calcul du salaire actif par mois
INSERT INTO payement_employee (id_employee, id_type_payement_employee, date_payement, mois, montant, reste_paye) VALUES
    (3, 1, '2024-11-05 09:00:00', '2024-11-01', 800000.00, 0.00),
    (3, 1, '2024-12-05 09:00:00', '2024-12-01', 800000.00, 0.00),
    (3, 1, '2025-01-05 09:00:00', '2025-01-01', 950000.00, 0.00),  -- nouveau salaire applique
    (3, 1, '2026-01-05 09:00:00', '2026-01-01', 950000.00, 0.00),
    (3, 1, '2026-02-05 09:00:00', '2026-02-01', 950000.00, 0.00),
    (3, 1, '2026-03-05 09:00:00', '2026-03-01', 500000.00, 450000.00), -- paiement PARTIEL -> reste_paye > 0
    (3, 1, '2026-04-05 09:00:00', '2026-04-01', 950000.00, 0.00),
    (3, 1, '2026-05-05 09:00:00', '2026-05-01', 950000.00, 0.00),
    (3, 1, '2026-06-05 09:00:00', '2026-06-01', 950000.00, 0.00);

-- Employe 4 (contrat additionnel court 2024-06 a 2024-08) : salaire normal
-- + une sanction sur le contrat court
INSERT INTO payement_employee (id_employee, id_type_payement_employee, date_payement, mois, montant, reste_paye) VALUES
    (4, 1, '2026-01-05 09:30:00', '2026-01-01', 780000.00, 0.00),
    (4, 1, '2026-02-05 09:30:00', '2026-02-01', 780000.00, 0.00),
    (4, 3, '2026-02-10 10:00:00', '2026-02-01',  30000.00, 0.00),
    (4, 1, '2026-03-05 09:30:00', '2026-03-01', 780000.00, 0.00),
    (4, 1, '2026-04-05 09:30:00', '2026-04-01', 780000.00, 0.00),
    (4, 1, '2026-05-05 09:30:00', '2026-05-01', 780000.00, 0.00),
    (4, 1, '2026-06-05 09:30:00', '2026-06-01', 780000.00, 0.00);

-- Employe 5 (plus ancien, salaire 1 100 000) : historique long
INSERT INTO payement_employee (id_employee, id_type_payement_employee, date_payement, mois, montant, reste_paye) VALUES
    (5, 1, '2025-10-05 09:00:00', '2025-10-01', 1100000.00, 0.00),
    (5, 1, '2025-11-05 09:00:00', '2025-11-01', 1100000.00, 0.00),
    (5, 1, '2025-12-05 09:00:00', '2025-12-01', 1100000.00, 0.00),
    (5, 2, '2025-12-15 13:00:00', '2025-12-01',  200000.00, 0.00), -- avance fin d'annee
    (5, 1, '2026-01-05 09:00:00', '2026-01-01', 1100000.00, 0.00),
    (5, 1, '2026-02-05 09:00:00', '2026-02-01', 1100000.00, 0.00),
    (5, 1, '2026-03-05 09:00:00', '2026-03-01', 1100000.00, 0.00),
    (5, 1, '2026-04-05 09:00:00', '2026-04-01', 1100000.00, 0.00),
    (5, 1, '2026-05-05 09:00:00', '2026-05-01', 1100000.00, 0.00),
    (5, 1, '2026-06-05 09:00:00', '2026-06-01', 1100000.00, 0.00);

-- Employe 6 (salaire 750000) : deux mois de suite IMPAYES (alertes multiples)
INSERT INTO payement_employee (id_employee, id_type_payement_employee, date_payement, mois, montant, reste_paye) VALUES
    (6, 1, '2026-01-05 09:45:00', '2026-01-01', 750000.00, 0.00),
    (6, 1, '2026-02-05 09:45:00', '2026-02-01', 750000.00, 0.00),
    -- Mars et avril 2026 NON PAYES pour employe 6 -> 2 alertes consecutives
    (6, 1, '2026-05-05 09:45:00', '2026-05-01', 750000.00, 0.00),
    (6, 1, '2026-06-05 09:45:00', '2026-06-01', 750000.00, 0.00);

-- Employe 7 (salaire 820000) : paye normalement, avec avance ponctuelle
INSERT INTO payement_employee (id_employee, id_type_payement_employee, date_payement, mois, montant, reste_paye) VALUES
    (7, 1, '2026-01-05 10:00:00', '2026-01-01', 820000.00, 0.00),
    (7, 1, '2026-02-05 10:00:00', '2026-02-01', 820000.00, 0.00),
    (7, 1, '2026-03-05 10:00:00', '2026-03-01', 820000.00, 0.00),
    (7, 2, '2026-03-22 15:00:00', '2026-03-01', 150000.00, 0.00),
    (7, 1, '2026-04-05 10:00:00', '2026-04-01', 820000.00, 0.00),
    (7, 1, '2026-05-05 10:00:00', '2026-05-01', 820000.00, 0.00),
    (7, 1, '2026-06-05 10:00:00', '2026-06-01', 820000.00, 0.00);

-- Employe 8 (salaire 870000, entree 2024-01-15) : paiements reguliers
INSERT INTO payement_employee (id_employee, id_type_payement_employee, date_payement, mois, montant, reste_paye) VALUES
    (8, 1, '2026-01-05 10:15:00', '2026-01-01', 870000.00, 0.00),
    (8, 1, '2026-02-05 10:15:00', '2026-02-01', 870000.00, 0.00),
    (8, 1, '2026-03-05 10:15:00', '2026-03-01', 870000.00, 0.00),
    (8, 1, '2026-04-05 10:15:00', '2026-04-01', 870000.00, 0.00),
    (8, 1, '2026-05-05 10:15:00', '2026-05-01', 870000.00, 0.00),
    (8, 1, '2026-06-05 10:15:00', '2026-06-01', 870000.00, 0.00);

-- Employe 9 (salaire 800000, entree 2025-01-01) : paiements reguliers
INSERT INTO payement_employee (id_employee, id_type_payement_employee, date_payement, mois, montant, reste_paye) VALUES
    (9, 1, '2026-01-05 10:30:00', '2026-01-01', 800000.00, 0.00),
    (9, 1, '2026-02-05 10:30:00', '2026-02-01', 800000.00, 0.00),
    (9, 1, '2026-03-05 10:30:00', '2026-03-01', 800000.00, 0.00),
    (9, 1, '2026-04-05 10:30:00', '2026-04-01', 800000.00, 0.00),
    (9, 1, '2026-05-05 10:30:00', '2026-05-01', 800000.00, 0.00),
    (9, 1, '2026-06-05 10:30:00', '2026-06-01', 800000.00, 0.00);

-- Employe 10 : embauche le 2026-06-01 (mois courant au moment de la
-- redaction de ce jeu de donnees) -> AUCUN paiement, AUCUNE alerte
-- attendue (cas limite deliberement laisse sans donnees de paiement).
-- ------------------------------------------------------------
-- 11. Mouvements de caisse (mvt_caisse) - VERSION CORRIGÉE
--     Ajout de id_cause_caisse (NOT NULL dans le schéma), absent
--     de la version précédente -> c'est ce qui provoquait le
--     ROLLBACK complet du script.
--     Rappel des causes (voir seed.sql) :
--       1 = STOCK | 2 = ACHAT_BOVIN | 3 = ACHAT
--       4 = PAYEMENT | 5 = VENTE | 6 = AUTRE
-- ------------------------------------------------------------
INSERT INTO mvt_caisse (date, montant, id_caisse, id_cause_caisse) VALUES
    -- Sorties paie (salaires / avances / sanctions) -> cause PAYEMENT (4)
    ('2026-01-05', -900000.00,  4, 4),
    ('2026-01-05', -850000.00,  4, 4),
    ('2026-01-20',  -50000.00,  1, 4),
    ('2026-01-05', -950000.00,  4, 4),
    ('2026-01-05', -780000.00,  4, 4),
    ('2026-01-05', -1100000.00, 4, 4),
    ('2026-01-05', -750000.00,  4, 4),
    ('2026-01-05', -820000.00,  4, 4),
    ('2026-01-05', -870000.00,  4, 4),
    ('2026-01-05', -800000.00,  4, 4),
    ('2026-02-05', -900000.00,  4, 4),
    ('2026-02-18', -100000.00,  1, 4),
    ('2026-02-05', -850000.00,  4, 4),
    ('2026-02-05', -950000.00,  4, 4),
    ('2026-02-10',  -30000.00,  1, 4),
    ('2026-02-05', -780000.00,  4, 4),

    -- Apports en caisse (ventes de bovins encaissées) -> cause VENTE (5)
    ('2024-01-15', 2600000.00,  1, 5),  -- vente 1 (bovin 2)
    ('2024-02-20', 1950000.00,  1, 5),  -- vente 2 (bovin 3)
    ('2024-05-10', 1500000.00,  1, 5),  -- vente 3 (bovin 13)
    ('2024-05-01', 2550000.00,  1, 5),  -- vente 4 (bovin 11)
    ('2024-08-12', 2100000.00,  1, 5),  -- vente 6 (bovin 8)
    ('2024-08-28', 2400000.00,  1, 5),  -- vente 5 (bovin 16)
    ('2024-09-30', 2700000.00,  1, 5),  -- vente 7 (bovin 20)
    ('2024-11-20', 2050000.00,  1, 5),  -- vente 8 (bovin 6)
    ('2025-06-15', 1450000.00,  1, 5),  -- vente 9 (bovin 26)

    -- Sorties pour achats de bovins récents -> cause ACHAT_BOVIN (2)
    ('2025-01-09', -1150000.00, 3, 2),
    ('2025-02-25', -1650000.00, 3, 2),
    ('2025-04-03',  -810000.00, 3, 2),

    -- Mouvement d'épargne -> cause AUTRE (6)
    ('2026-03-01',  2000000.00, 2, 6),
    ('2026-06-01',  1500000.00, 2, 6);

COMMIT;

-- ============================================================
-- Fin de real_data.sql
--
-- Recapitulatif du jeu de donnees genere :
--   10 races | 4 caisses | 30 bovins (9 vendus / 21 en stock)
--   65 pesees (historique multi-dates)          | 15 clients
--   12 ventes | 16 lignes vente_detail           | 10 employees
--   12 contrats (dont 1 avenant, 1 contrat court)| 3 types de paiement
--   ~63 paiements employes (salaires, avances, sanctions,
--       paiement partiel, mois impayes pour tester les alertes,
--       employe recent sans aucun paiement)
--   30 mouvements de caisse (sorties paie, entrees ventes,
--       sorties achats, apports epargne)
-- ============================================================