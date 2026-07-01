package mg.bovit.release.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "mvt_stock")
public class MouvementStock {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_materiel")
    private Materiel materiel;

    private String typeMouvement; // ENTREE ou SORTIE
    private Double prixUnitaire;
    private Double qte;
    private Double qteEnStock;
    private java.sql.Date dateMouvement;

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

    public String getTypeMouvement() {
        return typeMouvement;
    }

    public void setTypeMouvement(String typeMouvement) {
        this.typeMouvement = typeMouvement;
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

    public Double getQteEnStock() {
        return qteEnStock;
    }

    public void setQteEnStock(Double qteEnStock) {
        this.qteEnStock = qteEnStock;
    }

    public java.sql.Date getDateMouvement() {
        return dateMouvement;
    }

    public void setDateMouvement(java.sql.Date dateMouvement) {
        this.dateMouvement = dateMouvement;
    }

}