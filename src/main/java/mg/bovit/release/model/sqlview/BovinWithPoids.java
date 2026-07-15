package mg.bovit.release.model.sqlview;

import jakarta.persistence.*;
import java.sql.Date;

@Entity
@Table(name = "v_bovin_poids_actuel")
public class BovinWithPoids {
    
    @Id
    private Long id;
    
    @Column(name = "id_race")
    private Long idRace;
    
    @Column(name = "date_achat")
    private Date dateAchat;
    
    @Column(name = "date_vente")
    private Date dateVente;
    
    @Column(name = "prix_achat")
    private Double prixAchat;
    
    @Column(name = "prix_vente")
    private Double prixVente;
    
    @Column(name = "poids_achat")
    private Double poidsAchat;
    
    @Column(name = "poids_vente")
    private Double poidsVente;
    
    @Column(name = "race_nom")
    private String raceNom;
    
    @Column(name = "race_description")
    private String raceDescription;
    
    @Column(name = "poids_actuel")
    private Double poidsActuel;
    
    @Column(name = "date_dernier_pese")
    private Date dateDernierPese;
    
    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getIdRace() { return idRace; }
    public void setIdRace(Long idRace) { this.idRace = idRace; }
    
    public Date getDateAchat() { return dateAchat; }
    public void setDateAchat(Date dateAchat) { this.dateAchat = dateAchat; }
    
    public Date getDateVente() { return dateVente; }
    public void setDateVente(Date dateVente) { this.dateVente = dateVente; }
    
    public Double getPrixAchat() { return prixAchat; }
    public void setPrixAchat(Double prixAchat) { this.prixAchat = prixAchat; }
    
    public Double getPrixVente() { return prixVente; }
    public void setPrixVente(Double prixVente) { this.prixVente = prixVente; }
    
    public Double getPoidsAchat() { return poidsAchat; }
    public void setPoidsAchat(Double poidsAchat) { this.poidsAchat = poidsAchat; }
    
    public Double getPoidsVente() { return poidsVente; }
    public void setPoidsVente(Double poidsVente) { this.poidsVente = poidsVente; }
    
    public String getRaceNom() { return raceNom; }
    public void setRaceNom(String raceNom) { this.raceNom = raceNom; }
    
    public String getRaceDescription() { return raceDescription; }
    public void setRaceDescription(String raceDescription) { this.raceDescription = raceDescription; }
    
    public Double getPoidsActuel() { return poidsActuel; }
    public void setPoidsActuel(Double poidsActuel) { this.poidsActuel = poidsActuel; }
    
    public Date getDateDernierPese() { return dateDernierPese; }
    public void setDateDernierPese(Date dateDernierPese) { this.dateDernierPese = dateDernierPese; }
}