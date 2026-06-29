CREATE DATABASE bovin;
\c bovin;

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

    CONSTRAINT fk_bovin_race
        FOREIGN KEY (id_race)
        REFERENCES race(id)
)