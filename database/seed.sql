-- Insertion de races
INSERT INTO race (nom, descriptions) VALUES ('Holstein', 'Race laitière d''origine néerlandaise');
INSERT INTO race (nom, descriptions) VALUES ('Charolaise', 'Race à viande originaire de France');
INSERT INTO race (nom, descriptions) VALUES ('Limousine', 'Race à viande réputée pour sa qualité');
INSERT INTO race (nom, descriptions) VALUES ('Blonde d''Aquitaine', 'Race à viande du sud-ouest de la France');
INSERT INTO race (nom, descriptions) VALUES ('Normande', 'Race mixte laitière et viande');
INSERT INTO race (nom, descriptions) VALUES ('Salers', 'Race à viande et laitière du Massif Central');
INSERT INTO race (nom, descriptions) VALUES ('Montbéliarde', 'Race laitière de l''est de la France');
INSERT INTO race (nom, descriptions) VALUES ('Abondance', 'Race laitière des Alpes');

-- Insertion de caisses
INSERT INTO caisse (libelle, montant_actuelle) VALUES ('Caisse principale', 15000.00);
INSERT INTO caisse (libelle, montant_actuelle) VALUES ('Caisse d''épargne', 8000.00);
INSERT INTO caisse (libelle, montant_actuelle) VALUES ('Fonds d''investissement', 20000.00);

-- Insertion de bovins (certains vendus, d'autres non)
-- Note: On suppose que les races ont des id 1 à 8 (selon l'ordre d'insertion ci-dessus)
-- Ajuster les id_race si besoin.

-- Bovin 1 : acheté le 2020-03-12, pas encore vendu, prix 1500
INSERT INTO bovin (id_race, date_achat, date_vente, prix_achat, prix_vente,poids_achat,poids_vente) 
VALUES (1, '2020-03-12', NULL, 1500.00, NULL,100,NULL);

-- Bovin 2 : acheté le 2019-07-05, vendu le 2021-06-15, prix achat 1800, prix vente 2200
INSERT INTO bovin (id_race, date_achat, date_vente, prix_achat, prix_vente) 
VALUES (2, '2019-07-05', '2021-06-15', 1800.00, 2200.00);

-- Bovin 3 : acheté le 2021-01-20, vendu le 2022-03-10, prix achat 1200, prix vente 1600
INSERT INTO bovin (id_race, date_achat, date_vente, prix_achat, prix_vente) 
VALUES (3, '2021-01-20', '2022-03-10', 1200.00, 1600.00);

-- Bovin 4 : acheté le 2022-06-14, pas encore vendu, prix achat 950
INSERT INTO bovin (id_race, date_achat, date_vente, prix_achat, prix_vente) 
VALUES (4, '2022-06-14', NULL, 950.00, NULL);

-- Bovin 5 : acheté le 2023-09-01, pas encore vendu, prix achat 1100
INSERT INTO bovin (id_race, date_achat, date_vente, prix_achat, prix_vente) 
VALUES (5, '2023-09-01', NULL, 1100.00, NULL);

-- Bovin 6 : acheté le 2020-11-20, vendu le 2021-12-01, prix achat 1400, prix vente 1850
INSERT INTO bovin (id_race, date_achat, date_vente, prix_achat, prix_vente) 
VALUES (6, '2020-11-20', '2021-12-01', 1400.00, 1850.00);

-- Bovin 7 : acheté le 2018-08-25, pas encore vendu, prix achat 2000
INSERT INTO bovin (id_race, date_achat, date_vente, prix_achat, prix_vente) 
VALUES (7, '2018-08-25', NULL, 2000.00, NULL);

-- Bovin 8 : acheté le 2021-05-10, vendu le 2023-02-28, prix achat 1600, prix vente 2100
INSERT INTO bovin (id_race, date_achat, date_vente, prix_achat, prix_vente) 
VALUES (8, '2021-05-10', '2023-02-28', 1600.00, 2100.00);

-- Bovin 9 : acheté le 2022-10-03, pas encore vendu, prix achat 780
INSERT INTO bovin (id_race, date_achat, date_vente, prix_achat, prix_vente) 
VALUES (1, '2022-10-03', NULL, 780.00, NULL);

-- Bovin 10 : acheté le 2023-02-15, pas encore vendu, prix achat 1300
INSERT INTO bovin (id_race, date_achat, date_vente, prix_achat, prix_vente) 
VALUES (2, '2023-02-15', NULL, 1300.00, NULL);

-- Ajout de quelques bovins supplémentaires pour avoir plus de données
INSERT INTO bovin (id_race, date_achat, date_vente, prix_achat, prix_vente) 
VALUES (3, '2020-09-05', '2022-08-12', 1750.00, 2300.00);

INSERT INTO bovin (id_race, date_achat, date_vente, prix_achat, prix_vente) 
VALUES (4, '2023-01-20', NULL, 1200.00, NULL);

INSERT INTO bovin (id_race, date_achat, date_vente, prix_achat, prix_vente) 
VALUES (5, '2021-11-15', '2023-05-01', 900.00, 1300.00);

INSERT INTO bovin (id_race, date_achat, date_vente, prix_achat, prix_vente) 
VALUES (6, '2019-12-10', NULL, 1600.00, NULL);

INSERT INTO bovin (id_race, date_achat, date_vente, prix_achat, prix_vente) 
VALUES (7, '2022-04-22', NULL, 1100.00, NULL);

INSERT INTO bovin (id_race, date_achat, date_vente, prix_achat, prix_vente) 
VALUES (8, '2020-07-08', '2022-11-20', 1400.00, 1900.00);