package mg.bovit.release.model;

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

    @Column(
        name = "poids_achat",
        nullable = false
    )
    private Double poids_achat;

    @Column(
        name = "poids_vente"    
    )
    private Double poids_vente;

    public Double getPoids_achat() {
        return poids_achat;
    }

    public void setPoids_achat(Double poids_achat) {
        this.poids_achat = poids_achat;
    }

    public Double getPoids_vente() {
        return poids_vente;
    }

    public void setPoids_vente(Double poids_vente) {
        this.poids_vente = poids_vente;
    }

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

    public Date getDate_achat() {
        return date_achat;
    }

    public void setDate_achat(Date date_achat) {
        this.date_achat = date_achat;
    }

    public Date getDate_vente() {
        return date_vente;
    }

    public void setDate_vente(Date date_vente) {
        this.date_vente = date_vente;
    }

    public Double getPrix_achat() {
        return prix_achat;
    }

    public void setPrix_achat(Double prix_achat) {
        this.prix_achat = prix_achat;
    }

    public Double getPrix_vente() {
        return prix_vente;
    }

    public void setPrix_vente(Double prix_vente) {
        this.prix_vente = prix_vente;
    }
}