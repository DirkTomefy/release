package mg.bovit.release.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Column;

@Entity
@Table(name = "mouvement_stock")
public class MouvementStock {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_materiel")
    private Materiel materiel;

    @Column(name = "date_mouvement")
    private java.sql.Date dateMouvement;

    @Column(name = "type_mouvement")
    private String typeMouvement; // ENTREE ou SORTIE

    @Column(name = "quantite")
    private Double quantite;

    @Column(name = "prix_unitaire")
    private Double prixUnitaire; // Null si type_mouvement est SORTIE

    @Column(name = "qte_restant")
    private Double qteRestant; // Utile pour la gestion FIFO/LIFO sur les entrees

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

    public java.sql.Date getDateMouvement() {
        return dateMouvement;
    }

    public void setDateMouvement(java.sql.Date dateMouvement) {
        this.dateMouvement = dateMouvement;
    }

    public String getTypeMouvement() {
        return typeMouvement;
    }

    public void setTypeMouvement(String typeMouvement) {
        this.typeMouvement = typeMouvement;
    }

    public Double getQuantite() {
        return quantite;
    }

    public void setQuantite(Double quantite) {
        this.quantite = quantite;
    }

    public Double getPrixUnitaire() {
        return prixUnitaire;
    }

    public void setPrixUnitaire(Double prixUnitaire) {
        this.prixUnitaire = prixUnitaire;
    }

    public Double getQteRestant() {
        return qteRestant;
    }

    public void setQteRestant(Double qteRestant) {
        this.qteRestant = qteRestant;
    }
}