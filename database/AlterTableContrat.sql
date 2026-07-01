ALTER TABLE contrat ADD COLUMN id_employee INT NOT NULL;

ALTER TABLE contrat 
ADD CONSTRAINT fk_contrat_employee 
FOREIGN KEY (id_employee) REFERENCES employee(id);