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