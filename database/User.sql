-- Active: 1782839472391@@127.0.0.1@5432@bovin_db

-- ============================================
-- 1. SUPPRIMER LES TABLES EXISTANTES (si besoin)
-- ============================================
DROP TABLE IF EXISTS utilisateur CASCADE;
DROP TABLE IF EXISTS role CASCADE;

-- ============================================
-- 2. CRÉATION DES TABLES
-- ============================================

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

-- ============================================
-- 3. INSERTION DES RÔLES
-- ============================================

INSERT INTO role (nom, description) 
VALUES ('ADMIN', 'Administrateur avec tous les accès')
ON CONFLICT (nom) DO NOTHING;

INSERT INTO role (nom, description) 
VALUES ('GESTIONNAIRE', 'Gestionnaire avec accès limité à /bovins et /peseBovin')
ON CONFLICT (nom) DO NOTHING;

-- ============================================
-- 4. INSERTION DES UTILISATEURS AVEC MOTS DE PASSE EN CLAIR
-- ============================================

-- Supprimer les anciens utilisateurs (pour éviter les doublons)
DELETE FROM utilisateur WHERE username IN ('admin', 'gestionnaire', 'user');

-- Insertion de l'administrateur (mot de passe: admin123)
INSERT INTO utilisateur (username, password, email, nom, prenom, id_role, actif, date_creation)
VALUES (
    'admin', 
    'admin123', 
    'admin@bovit.mg', 
    'Admin', 
    'Système', 
    (SELECT id FROM role WHERE nom = 'ADMIN'), 
    true, 
    CURRENT_TIMESTAMP
);

-- Insertion du gestionnaire (mot de passe: gest123)
INSERT INTO utilisateur (username, password, email, nom, prenom, id_role, actif, date_creation)
VALUES (
    'gestionnaire', 
    'gest123', 
    'gest@bovit.mg', 
    'Gestion', 
    'Troupeau', 
    (SELECT id FROM role WHERE nom = 'GESTIONNAIRE'), 
    true, 
    CURRENT_TIMESTAMP
);

-- Insertion d'un utilisateur standard (mot de passe: user123)
INSERT INTO utilisateur (username, password, email, nom, prenom, id_role, actif, date_creation)
VALUES (
    'user', 
    'user123',  
    'user@bovit.mg', 
    'Utilisateur', 
    'Standard', 
    (SELECT id FROM role WHERE nom = 'GESTIONNAIRE'), 
    true, 
    CURRENT_TIMESTAMP
);

-- ============================================
-- 5. VÉRIFICATION DES DONNÉES
-- ============================================

-- Vérifier les rôles
SELECT * FROM role;

-- Vérifier les utilisateurs (mots de passe en clair visibles !)
SELECT id, username, password, email, nom, prenom, id_role, actif, date_creation 
FROM utilisateur;

-- Vérifier les utilisateurs avec leurs rôles
SELECT 
    u.id,
    u.username,
    u.password,
    u.email,
    u.nom,
    u.prenom,
    r.nom AS role,
    u.actif
FROM utilisateur u
JOIN role r ON u.id_role = r.id
ORDER BY u.id;

