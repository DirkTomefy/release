package mg.bovit.release.dto;

import java.math.BigDecimal;
import java.sql.Date;

public class EmployeeContratDTO {
    // Informations de l'employé
    private String nom;
    private String prenom;
    private Date dateNaissance;
    private Date dateEntree;

    // Informations du premier contrat
    private Date dateDebut;
    private Date dateFin;
    private BigDecimal salaire;

    // Getters et Setters
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }

    public Date getDateNaissance() { return dateNaissance; }
    public void setDateNaissance(Date dateNaissance) { this.dateNaissance = dateNaissance; }

    public Date getDateEntree() { return dateEntree; }
    public void setDateEntree(Date dateEntree) { this.dateEntree = dateEntree; }

    public Date getDateDebut() { return dateDebut; }
    public void setDateDebut(Date dateDebut) { this.dateDebut = dateDebut; }

    public Date getDateFin() { return dateFin; }
    public void setDateFin(Date dateFin) { this.dateFin = dateFin; }

    public BigDecimal getSalaire() { return salaire; }
    public void setSalaire(BigDecimal salaire) { this.salaire = salaire; }
}