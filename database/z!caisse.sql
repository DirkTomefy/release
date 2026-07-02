DROP TABLE IF EXISTS mvt_caisse;
CREATE TABLE mvt_caisse(
    id SERIAL PRIMARY KEY,
    id_caisse INTEGER NOT NULL,
    type_mouvement VARCHAR(20) NOT NULL CHECK (type_mouvement IN ('ENTREE', 'SORTIE')),
    montant  DOUBLE PRECISION NOT NULL,
    date DATE NOT NULL,

    CONSTRAINT fk_mvt_caisse_caisse
        FOREIGN KEY (id_caisse)
        REFERENCES caisse(id)
);
