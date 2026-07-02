package mg.bovit.release.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "mvt_stock_entree")
public class MouvementStockEntree {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_materiel")
    private Materiel materiel;

    private Double prixUnitaire;
    private Double qte;
    private Double qteRestant;
    private java.sql.Date dateEntree;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Materiel getMateriel() {
        return materiel;
    }

    public void setMateriel(Materiel materiel) {
        this.materiel = materiel;
    }

    public Double getPrixUnitaire() {
        return prixUnitaire;
    }

    public void setPrixUnitaire(Double prixUnitaire) {
        this.prixUnitaire = prixUnitaire;
    }

    public Double getQte() {
        return qte;
    }

    public void setQte(Double qte) {
        this.qte = qte;
    }

    public Double getQteRestant() {
        return qteRestant;
    }

    public void setQteRestant(Double qteRestant) {
        this.qteRestant = qteRestant;
    }

    public java.sql.Date getDateEntree() {
        return dateEntree;
    }

    public void setDateEntree(java.sql.Date dateEntree) {
        this.dateEntree = dateEntree;
    }

}