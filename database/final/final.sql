-- ============================================================
-- Script combiné pour l'initialisation complète de la base bovin_db
-- Exécuter sur une base vide (ou avec DROP en tête).
-- Ordre : suppression, création des tables, insertion des données,
-- création de la vue.
-- ============================================================

-- Suppression des objets existants (optionnel, pour réinitialisation)
DROP VIEW IF EXISTS v_bovin_poids_actuel CASCADE;
DROP TABLE IF EXISTS vente_detail CASCADE;
DROP TABLE IF EXISTS vente_bovin CASCADE;
DROP TABLE IF EXISTS client CASCADE;
DROP TABLE IF EXISTS mvt_caisse CASCADE;
DROP TABLE IF EXISTS payement_employee CASCADE;
DROP TABLE IF EXISTS type_payement_employee CASCADE;
DROP TABLE IF EXISTS contrat CASCADE;
DROP TABLE IF EXISTS employee CASCADE;
DROP TABLE IF EXISTS pese_bovin CASCADE;
DROP TABLE IF EXISTS mortalite CASCADE;
DROP TABLE IF EXISTS bovin CASCADE;
DROP TABLE IF EXISTS race CASCADE;
DROP TABLE IF EXISTS caisse CASCADE;

DROP TABLE IF EXISTS type_materiel CASCADE;
DROP TABLE IF EXISTS materiel CASCADE;
DROP TABLE IF EXISTS mvt_stock_entree CASCADE;
DROP TABLE IF EXISTS mvt_stock_entree_paiement CASCADE;
DROP TABLE IF EXISTS mvt_stock_sortie CASCADE;
DROP TABLE IF EXISTS inventaire_detail CASCADE;
DROP TABLE IF EXISTS inventaire CASCADE;

-- ============================================================
-- 1. Création des tables (ordre des dépendances)
-- ============================================================

CREATE TABLE caisse (
    id SERIAL PRIMARY KEY,
    libelle VARCHAR(100) NOT NULL,
    montant_actuelle DOUBLE PRECISION NOT NULL
);

CREATE TABLE race (
    id SERIAL PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    descriptions TEXT
);

CREATE TABLE bovin (
    id SERIAL PRIMARY KEY,
    id_race INTEGER NOT NULL,
    date_achat DATE NOT NULL,
    date_vente DATE,
    prix_achat DOUBLE PRECISION NOT NULL,
    prix_vente DOUBLE PRECISION,
    poids_achat DOUBLE PRECISION NOT NULL,
    poids_vente DOUBLE PRECISION,
    CONSTRAINT fk_bovin_race FOREIGN KEY (id_race) REFERENCES race(id)
);

CREATE TABLE pese_bovin (
    id SERIAL PRIMARY KEY,
    id_bovin INTEGER NOT NULL,
    date_pese DATE NOT NULL,
    poids_apres DOUBLE PRECISION NOT NULL,
    CONSTRAINT fk_bovin_poids FOREIGN KEY (id_bovin) REFERENCES bovin(id)
);

CREATE TABLE mortalite (
    id SERIAL PRIMARY KEY,
    id_race INTEGER NOT NULL,
    prix_achat DOUBLE PRECISION NOT NULL,
    poids_mort DOUBLE PRECISION NOT NULL,
    date DATE NOT NULL,
    CONSTRAINT fk_mortalite_race FOREIGN KEY (id_race) REFERENCES race(id)
);

CREATE TABLE employee (
    id SERIAL PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    prenom VARCHAR(100) NOT NULL,
    date_naissance DATE NOT NULL,
    date_entree DATE NOT NULL DEFAULT CURRENT_DATE
);

CREATE TABLE contrat (
    id SERIAL PRIMARY KEY,
    date_debut DATE NOT NULL,
    date_fin DATE,
    id_employee INT NOT NULL,
    date_creation TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    salaire NUMERIC(12, 2) NOT NULL CHECK (salaire >= 0),
    CONSTRAINT fk_contrat_employee FOREIGN KEY (id_employee) REFERENCES employee(id)
);

CREATE TABLE type_payement_employee (
    id SERIAL PRIMARY KEY,
    libelle VARCHAR(50) NOT NULL
);

