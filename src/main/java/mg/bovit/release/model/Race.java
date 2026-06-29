package mg.bovit.release.model;

import java.lang.annotation.Inherited;

import jakarta.persistence.*;

import java.sql.Date;

@Entity
@Table(name="race")
public class Race {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(
        name = "nom",
        nullable = false
    )
    private String nom;

    @Column(
        name = "descriptions"    
    )
    private String descriptions;
}