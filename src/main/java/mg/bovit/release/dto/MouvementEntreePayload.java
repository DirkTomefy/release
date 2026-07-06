package mg.bovit.release.dto;

import java.util.List;

public class MouvementEntreePayload {
    private Long materielId;
    private Double prixUnitaire;
    private Double quantite;
    private String dateMouvement;
    private List<MouvementEntreePaiementPayload> payments;

    public MouvementEntreePayload() {
    }

    public MouvementEntreePayload(Long materielId, Double prixUnitaire, Double quantite, String dateMouvement, List<MouvementEntreePaiementPayload> payments) {
        this.materielId = materielId;
        this.prixUnitaire = prixUnitaire;
        this.quantite = quantite;
        this.dateMouvement = dateMouvement;
        this.payments = payments;
    }

    public Long getMaterielId() {
        return materielId;
    }

    public void setMaterielId(Long materielId) {
        this.materielId = materielId;
    }

    public Double getPrixUnitaire() {
        return prixUnitaire;
    }

    public void setPrixUnitaire(Double prixUnitaire) {
        this.prixUnitaire = prixUnitaire;
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

    public List<MouvementEntreePaiementPayload> getPayments() {
        return payments;
    }

    public void setPayments(List<MouvementEntreePaiementPayload> payments) {
        this.payments = payments;
    }
}
