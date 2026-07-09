
CREATE TABLE client (
    id SERIAL PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    prenom VARCHAR(100) NOT NULL,
    contact VARCHAR(100) NOT NULL
);

CREATE TABLE vente_bovin (
    id SERIAL PRIMARY KEY,
    id_client INT NOT NULL,
    description VARCHAR(200),
    date_vente DATE NOT NULL DEFAULT CURRENT_DATE,

    CONSTRAINT fk_vente_bovin_client FOREIGN KEY (id_client)
    REFERENCES client(id)
);

CREATE Table vente_detail (
    id SERIAL PRIMARY KEY,
    id_vente INT NOT NULL,
    id_bovin INT NOT NULL,

    CONSTRAINT fk_vente_detail_vente FOREIGN KEY (id_vente)
    REFERENCES vente_bovin(id),
    CONSTRAINT fk_vente_detail_bovin FOREIGN KEY (id_bovin)
    REFERENCES bovin(id),
    CONSTRAINT fk_vente_detail_bovin UNIQUE (id_bovin)
)
