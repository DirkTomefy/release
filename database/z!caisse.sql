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

INSERT INTO mvt_caisse (id_caisse, type_mouvement, montant, date) VALUES 
    (1, 'ENTREE', 5000.00, '2024-01-15'),
    (2, 'SORTIE', 2000.00, '2024-02-10'),
    (3, 'ENTREE', 10000.00, '2024-03-05'),
    (1, 'SORTIE', 3000.00, '2024-04-20');

SELECT id_caisse,
         SUM(CASE WHEN type_mouvement = 'ENTREE' THEN montant ELSE 0 END)  - 
         SUM(CASE WHEN type_mouvement = 'SORTIE' THEN montant ELSE 0 END)  as montant_actuelle
FROM mvt_caisse
GROUP BY id_caisse