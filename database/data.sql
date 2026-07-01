-- Active: 1781686611755@@127.0.0.1@3306.1@5432@bovin_db
CREATE DATABASE bovin_db;
\c bovin_db;

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

CREATE TABLE bovin(
    id SERIAL PRIMARY KEY,
    id_race INTEGER NOT NULL,
    date_achat DATE NOT NULL,
    date_vente DATE,
    prix_achat DOUBLE PRECISION NOT NULL,
    prix_vente DOUBLE PRECISION,
    poids_achat DOUBLE PRECISION NOT NULL,
    poids_vente DOUBLE PRECISION,

    CONSTRAINT fk_bovin_race
        FOREIGN KEY (id_race)
        REFERENCES race(id)
);

CREATE TABLE pese_bovin(
    id SERIAL PRIMARY KEY,
    id_bovin INTEGER NOT NULL,
    date_pese DATE NOT NULL,
    poids_apres DOUBLE PRECISION NOT NULL,

    CONSTRAINT fk_bovin_poids
        FOREIGN KEY (id_bovin)
        REFERENCES bovin(id)
);

-- module payment
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
    date_creation TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    salaire NUMERIC(12, 2) NOT NULL CHECK (salaire >= 0)
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
    
    CONSTRAINT fk_payement_employee FOREIGN KEY (id_employee) 
        REFERENCES employee(id) ON DELETE CASCADE,
    CONSTRAINT fk_payement_type FOREIGN KEY (id_type_payement_employee) 
        REFERENCES type_payement_employee(id) ON DELETE RESTRICT
);