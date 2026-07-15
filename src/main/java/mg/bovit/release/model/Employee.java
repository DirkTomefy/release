package mg.bovit.release.model;

import jakarta.persistence.*;

import java.sql.Date;

@Entity
@Table(name = "employee")
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(
        name = "nom",
        nullable = false
    )
    private String nom;

    @Column(
        name = "prenom",
        nullable = false
    )
    private String prenom;

    @Column(
        name = "date_naissance",
        nullable = false
    )
    private Date dateNaissance;

    @Column(
        name = "date_entree",
        nullable = false
    )
    private Date dateEntree;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public Date getDateNaissance() {
        return dateNaissance;
    }

    public void setDateNaissance(Date dateNaissance) {
        this.dateNaissance = dateNaissance;
    }

    public Date getDateEntree() {
        return dateEntree;
    }

    public void setDateEntree(Date dateEntree) {
        this.dateEntree = dateEntree;
    }
}