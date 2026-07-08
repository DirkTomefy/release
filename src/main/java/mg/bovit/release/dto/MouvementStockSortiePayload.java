package mg.bovit.release.dto;

public class MouvementStockSortiePayload {
    private Long materielId;
    private Double quantite;
    private String dateMouvement;

    public MouvementStockSortiePayload(Long materielId, Double quantite, String dateMouvement) {
        this.materielId = materielId;
        this.quantite = quantite;
        this.dateMouvement = dateMouvement;
    }

    public MouvementStockSortiePayload() {
    }

    public Long getMaterielId() {
        return materielId;
    }

    public void setMaterielId(Long materielId) {
        this.materielId = materielId;
    }

    public Double getQuantite() {
        return quantite;
    }

    public void setQuantite(Double quantite) {
        this.quantite = quantite;
    }

    public String getDateMouvement() {
        return dateMouvement;
    }

    public void setDateMouvement(String dateMouvement) {
        this.dateMouvement = dateMouvement;
    }
}
