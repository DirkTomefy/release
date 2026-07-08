package mg.bovit.release.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "mvt_stock_entree_paiement")
public class MouvementStockEntreePaiement {
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_mvt_stock_entree")
    private MouvementStockEntree mouvementStockEntree;

    @ManyToOne
    @JoinColumn(name = "id_caisse")
    private Caisse caisse;

    private Double montant;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public MouvementStockEntree getMouvementStockEntree() {
        return mouvementStockEntree;
    }

    public void setMouvementStockEntree(MouvementStockEntree mouvementStockEntree) {
        this.mouvementStockEntree = mouvementStockEntree;
    }

    public Caisse getCaisse() {
        return caisse;
    }

    public void setCaisse(Caisse caisse) {
        this.caisse = caisse;
    }

    public Double getMontant() {
        return montant;
    }

    public void setMontant(Double montant) {
        this.montant = montant;
    }
}
