-- Active: 1779074118545@@127.0.0.1@5432@bovin_db
CREATE TABLE
    users (
        id SERIAL PRIMARY KEY,
        login VARCHAR(255) NOT NULL UNIQUE,
        password VARCHAR(255) NOT NULL
    );

CREATE TABLE
    role (
        id SERIAL PRIMARY KEY,
        libelle VARCHAR(255) NOT NULL UNIQUE
    );

CREATE TABLE
    user_role (
        id_user INT NOT NULL,
        id_role INT NOT NULL,
        PRIMARY KEY (id_user, id_role),
        FOREIGN KEY (id_user) REFERENCES users (id) ON DELETE CASCADE,
        FOREIGN KEY (id_role) REFERENCES role (id) ON DELETE CASCADE
    );

INSERT INTO
     role (libelle)
VALUES
    ('ADMIN'),
    ('VENTE'),
    ('PESEE'),
    ('LOT'),
    ('STOCK'),
    ('CAISE');