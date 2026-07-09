package mg.bovit.release.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "mvt_stock_paiement")
public class MouvementStockPaiement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_mouvement_stock")
    private MouvementStock mouvementStock;

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

    public MouvementStock getMouvementStock() {
        return mouvementStock;
    }

    public void setMouvementStock(MouvementStock mouvementStock) {
        this.mouvementStock = mouvementStock;
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