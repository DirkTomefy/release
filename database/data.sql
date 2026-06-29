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