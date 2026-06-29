package mg.bovit.release.model;

import java.lang.annotation.Inherited;

import jakarta.persistence.*;

import java.sql.Date;

@Entity
@Table(name="caisse")
public class Caisse {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(
        name = "libelle",
        nullable = false
    )
    private String libelle;

    @Column(
        name = "montant_actuelle",
        nullable = false
    )
    private Double montant_actuelle;
}