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

