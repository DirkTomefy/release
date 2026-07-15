package mg.bovit.release.model.sqlview;

import java.sql.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import mg.bovit.release.model.Bovin;

@Entity
@Table(name = "v_pese_bovin_with_date_vente")
public class PeseBovinWithDateVente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_bovin")
    private Bovin bovin;

    @Column(name = "date_pese", nullable = false)
    private Date date_pese;

    @Column(name = "poids_apres", nullable = false)
    private Double poids_apres;

    @Column(name = "date_vente", nullable = false)
    private Date date_vente;

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

    public Date getDate_vente() {
        return date_vente;
    }

    public void setDate_vente(Date date_vente) {
        this.date_vente = date_vente;
    }
}
