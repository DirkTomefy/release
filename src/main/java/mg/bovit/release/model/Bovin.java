package mg.bovit.release.model;

import java.lang.annotation.Inherited;

import jakarta.persistence.*;

import java.sql.Date;

@Entity
@Table(name="bovin")
public class Bovin {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name="id_race")
    private Race race;

    @Column(
        name = "date_achat",
        nullable = false
    )
    private Date date_achat;

    @Column(
        name = "date_vente"    
    )
    private Date date_vente;

    @Column(
        name = "prix_achat",
        nullable = false
    )
    private Double prix_achat;

    @Column(
        name = "prix_vente"    
    )
    private Double prix_vente;
}