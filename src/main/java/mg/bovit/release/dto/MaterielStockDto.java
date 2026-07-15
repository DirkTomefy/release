package mg.bovit.release.dto;

import mg.bovit.release.model.Materiel;

public class MaterielStockDto {
    private Materiel materiel;
    private Double quantiteRestant;

    public MaterielStockDto() {
    }
    
    public MaterielStockDto(Materiel materiel, Double quantiteRestant) {
        this.materiel = materiel;
        this.quantiteRestant = quantiteRestant;
    }

    public Materiel getMateriel() {
        return materiel;
    }

    public void setMateriel(Materiel materiel) {
        this.materiel = materiel;
    }

    public Double getQuantiteRestant() {
        return quantiteRestant;
    }

    public void setQuantiteRestant(Double quantiteRestant) {
        this.quantiteRestant = quantiteRestant;
    }
}
