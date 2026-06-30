-- Nouvelle table : type_materiel
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
    
    CONSTRAINT fk_materiel_type
        FOREIGN KEY (id_type_materiel)
        REFERENCES type_materiel(id)
);

-- Nouvelle table : mvt_stock
CREATE TABLE mvt_stock (
    id SERIAL PRIMARY KEY,
    id_materiel INTEGER NOT NULL,
    type_mouvement VARCHAR(10) NOT NULL CHECK (type_mouvement IN ('ENTREE', 'SORTIE')),
    prix_unitaire DOUBLE PRECISION NOT NULL,
    qte DOUBLE PRECISION NOT NULL,
    date_mouvement DATE DEFAULT CURRENT_DATE,
    
    CONSTRAINT fk_mvt_stock_materiel
        FOREIGN KEY (id_materiel)
        REFERENCES materiel(id)
);