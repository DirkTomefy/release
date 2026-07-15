package mg.bovit.release.dto;

import java.util.List;

public class MouvementStockPayload {
    private String typeMouvement; // ENTREE ou SORTIE
    private Long materielId;
    private Double quantite;
    private String dateMouvement;
    private Double prixUnitaire; // Null si typeMouvement est SORTIE
    private List<MouvementPaiementPayload> payments; // Vide si typeMouvement est SORTIE

    public MouvementStockPayload() {
    }

    public MouvementStockPayload(String typeMouvement, Long materielId, Double quantite, String dateMouvement, Double prixUnitaire, List<MouvementPaiementPayload> payments) {
        this.typeMouvement = typeMouvement;
        this.materielId = materielId;
        this.quantite = quantite;
        this.dateMouvement = dateMouvement;
        this.prixUnitaire = prixUnitaire;
        this.payments = payments;
    }

    public String getTypeMouvement() {
        return typeMouvement;
    }

    public void setTypeMouvement(String typeMouvement) {
        this.typeMouvement = typeMouvement;
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

    public Double getPrixUnitaire() {
        return prixUnitaire;
    }

    public void setPrixUnitaire(Double prixUnitaire) {
        this.prixUnitaire = prixUnitaire;
    }

    public List<MouvementPaiementPayload> getPayments() {
        return payments;
    }

    public void setPayments(List<MouvementPaiementPayload> payments) {
        this.payments = payments;
    }
}