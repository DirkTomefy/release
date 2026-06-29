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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLibelle() {
        return libelle;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }

    public Double getMontant_actuelle() {
        return montant_actuelle;
    }

    public void setMontant_actuelle(Double montant_actuelle) {
        this.montant_actuelle = montant_actuelle;
    }
}