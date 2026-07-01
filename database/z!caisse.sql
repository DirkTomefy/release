CREATE TABLE mvt_caisse(
    id SERIAL PRIMARY KEY,
    date DATE NOT NULL,
    montant  DOUBLE PRECISION NOT NULL,
    id_caisse INTEGER NOT NULL,

    CONSTRAINT fk_mvt_caisse_caisse
        FOREIGN KEY (id_caisse)
        REFERENCES caisse(id)
);