-- Table payement_employee modifiée pour inclure les champs mois et montant
-- (initialement ajoutés par la migration)
CREATE TABLE payement_employee (
    id SERIAL PRIMARY KEY,
    id_employee INT NOT NULL,
    id_type_payement_employee INT NOT NULL,
    date_payement TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    reste_paye NUMERIC(12, 2) NOT NULL DEFAULT 0.00 CHECK (reste_paye >= 0),
    mois DATE NOT NULL DEFAULT date_trunc('month', CURRENT_DATE)::date,
    montant NUMERIC(12, 2) NOT NULL DEFAULT 0,
    CONSTRAINT fk_payement_employee FOREIGN KEY (id_employee)
        REFERENCES employee(id) ON DELETE CASCADE,
    CONSTRAINT fk_payement_type FOREIGN KEY (id_type_payement_employee)
        REFERENCES type_payement_employee(id) ON DELETE RESTRICT
);

CREATE TABLE mvt_caisse (
    id SERIAL PRIMARY KEY,
    date DATE NOT NULL,
    montant DOUBLE PRECISION NOT NULL,
    id_caisse INTEGER NOT NULL,
    CONSTRAINT fk_mvt_caisse_caisse FOREIGN KEY (id_caisse) REFERENCES caisse(id)
);

CREATE TABLE client (
    id SERIAL PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    prenom VARCHAR(100) NOT NULL,
    contact VARCHAR(100) NOT NULL
);

CREATE TABLE vente_bovin (
    id SERIAL PRIMARY KEY,
    id_client INT NOT NULL,
    description VARCHAR(200),
    date_vente DATE NOT NULL DEFAULT CURRENT_DATE,
    CONSTRAINT fk_vente_bovin_client FOREIGN KEY (id_client) REFERENCES client(id)
);

CREATE TABLE vente_detail (
    id SERIAL PRIMARY KEY,
    id_vente INT NOT NULL,
    id_bovin INT NOT NULL,
    CONSTRAINT fk_vente_detail_vente FOREIGN KEY (id_vente) REFERENCES vente_bovin(id),
    CONSTRAINT fk_vente_detail_bovin FOREIGN KEY (id_bovin) REFERENCES bovin(id)
);

-- Nouvelle table : type_materiel
-- ! FROM STOCK

CREATE TABLE type_materiel (
    id SERIAL PRIMARY KEY,
    libelle VARCHAR(100) NOT NULL
);

-- Insertion des types de matériel
INSERT INTO type_materiel (libelle) VALUES 
    ('Aliment'),
    ('Ustensile'),
    ('Autre');

-- Nouvelle table : materiel
CREATE TABLE materiel (
    id SERIAL PRIMARY KEY,
    libelle VARCHAR(100) NOT NULL,
    id_type_materiel INTEGER NOT NULL,
    type_gestion VARCHAR(20) NOT NULL CHECK (type_gestion IN ('FIFO', 'LIFO')), -- c'est plus logique
    
    CONSTRAINT fk_materiel_type
        FOREIGN KEY (id_type_materiel)
        REFERENCES type_materiel(id)
);

INSERT INTO materiel (libelle, id_type_materiel, type_gestion) VALUES 
    ('Aliment A', 1, 'FIFO'),
    ('Aliment B', 1, 'FIFO'),
    ('Ustensile A', 2, 'LIFO'),
    ('Ustensile B', 2, 'LIFO'),
    ('Autre A', 3, 'FIFO');

-- Nouvelle table : mvt_stock_entree
CREATE TABLE mvt_stock_entree (
    id SERIAL PRIMARY KEY,
    id_materiel INTEGER NOT NULL,
    prix_unitaire DOUBLE PRECISION NOT NULL,
    qte DOUBLE PRECISION NOT NULL,
    qte_restant DOUBLE PRECISION NOT NULL,
    date_entree DATE DEFAULT CURRENT_DATE,
    
    CONSTRAINT fk_mvt_stock_materiel
        FOREIGN KEY (id_materiel)
        REFERENCES materiel(id)
);

CREATE TABLE mvt_stock_entree_paiement (
    id SERIAL PRIMARY KEY,
    id_mvt_stock_entree INTEGER NOT NULL,
    id_caisse INTEGER NOT NULL,
    montant DOUBLE PRECISION NOT NULL,

    CONSTRAINT fk_mvt_stock_entree_paiement -- payement ou paiement ? -> paiement
        FOREIGN KEY (id_mvt_stock_entree)
        REFERENCES mvt_stock_entree(id),
    
    CONSTRAINT fk_mvt_stock_caisse
        FOREIGN KEY (id_caisse)
        REFERENCES caisse(id)
);

