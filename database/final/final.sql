-- Active: 1779021999916@@127.0.0.1@5432@bovin_db
-- ============================================================
-- Script combiné pour l'initialisation complète de la base bovin_db
-- ============================================================

-- Suppression des objets existants (pour réinitialisation propre)
DROP VIEW IF EXISTS v_pese_bovin_with_date_vente CASCADE;
DROP VIEW IF EXISTS v_bovin_poids_actuel CASCADE;

-- Suppression des tables dépendantes d'abord
DROP TABLE IF EXISTS user_role CASCADE;
DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS role CASCADE;
DROP TABLE IF EXISTS facture_detail CASCADE;
DROP TABLE IF EXISTS facture CASCADE;
DROP TABLE IF EXISTS inventaire_detail CASCADE;
DROP TABLE IF EXISTS inventaire CASCADE;
DROP TABLE IF EXISTS inventaire_bovin_detail CASCADE;
DROP TABLE IF EXISTS inventaire_bovin CASCADE;
DROP TABLE IF EXISTS mvt_stock_paiement CASCADE;
DROP TABLE IF EXISTS mouvement_stock CASCADE;
DROP TABLE IF EXISTS materiel CASCADE;
DROP TABLE IF EXISTS type_materiel CASCADE;
DROP TABLE IF EXISTS vente_detail CASCADE;
DROP TABLE IF EXISTS vente_bovin CASCADE;
DROP TABLE IF EXISTS client CASCADE;
DROP TABLE IF EXISTS mvt_caisse CASCADE;
DROP TABLE IF EXISTS cause_caisse CASCADE;
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
    id_caisse_parent INTEGER,
    montant_actuelle DOUBLE PRECISION NOT NULL,
    CONSTRAINT fk_caisse_parent FOREIGN KEY (id_caisse_parent) REFERENCES caisse(id),
    CONSTRAINT ck_montant_non_negatif CHECK (montant_actuelle >= 0)
);

CREATE TABLE cause_caisse(
    id SERIAL PRIMARY KEY,
    libelle VARCHAR(100) NOT NULL
);

CREATE TABLE race (
    id SERIAL PRIMARY KEY,
    nom VARCHAR(100) UNIQUE NOT NULL,
    descriptions TEXT
);

CREATE TABLE bovin (
    id SERIAL PRIMARY KEY,
    id_race INTEGER NOT NULL,
    date_achat DATE NOT NULL,
    date_vente DATE,
    prix_achat DOUBLE PRECISION NOT NULL CHECK (prix_achat >= 0),
    prix_vente DOUBLE PRECISION CHECK (prix_vente IS NULL OR prix_vente >= 0),
    poids_achat DOUBLE PRECISION NOT NULL CHECK (poids_achat >= 0),
    poids_vente DOUBLE PRECISION CHECK (poids_vente IS NULL OR poids_vente >= 0),
    CONSTRAINT fk_bovin_race FOREIGN KEY (id_race) REFERENCES race(id)
);

CREATE TABLE pese_bovin (
    id SERIAL PRIMARY KEY,
    id_bovin INTEGER NOT NULL,
    date_pese DATE NOT NULL,
    poids_apres DOUBLE PRECISION NOT NULL CHECK (poids_apres >= 0),
    CONSTRAINT fk_bovin_poids FOREIGN KEY (id_bovin) REFERENCES bovin(id)
);

