-- Nouvelle table : type_materiel
DROP TABLE IF EXISTS type_materiel CASCADE;
DROP TABLE IF EXISTS materiel CASCADE;
DROP TABLE IF EXISTS mvt_stock_entree CASCADE;
DROP TABLE IF EXISTS mvt_stock_entree_payement CASCADE;
DROP TABLE IF EXISTS mvt_stock_sortie CASCADE;

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

-- SELECT SUM(qte_restant) AS total_qte_restant
-- FROM mvt_stock_entree
-- WHERE id_materiel = 2;

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
