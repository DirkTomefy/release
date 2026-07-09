package mg.bovit.release.model;

import jakarta.persistence.*;

import java.sql.Date;

@Entity
@Table(name = "mortalite")
public class Mortalite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_race", nullable = false)
    private Race race;

    @Column(
        name = "prix_achat",
        nullable = false
    )
    private Double prix_achat;

    @Column(
        name = "poids_mort",
        nullable = false
    )
    private Double poids_mort;

    @Column(
        name = "date",
        nullable = false
    )
    private Date date;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Race getRace() {
        return race;
    }

    public void setRace(Race race) {
        this.race = race;
    }

    public Double getPrix_achat() {
        return prix_achat;
    }

    public void setPrix_achat(Double prix_achat) {
        this.prix_achat = prix_achat;
    }

    public Double getPoids_mort() {
        return poids_mort;
    }

    public void setPoids_mort(Double poids_mort) {
        this.poids_mort = poids_mort;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