CREATE TABLE mortalite (
    id SERIAL PRIMARY KEY,
    id_race INTEGER NOT NULL,
    prix_achat DOUBLE PRECISION NOT NULL CHECK (prix_achat >= 0),
    poids_mort DOUBLE PRECISION NOT NULL CHECK (poids_mort >= 0),
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
    id_cause_caisse INTEGER NOT NULL,
    CONSTRAINT fk_mvt_caisse_caisse FOREIGN KEY (id_caisse) REFERENCES caisse(id) ON DELETE RESTRICT,
    CONSTRAINT fk_mvt_caisse_cause FOREIGN KEY (id_cause_caisse) REFERENCES cause_caisse(id) ON DELETE RESTRICT
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

-- Tables pour la gestion des stocks de Matériel
CREATE TABLE type_materiel (
    id SERIAL PRIMARY KEY,
    libelle VARCHAR(100) NOT NULL
);

CREATE TABLE materiel (
    id SERIAL PRIMARY KEY,
    libelle VARCHAR(100) NOT NULL,
    id_type_materiel INTEGER NOT NULL,
    type_gestion VARCHAR(20) NOT NULL CHECK (type_gestion IN ('FIFO', 'LIFO')),
    CONSTRAINT fk_materiel_type FOREIGN KEY (id_type_materiel) REFERENCES type_materiel(id)
);

CREATE TABLE mouvement_stock (
    id SERIAL PRIMARY KEY,
    id_materiel INTEGER NOT NULL,
    date_mouvement DATE NOT NULL DEFAULT CURRENT_DATE,
    type_mouvement VARCHAR(10) NOT NULL CHECK (type_mouvement IN ('ENTREE', 'SORTIE')),
    quantite DOUBLE PRECISION NOT NULL CHECK (quantite > 0),
    prix_unitaire DOUBLE PRECISION,
    qte_restant DOUBLE PRECISION,
    CONSTRAINT fk_mouvement_stock_materiel FOREIGN KEY (id_materiel) REFERENCES materiel(id) ON DELETE RESTRICT
);

CREATE TABLE mvt_stock_paiement (
    id SERIAL PRIMARY KEY,
    id_mouvement_stock INTEGER NOT NULL,
    id_caisse INTEGER NOT NULL,
    montant DOUBLE PRECISION NOT NULL,
    CONSTRAINT fk_mvt_stock_paiement_mouvement FOREIGN KEY (id_mouvement_stock) REFERENCES mouvement_stock(id) ON DELETE CASCADE,
    CONSTRAINT fk_mvt_stock_paiement_caisse FOREIGN KEY (id_caisse) REFERENCES caisse(id) ON DELETE RESTRICT
);

-- Inventaire du MATÉRIEL
CREATE TABLE inventaire (
    id SERIAL PRIMARY KEY,
    date_inventaire DATE NOT NULL DEFAULT CURRENT_DATE,
    libelle VARCHAR(100)
);

CREATE TABLE inventaire_detail (
    id SERIAL PRIMARY KEY,
    id_inventaire INTEGER NOT NULL,
    id_materiel INTEGER NOT NULL,
    quantite_initiale DOUBLE PRECISION NOT NULL,
    quantite_finale DOUBLE PRECISION NOT NULL,
    observations TEXT,
    CONSTRAINT fk_inventaire_detail_inventaire FOREIGN KEY (id_inventaire) REFERENCES inventaire(id) ON DELETE CASCADE,
    CONSTRAINT fk_inventaire_detail_materiel FOREIGN KEY (id_materiel) REFERENCES materiel(id) ON DELETE RESTRICT
);

-- Facturation (Unique, sans doublon)
CREATE TABLE facture (
    id SERIAL PRIMARY KEY,
    id_vente INT NOT NULL UNIQUE,
    numero_facture VARCHAR(50) NOT NULL,
    code_facture VARCHAR(50) NOT NULL UNIQUE,
    date_facture DATE NOT NULL DEFAULT CURRENT_DATE,
    montant_total DOUBLE PRECISION NOT NULL,
    CONSTRAINT fk_facture_vente FOREIGN KEY (id_vente) REFERENCES vente_bovin(id)
);

CREATE INDEX idx_facture_code ON facture(code_facture);

CREATE TABLE facture_detail (
    id SERIAL PRIMARY KEY,
    id_facture INT NOT NULL,
    id_vente_detail INT NOT NULL UNIQUE,
    prix_unitaire DOUBLE PRECISION NOT NULL,
    quantite INT NOT NULL DEFAULT 1,
    CONSTRAINT fk_facture_detail_facture FOREIGN KEY (id_facture) REFERENCES facture(id),
    CONSTRAINT fk_facture_detail_vente_detail FOREIGN KEY (id_vente_detail) REFERENCES vente_detail(id)
);

-- Inventaire des BOVINS (Renommé pour éviter le conflit avec l'inventaire matériel)
CREATE TABLE inventaire_bovin (
    id SERIAL PRIMARY KEY,
    date_inventaire DATE NOT NULL DEFAULT CURRENT_DATE,
    libelle VARCHAR(100)
);

CREATE TABLE inventaire_bovin_detail (
    id SERIAL PRIMARY KEY,
    id_inventaire_bovin INTEGER NOT NULL,
    id_bovin INTEGER NOT NULL,
    quantite INTEGER NOT NULL DEFAULT 1,
    observations TEXT,
    CONSTRAINT fk_inv_bovin_detail_parent FOREIGN KEY (id_inventaire_bovin) REFERENCES inventaire_bovin(id) ON DELETE CASCADE,
    CONSTRAINT fk_inv_bovin_detail_bovin FOREIGN KEY (id_bovin) REFERENCES bovin(id) ON DELETE RESTRICT
);

CREATE INDEX IF NOT EXISTS idx_mortalite_date ON mortalite(date);
CREATE INDEX IF NOT EXISTS idx_mortalite_id_race ON mortalite(id_race);

-- ============================================================
-- 2. Insertion des données de référence
-- ============================================================

INSERT INTO type_payement_employee (libelle) VALUES
    ('Salaire'),
    ('Avance'),
    ('Sanction');

INSERT INTO race (nom, descriptions) VALUES
    ('Holstein', 'Race laitière d''origine néerlandaise'),
    ('Charolaise', 'Race à viande originaire de France'),
    ('Limousine', 'Race à viande réputée pour sa qualité'),
    ('Blonde d''Aquitaine', 'Race à viande du sud-ouest de la France'),
    ('Normande', 'Race mixte laitière et viande'),
    ('Salers', 'Race à viande et laitière du Massif Central'),
    ('Montbéliarde', 'Race laitière de l''est de la France'),
    ('Abondance', 'Race laitière des Alpes');

INSERT INTO caisse (libelle, montant_actuelle) VALUES
    ('Caisse principale', 15000.00),
    ('Caisse d''épargne', 8000.00),
    ('Fonds d''investissement', 20000.00);

INSERT INTO cause_caisse (libelle) VALUES
    ('STOCK'),
    ('ACHAT_BOVIN'),
    ('ACHAT'),
    ('PAYEMENT'),
    ('VENTE'),
    ('AUTRE');

INSERT INTO mvt_caisse (date, montant, id_caisse, id_cause_caisse)
SELECT
    CURRENT_DATE,
    c.montant_actuelle,
    c.id,
    cc.id
FROM caisse c
JOIN cause_caisse cc ON cc.libelle = 'STOCK';

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

INSERT INTO vente_bovin (id_client, description, date_vente) VALUES
    (1, 'Achat pour elevage familial a Ambatondrazaka', '2025-01-18'),
    (3, 'Revente pour boucherie locale Antsirabe', '2025-02-09'),
    (5, 'Achat pour fete traditionnelle', '2025-03-21'),
    (2, 'Constitution de troupeau de reproduction', '2025-04-12'),
    (8, 'Achat de bovins pour engraissement', '2025-05-04'),
    (10, 'Approvisionnement de restaurant viande', '2025-05-29'),
    (6, 'Achat mixte production et revente', '2025-06-14'),
    (4, 'Vente directe marche de gros', '2025-06-30');

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

INSERT INTO type_materiel (libelle) VALUES 
    ('Aliment'),
    ('Ustensile'),
    ('Autre');

INSERT INTO materiel (libelle, id_type_materiel, type_gestion) VALUES 
    ('Aliment A', 1, 'FIFO'),
    ('Aliment B', 1, 'FIFO'),
    ('Ustensile A', 2, 'LIFO'),
    ('Ustensile B', 2, 'LIFO'),
    ('Autre A', 3, 'FIFO');

-- ============================================================
-- 3. Vues
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

CREATE VIEW v_pese_bovin_with_date_vente AS
SELECT
    pb.id,
    pb.id_bovin,
    pb.date_pese,
    pb.poids_apres,
    b.date_vente
FROM pese_bovin pb
JOIN bovin b ON b.id = pb.id_bovin;

-- =========================================================
-- 4) Authentification (Schema & Seed)
-- =========================================================
CREATE TABLE IF NOT EXISTS users (
    id SERIAL PRIMARY KEY,
    login VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    actif BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS role (
    id SERIAL PRIMARY KEY,
    libelle VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS user_role (
    id_user INT NOT NULL,
    id_role INT NOT NULL,
    PRIMARY KEY (id_user, id_role),
    FOREIGN KEY (id_user) REFERENCES users (id) ON DELETE CASCADE,
    FOREIGN KEY (id_role) REFERENCES role (id) ON DELETE CASCADE
);

-- Initialisation des rôles & utilisateurs
DELETE FROM user_role;
DELETE FROM users;
DELETE FROM role;

INSERT INTO role (libelle) VALUES
    ('ADMIN'),
    ('VENTE'),
    ('PESEE'),
    ('LOT'),
    ('STOCK'),
    ('CAISSE'),
    ('EMPLOYE');

INSERT INTO users (login, password) VALUES
    ('admin', '$2a$10$3jJrKEYZUynHZjSpcZlgeuepQgjkHB4PLhnhKj.CqBqM1a9nd9l3u'),
    ('vente', '$2b$12$eq/si50na5QH.duUGjhj0u83ePqkc5/poe809gqLUEtfu7Eju3YYC'),
    ('pesee', '$2b$12$e/g0EXA../i/WAy6R42Ix.E/PsyT2Fw9DZ3p7241HqeSl7B.9J5Eu'),
    ('lot', '$2b$12$mwNr1QvqTX8qgQ8zkY5VHeI06PO4nwB9plAs7PGksGRGzg6.qUO0u'),
    ('stock', '$2b$12$LmbzH7GJ5VhLvyNBejyMA.R7Ar8pYbrn5F1m2koeMIdS4UYrwmDbG'),
    ('caisse', '$2b$12$.oMi1aG.Zf9F2dy0G3iVeuXihavI2wCm8H14vht1BoKctT7CdKp9q'),
    ('employe', '$2b$12$2tLq/lKziIZY.HD1eImYo.Gwuw1JAf54Ey/Y6yDs3T4wagdJJ0ZgG');

-- Association des rôles
INSERT INTO user_role (id_user, id_role)
SELECT u.id, r.id FROM users u JOIN role r ON r.libelle = 'ADMIN' WHERE u.login = 'admin';

INSERT INTO user_role (id_user, id_role)
SELECT u.id, r.id FROM users u JOIN role r ON r.libelle = 'VENTE' WHERE u.login = 'vente';

INSERT INTO user_role (id_user, id_role)
SELECT u.id, r.id FROM users u JOIN role r ON r.libelle = 'PESEE' WHERE u.login = 'pesee';

INSERT INTO user_role (id_user, id_role)
SELECT u.id, r.id FROM users u JOIN role r ON r.libelle = 'LOT' WHERE u.login = 'lot';

INSERT INTO user_role (id_user, id_role)
SELECT u.id, r.id FROM users u JOIN role r ON r.libelle = 'STOCK' WHERE u.login = 'stock';

INSERT INTO user_role (id_user, id_role)
SELECT u.id, r.id FROM users u JOIN role r ON r.libelle = 'CAISSE' WHERE u.login = 'caisse';

INSERT INTO user_role (id_user, id_role)
SELECT u.id, r.id FROM users u JOIN role r ON r.libelle = 'EMPLOYE' WHERE u.login = 'employe';