CREATE TABLE mvt_stock_sortie (
    id SERIAL PRIMARY KEY,
    id_materiel INTEGER NOT NULL,
    qte DOUBLE PRECISION NOT NULL,
    date_sortie DATE DEFAULT CURRENT_DATE,

    CONSTRAINT fk_mvt_stock_sortie_materiel
        FOREIGN KEY (id_materiel)
        REFERENCES materiel(id)
);


-- ============================================================
-- 2. Insertion des données de référence
-- ============================================================

-- Types de paiement employé
INSERT INTO type_payement_employee (libelle) VALUES
    ('Salaire'),
    ('Avance'),
    ('Sanction');

-- Races
INSERT INTO race (nom, descriptions) VALUES
    ('Holstein', 'Race laitière d''origine néerlandaise'),
    ('Charolaise', 'Race à viande originaire de France'),
    ('Limousine', 'Race à viande réputée pour sa qualité'),
    ('Blonde d''Aquitaine', 'Race à viande du sud-ouest de la France'),
    ('Normande', 'Race mixte laitière et viande'),
    ('Salers', 'Race à viande et laitière du Massif Central'),
    ('Montbéliarde', 'Race laitière de l''est de la France'),
    ('Abondance', 'Race laitière des Alpes');

-- Caisses
INSERT INTO caisse (libelle, montant_actuelle) VALUES
    ('Caisse principale', 15000.00),
    ('Caisse d''épargne', 8000.00),
    ('Fonds d''investissement', 20000.00);

-- Bovins (seed.sql)
INSERT INTO bovin (id_race, date_achat, date_vente, prix_achat, prix_vente, poids_achat, poids_vente) VALUES
    (1, '2020-03-12', NULL, 1500.00, NULL, 100, NULL),
    (2, '2019-07-05', '2021-06-15', 1800.00, 2200.00, 120, NULL),
    (3, '2021-01-20', '2022-03-10', 1200.00, 1600.00, 95, NULL),
    (4, '2022-06-14', NULL, 950.00, NULL, 88, NULL),
    (5, '2023-09-01', NULL, 1100.00, NULL, 92, NULL),
    (6, '2020-11-20', '2021-12-01', 1400.00, 1850.00, 105, NULL),
    (7, '2018-08-25', NULL, 2000.00, NULL, 130, NULL),
    (8, '2021-05-10', '2023-02-28', 1600.00, 2100.00, 115, NULL),
    (1, '2022-10-03', NULL, 780.00, NULL, 82, NULL),
    (2, '2023-02-15', NULL, 1300.00, NULL, 98, NULL),
    (3, '2020-09-05', '2022-08-12', 1750.00, 2300.00, 125, NULL),
    (4, '2023-01-20', NULL, 1200.00, NULL, 96, NULL),
    (5, '2021-11-15', '2023-05-01', 900.00, 1300.00, 90, NULL),
    (6, '2019-12-10', NULL, 1600.00, NULL, 110, NULL),
    (7, '2022-04-22', NULL, 1100.00, NULL, 94, NULL),
    (8, '2020-07-08', '2022-11-20', 1400.00, 1900.00, 108, NULL);

-- Clients (initDbVenteData.sql)
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

-- Ventes bovins
INSERT INTO vente_bovin (id_client, description, date_vente) VALUES
    (1, 'Achat pour elevage familial a Ambatondrazaka', '2025-01-18'),
    (3, 'Revente pour boucherie locale Antsirabe', '2025-02-09'),
    (5, 'Achat pour fete traditionnelle', '2025-03-21'),
    (2, 'Constitution de troupeau de reproduction', '2025-04-12'),
    (8, 'Achat de bovins pour engraissement', '2025-05-04'),
    (10, 'Approvisionnement de restaurant viande', '2025-05-29'),
    (6, 'Achat mixte production et revente', '2025-06-14'),
    (4, 'Vente directe marche de gros', '2025-06-30');

-- Détails des ventes
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

-- ============================================================
-- 3. Vue : poids actuel des bovins (z!view.sql)
-- ============================================================

