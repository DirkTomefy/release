-- Donnees de test realistes pour les tables de vente
-- Ordre d'insertion: client -> vente_bovin -> vente_detail

INSERT INTO client (nom, prenom, contact) VALUES
('Rakotondrazaka', 'Hery', '0341203345'),
('Rasoanaivo', 'Miora', '0324517789'),
('Andriamihaja', 'Tiana', '0331459982'),
('Ravelomanantsoa', 'Feno', '0348852211'),
('Razafindrakoto', 'Nirina', '0327704412'),
('Rabemananjara', 'Toky', '0335521900'),
('Ratsimbazafy', 'Voahangy', '0346198227'),
('Andrianarison', 'Lova', '0324441788'),
('Ranaivoarisoa', 'Harena', '0338076614'),
('Rafidimanana', 'Aina', '0342785409');

INSERT INTO vente_bovin (id_client, description, date_vente) VALUES
(1, 'Achat pour elevage familial a Ambatondrazaka', '2025-01-18'),
(3, 'Revente pour boucherie locale Antsirabe', '2025-02-09'),
(5, 'Achat pour fete traditionnelle', '2025-03-21'),
(2, 'Constitution de troupeau de reproduction', '2025-04-12'),
(8, 'Achat de bovins pour engraissement', '2025-05-04'),
(10, 'Approvisionnement de restaurant viande', '2025-05-29'),
(6, 'Achat mixte production et revente', '2025-06-14'),
(4, 'Vente directe marche de gros', '2025-06-30');

INSERT INTO vente_detail (id_vente, id_bovin) VALUES
(1, 2),
(1, 3),
(2, 6),
(3, 8),
(4, 11),
(4, 13),
(5, 15),
(6, 16),
(7, 5),
(8, 10);
