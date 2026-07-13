-- Auth seed for local development.
-- The tables below are intentionally minimal and readable so the seed can be rerun easily.

-- =========================================================
-- 1) Schema
-- =========================================================
CREATE TABLE IF NOT EXISTS users (
    id SERIAL PRIMARY KEY,
    login VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    actif BOOLEAN NOT NULL DEFAULT TRUE
);

ALTER TABLE users
    ADD COLUMN IF NOT EXISTS actif BOOLEAN NOT NULL DEFAULT TRUE;

CREATE TABLE IF NOT EXISTS role (
    id SERIAL PRIMARY KEY,
    libelle VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS user_role (
    id_user INT NOT NULL,
    id_role INT NOT NULL,
    PRIMARY KEY (id_user, id_role),
    FOREIGN KEY (id_user) REFERENCES users (id) ON DELETE CASCADE,
    FOREIGN KEY (id_role) REFERENCES role (id) ON DELETE CASCADE
);

-- =========================================================
-- 2) Reset seed data
-- =========================================================
DELETE FROM user_role;
DELETE FROM users;
DELETE FROM role;

-- =========================================================
-- 3) Roles
-- =========================================================
INSERT INTO role (libelle) VALUES
    ('ADMIN'),
    ('VENTE'),
    ('PESEE'),
    ('LOT'),
    ('STOCK'),
    ('CAISSE'),
    ('EMPLOYE');

-- =========================================================
-- 4) Demo accounts
-- =========================================================
-- admin   -> admin123
-- vente   -> vente123
-- pesee   -> pesee123
-- lot     -> lot123
-- stock   -> stock123
-- caisse  -> caisse123
-- employe -> employe123

INSERT INTO users (login, password) VALUES
    ('admin', '$2a$10$3jJrKEYZUynHZjSpcZlgeuepQgjkHB4PLhnhKj.CqBqM1a9nd9l3u'),
    ('vente', '$2b$12$eq/si50na5QH.duUGjhj0u83ePqkc5/poe809gqLUEtfu7Eju3YYC'),
    ('pesee', '$2b$12$e/g0EXA../i/WAy6R42Ix.E/PsyT2Fw9DZ3p7241HqeSl7B.9J5Eu'),
    ('lot', '$2b$12$mwNr1QvqTX8qgQ8zkY5VHeI06PO4nwB9plAs7PGksGRGzg6.qUO0u'),
    ('stock', '$2b$12$LmbzH7GJ5VhLvyNBejyMA.R7Ar8pYbrn5F1m2koeMIdS4UYrwmDbG'),
    ('caisse', '$2b$12$.oMi1aG.Zf9F2dy0G3iVeuXihavI2wCm8H14vht1BoKctT7CdKp9q'),
    ('employe', '$2b$12$2tLq/lKziIZY.HD1eImYo.Gwuw1JAf54Ey/Y6yDs3T4wagdJJ0ZgG');

-- =========================================================
-- 5) User-role links
-- =========================================================
INSERT INTO user_role (id_user, id_role)
SELECT u.id, r.id
FROM users u
JOIN role r ON r.libelle = 'ADMIN'
WHERE u.login = 'admin';

INSERT INTO user_role (id_user, id_role)
SELECT u.id, r.id
FROM users u
JOIN role r ON r.libelle = 'VENTE'
WHERE u.login = 'vente';

INSERT INTO user_role (id_user, id_role)
SELECT u.id, r.id
FROM users u
JOIN role r ON r.libelle = 'PESEE'
WHERE u.login = 'pesee';

INSERT INTO user_role (id_user, id_role)
SELECT u.id, r.id
FROM users u
JOIN role r ON r.libelle = 'LOT'
WHERE u.login = 'lot';

INSERT INTO user_role (id_user, id_role)
SELECT u.id, r.id
FROM users u
JOIN role r ON r.libelle = 'STOCK'
WHERE u.login = 'stock';

INSERT INTO user_role (id_user, id_role)
SELECT u.id, r.id
FROM users u
JOIN role r ON r.libelle = 'CAISSE'
WHERE u.login = 'caisse';

INSERT INTO user_role (id_user, id_role)
SELECT u.id, r.id
FROM users u
JOIN role r ON r.libelle = 'EMPLOYE'
WHERE u.login = 'employe';