CREATE VIEW v_bovin_poids_actuel AS
SELECT
    b.id,
    b.id_race,
    b.date_achat,
    b.date_vente,
    b.prix_achat,
    b.prix_vente,
    b.poids_achat,
    b.poids_vente,
    r.nom AS race_nom,
    r.descriptions AS race_description,
    (
        SELECT pb.poids_apres
        FROM pese_bovin pb
        WHERE pb.id_bovin = b.id
        ORDER BY pb.date_pese DESC
        LIMIT 1
    ) AS poids_actuel,
    (
        SELECT pb.date_pese
        FROM pese_bovin pb
        WHERE pb.id_bovin = b.id
        ORDER BY pb.date_pese DESC
        LIMIT 1
    ) AS date_dernier_pese
FROM bovin b
JOIN race r ON b.id_race = r.id;

-- ============================================================
-- Fin du script
-- ============================================================-- ============================================================
-- Script combiné pour l'initialisation complète de la base bovin_db
-- Exécuter sur une base vide (ou avec DROP en tête).
-- Ordre : suppression, création des tables, insertion des données,
-- création de la vue.
-- ============================================================

-- Suppression des objets existants (optionnel, pour réinitialisation)
DROP VIEW IF EXISTS v_bovin_poids_actuel CASCADE;
DROP TABLE IF EXISTS vente_detail CASCADE;
DROP TABLE IF EXISTS vente_bovin CASCADE;
DROP TABLE IF EXISTS client CASCADE;
DROP TABLE IF EXISTS mvt_caisse CASCADE;
DROP TABLE IF EXISTS payement_employee CASCADE;
DROP TABLE IF EXISTS type_payement_employee CASCADE;
DROP TABLE IF EXISTS contrat CASCADE;
DROP TABLE IF EXISTS employee CASCADE;
DROP TABLE IF EXISTS pese_bovin CASCADE;
DROP TABLE IF EXISTS mortalite CASCADE;
DROP TABLE IF EXISTS bovin CASCADE;
DROP TABLE IF EXISTS race CASCADE;
DROP TABLE IF EXISTS caisse CASCADE;

-- ============================================================
-- 1. Création des tables (ordre des dépendances)
-- ============================================================

CREATE TABLE caisse (
    id SERIAL PRIMARY KEY,
    libelle VARCHAR(100) NOT NULL,
    montant_actuelle DOUBLE PRECISION NOT NULL
);

CREATE TABLE race (
    id SERIAL PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    descriptions TEXT
);

CREATE TABLE bovin (
    id SERIAL PRIMARY KEY,
    id_race INTEGER NOT NULL,
    date_achat DATE NOT NULL,
    date_vente DATE,
    prix_achat DOUBLE PRECISION NOT NULL,
    prix_vente DOUBLE PRECISION,
    poids_achat DOUBLE PRECISION NOT NULL,
    poids_vente DOUBLE PRECISION,
    CONSTRAINT fk_bovin_race FOREIGN KEY (id_race) REFERENCES race(id)
);

CREATE TABLE pese_bovin (
    id SERIAL PRIMARY KEY,
    id_bovin INTEGER NOT NULL,
    date_pese DATE NOT NULL,
    poids_apres DOUBLE PRECISION NOT NULL,
    CONSTRAINT fk_bovin_poids FOREIGN KEY (id_bovin) REFERENCES bovin(id)
);

CREATE TABLE mortalite (
    id SERIAL PRIMARY KEY,
    id_race INTEGER NOT NULL,
    prix_achat DOUBLE PRECISION NOT NULL,
    poids_mort DOUBLE PRECISION NOT NULL,
    date DATE NOT NULL,
    CONSTRAINT fk_mortalite_race FOREIGN KEY (id_race) REFERENCES race(id)
);

CREATE TABLE employee (
    id SERIAL PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    prenom VARCHAR(100) NOT NULL,
    date_naissance DATE NOT NULL,
    date_entree DATE NOT NULL DEFAULT CURRENT_DATE
);

CREATE TABLE contrat (
    id SERIAL PRIMARY KEY,
    date_debut DATE NOT NULL,
    date_fin DATE,
    id_employee INT NOT NULL,
    date_creation TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    salaire NUMERIC(12, 2) NOT NULL CHECK (salaire >= 0),
    CONSTRAINT fk_contrat_employee FOREIGN KEY (id_employee) REFERENCES employee(id)
);

CREATE TABLE type_payement_employee (
    id SERIAL PRIMARY KEY,
    libelle VARCHAR(50) NOT NULL
);

