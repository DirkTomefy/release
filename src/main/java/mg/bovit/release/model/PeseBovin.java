package mg.bovit.release.model;

import jakarta.persistence.*;

import java.sql.Date;

@Entity
@Table(name="pese_bovin")
public class PeseBovin {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name="id_bovin")
    private Bovin bovin;

    @Column(
        name="date_pese",
        nullable = false
    )
    private Date date_pese;

    @Column(
        name="poids_apres",
        nullable = false
    )
    private Double poids_apres;

    // getters / setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Bovin getBovin() {
        return bovin;
    }

    public void setBovin(Bovin bovin) {
        this.bovin = bovin;
    }

    public Date getDate_pese() {
        return date_pese;
    }

    public void setDate_pese(Date date_pese) {
        this.date_pese = date_pese;
    }

    public Double getPoids_apres() {
        return poids_apres;
    }

    public void setPoids_apres(Double poids_apres) {
        this.poids_apres = poids_apres;
    }
}
