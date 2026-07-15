package mg.bovit.release.model;

import jakarta.persistence.*;

@Entity
@Table(name = "inventaire_detail")
public class InventaireDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_inventaire", nullable = false)
    private Inventaire inventaire;

    @ManyToOne
    @JoinColumn(name = "id_materiel", nullable = false)
    private Materiel materiel;

    private Double quantiteInitiale;
    private Double quantiteFinale;

    private String observations;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Inventaire getInventaire() {
        return inventaire;
    }

    public void setInventaire(Inventaire inventaire) {
        this.inventaire = inventaire;
    }

    public Materiel getMateriel() {
        return materiel;
    }

    public void setMateriel(Materiel materiel) {
        this.materiel = materiel;
    }

    public Double getQuantiteInitiale() {
        return quantiteInitiale;
    }

    public void setQuantiteInitiale(Double quantiteInitiale) {
        this.quantiteInitiale = quantiteInitiale;
    }

    public Double getQuantiteFinale() {
        return quantiteFinale;
    }

    public void setQuantiteFinale(Double quantiteFinale) {
        this.quantiteFinale = quantiteFinale;
    }

    public String getObservations() {
        return observations;
    }

    public void setObservations(String observations) {
        this.observations = observations;
    }

}