-- Table payement_employee modifiée pour inclure les champs mois et montant
-- (initialement ajoutés par la migration)
CREATE TABLE payement_employee (
    id SERIAL PRIMARY KEY,
    id_employee INT NOT NULL,
    id_type_payement_employee INT NOT NULL,
    date_payement TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    reste_paye NUMERIC(12, 2) NOT NULL DEFAULT 0.00 CHECK (reste_paye >= 0),
    mois DATE NOT NULL DEFAULT date_trunc('month', CURRENT_DATE)::date,
    montant NUMERIC(12, 2) NOT NULL DEFAULT 0,
    CONSTRAINT fk_payement_employee FOREIGN KEY (id_employee)
        REFERENCES employee(id) ON DELETE CASCADE,
    CONSTRAINT fk_payement_type FOREIGN KEY (id_type_payement_employee)
        REFERENCES type_payement_employee(id) ON DELETE RESTRICT
);

CREATE TABLE mvt_caisse (
    id SERIAL PRIMARY KEY,
    date DATE NOT NULL,
    montant DOUBLE PRECISION NOT NULL,
    id_caisse INTEGER NOT NULL,
    CONSTRAINT fk_mvt_caisse_caisse FOREIGN KEY (id_caisse) REFERENCES caisse(id)
);

CREATE TABLE client (
    id SERIAL PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    prenom VARCHAR(100) NOT NULL,
    contact VARCHAR(100) NOT NULL
);

CREATE TABLE vente_bovin (
    id SERIAL PRIMARY KEY,
    id_client INT NOT NULL,
    description VARCHAR(200),
    date_vente DATE NOT NULL DEFAULT CURRENT_DATE,
    CONSTRAINT fk_vente_bovin_client FOREIGN KEY (id_client) REFERENCES client(id)
);

CREATE TABLE vente_detail (
    id SERIAL PRIMARY KEY,
    id_vente INT NOT NULL,
    id_bovin INT NOT NULL,
    CONSTRAINT fk_vente_detail_vente FOREIGN KEY (id_vente) REFERENCES vente_bovin(id),
    CONSTRAINT fk_vente_detail_bovin FOREIGN KEY (id_bovin) REFERENCES bovin(id)
);

-- ============================================================
-- 2. Insertion des données de référence
-- ============================================================

-- Types de paiement employé
INSERT INTO type_payement_employee (libelle) VALUES
    ('Salaire'),
    ('Avance'),
    ('Sanction');

-- Races
INSERT INTO race (nom, descriptions) VALUES
    ('Holstein', 'Race laitière d''origine néerlandaise'),
    ('Charolaise', 'Race à viande originaire de France'),
    ('Limousine', 'Race à viande réputée pour sa qualité'),
    ('Blonde d''Aquitaine', 'Race à viande du sud-ouest de la France'),
    ('Normande', 'Race mixte laitière et viande'),
    ('Salers', 'Race à viande et laitière du Massif Central'),
    ('Montbéliarde', 'Race laitière de l''est de la France'),
    ('Abondance', 'Race laitière des Alpes');

-- Caisses
INSERT INTO caisse (libelle, montant_actuelle) VALUES
    ('Caisse principale', 15000.00),
    ('Caisse d''épargne', 8000.00),
    ('Fonds d''investissement', 20000.00);

-- Bovins (seed.sql)
INSERT INTO bovin (id_race, date_achat, date_vente, prix_achat, prix_vente, poids_achat, poids_vente) VALUES
    (1, '2020-03-12', NULL, 1500.00, NULL, 100, NULL),
    (2, '2019-07-05', '2021-06-15', 1800.00, 2200.00, 120, NULL),
    (3, '2021-01-20', '2022-03-10', 1200.00, 1600.00, 95, NULL),
    (4, '2022-06-14', NULL, 950.00, NULL, 88, NULL),
    (5, '2023-09-01', NULL, 1100.00, NULL, 92, NULL),
    (6, '2020-11-20', '2021-12-01', 1400.00, 1850.00, 105, NULL),
    (7, '2018-08-25', NULL, 2000.00, NULL, 130, NULL),
    (8, '2021-05-10', '2023-02-28', 1600.00, 2100.00, 115, NULL),
    (1, '2022-10-03', NULL, 780.00, NULL, 82, NULL),
    (2, '2023-02-15', NULL, 1300.00, NULL, 98, NULL),
    (3, '2020-09-05', '2022-08-12', 1750.00, 2300.00, 125, NULL),
    (4, '2023-01-20', NULL, 1200.00, NULL, 96, NULL),
    (5, '2021-11-15', '2023-05-01', 900.00, 1300.00, 90, NULL),
    (6, '2019-12-10', NULL, 1600.00, NULL, 110, NULL),
    (7, '2022-04-22', NULL, 1100.00, NULL, 94, NULL),
    (8, '2020-07-08', '2022-11-20', 1400.00, 1900.00, 108, NULL);

