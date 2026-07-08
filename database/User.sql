-- Active: 1782839472391@@127.0.0.1@5432@bovin_db
-- Table des rôles
CREATE TABLE IF NOT EXISTS role (
    id SERIAL PRIMARY KEY,
    nom VARCHAR(50) NOT NULL UNIQUE,
    description TEXT
);

-- Table des utilisateurs
CREATE TABLE IF NOT EXISTS utilisateur (
    id SERIAL PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100),
    nom VARCHAR(100),
    prenom VARCHAR(100),
    id_role INTEGER NOT NULL,
    actif BOOLEAN DEFAULT TRUE,
    date_creation TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_utilisateur_role FOREIGN KEY (id_role) 
        REFERENCES role(id)
);

-- Insertion des rôles par défaut
INSERT INTO role (nom, description) 
VALUES ('ADMIN', 'Administrateur avec tous les accès')
ON CONFLICT (nom) DO NOTHING;

INSERT INTO role (nom, description) 
VALUES ('GESTIONNAIRE', 'Gestionnaire avec accès limité à /bovins et /peseBovin')
ON CONFLICT (nom) DO NOTHING;

-- Insertion d'un utilisateur admin par défaut (mot de passe: admin123)
-- Le mot de passe est encodé en BCrypt: admin123
INSERT INTO utilisateur (username, password, email, nom, prenom, id_role, actif)
SELECT 'admin', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', 'admin@bovit.mg', 'Admin', 'Système', 
       (SELECT id FROM role WHERE nom = 'ADMIN'), true
WHERE NOT EXISTS (SELECT 1 FROM utilisateur WHERE username = 'admin');

-- Insertion d'un utilisateur gestionnaire par défaut (mot de passe: gest123)
INSERT INTO utilisateur (username, password, email, nom, prenom, id_role, actif)
SELECT 'gestionnaire', '$2a$10$CwTycUXWue0Thq9StjUM0uJx5cxZ2NxQhE5T5Z5Z5Z5Z5Z5Z5Z5Z5', 'gest@bovit.mg', 'Gestion', 'Troupeau', 
       (SELECT id FROM role WHERE nom = 'GESTIONNAIRE'), true
WHERE NOT EXISTS (SELECT 1 FROM utilisateur WHERE username = 'gestionnaire');