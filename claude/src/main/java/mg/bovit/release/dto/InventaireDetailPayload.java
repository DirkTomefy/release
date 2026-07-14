package mg.bovit.release.dto;

public class InventaireDetailPayload {
    private Long materielId;
    private Double quantiteInitiale;
    private Double quantiteFinale;
    private String observations;

    public InventaireDetailPayload() {
    }

    public InventaireDetailPayload(Long materielId, Double quantiteInitiale, Double quantiteFinale,
            String observations) {
        this.materielId = materielId;
        this.quantiteInitiale = quantiteInitiale;
        this.quantiteFinale = quantiteFinale;
        this.observations = observations;
    }

    public Long getMaterielId() {
        return materielId;
    }

    public void setMaterielId(Long materielId) {
        this.materielId = materielId;
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