-- Clients (initDbVenteData.sql)
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

-- Ventes bovins
INSERT INTO vente_bovin (id_client, description, date_vente) VALUES
    (1, 'Achat pour elevage familial a Ambatondrazaka', '2025-01-18'),
    (3, 'Revente pour boucherie locale Antsirabe', '2025-02-09'),
    (5, 'Achat pour fete traditionnelle', '2025-03-21'),
    (2, 'Constitution de troupeau de reproduction', '2025-04-12'),
    (8, 'Achat de bovins pour engraissement', '2025-05-04'),
    (10, 'Approvisionnement de restaurant viande', '2025-05-29'),
    (6, 'Achat mixte production et revente', '2025-06-14'),
    (4, 'Vente directe marche de gros', '2025-06-30');

-- Détails des ventes
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

-- ============================================================
-- 3. Vue : poids actuel des bovins (z!view.sql)
-- ============================================================

CREATE VIEW v_bovin_poids_actuel AS
SELECT
    b.id,
    b.id_race,
    b.date_achat,
    b.date_vente,
    b.prix_achat,
    b.prix_vente,
    b.poids_achat,
    b.poids_vente,
    r.nom AS race_nom,
    r.descriptions AS race_description,
    (
        SELECT pb.poids_apres
        FROM pese_bovin pb
        WHERE pb.id_bovin = b.id
        ORDER BY pb.date_pese DESC
        LIMIT 1
    ) AS poids_actuel,
    (
        SELECT pb.date_pese
        FROM pese_bovin pb
        WHERE pb.id_bovin = b.id
        ORDER BY pb.date_pese DESC
        LIMIT 1
    ) AS date_dernier_pese
FROM bovin b
JOIN race r ON b.id_race = r.id;

-- ============================================================
-- Tables : INVENTAIRE et INVENTAIRE_DETAIL
-- ============================================================

CREATE TABLE inventaire (
    id SERIAL PRIMARY KEY,
    date_inventaire DATE NOT NULL DEFAULT CURRENT_DATE,
    libelle VARCHAR(100)
);

CREATE TABLE inventaire_detail (
    id SERIAL PRIMARY KEY,
    id_inventaire INTEGER NOT NULL,
    id_bovin INTEGER NOT NULL,
    quantite INTEGER NOT NULL DEFAULT 1,
    observations TEXT,
    CONSTRAINT fk_inventaire_detail_inventaire FOREIGN KEY (id_inventaire) REFERENCES inventaire(id) ON DELETE CASCADE,
    CONSTRAINT fk_inventaire_detail_bovin FOREIGN KEY (id_bovin) REFERENCES bovin(id) ON DELETE RESTRICT
);


-- Table facture modifiée
CREATE TABLE facture (
    id SERIAL PRIMARY KEY,
    id_vente INT NOT NULL UNIQUE,
    numero_facture VARCHAR(50) NOT NULL,   -- peut être conservé comme numéro séquentiel simple
    code_facture VARCHAR(50) NOT NULL UNIQUE, -- format : fact_MM_AAAA_XXX_IDVENTE
    date_facture DATE NOT NULL DEFAULT CURRENT_DATE,
    montant_total DOUBLE PRECISION NOT NULL,
    CONSTRAINT fk_facture_vente FOREIGN KEY (id_vente) REFERENCES vente_bovin(id)
);

CREATE INDEX idx_facture_code ON facture(code_facture);

-- Table facture_detail inchangée
CREATE TABLE facture_detail (
    id SERIAL PRIMARY KEY,
    id_facture INT NOT NULL,
    id_vente_detail INT NOT NULL UNIQUE,
    prix_unitaire DOUBLE PRECISION NOT NULL,
    quantite INT NOT NULL DEFAULT 1,
    CONSTRAINT fk_facture_detail_facture FOREIGN KEY (id_facture) REFERENCES facture(id),
    CONSTRAINT fk_facture_detail_vente_detail FOREIGN KEY (id_vente_detail) REFERENCES vente_detail(id)
);

--mortalité
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

-- ============================================================
-- Fin du script
-- ============================================================