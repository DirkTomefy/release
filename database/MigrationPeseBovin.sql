-- Active: 1779074118545@@127.0.0.1@5432@bovin_db
CREATE VIEW
    v_pese_bovin_with_date_vente AS (
        SELECT
            pb.*,
            b.date_vente
        FROM
            pese_bovin pb
            JOIN public.bovin b on pb.id_bovin = b.id
    );