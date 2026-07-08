package mg.bovit.release.dto;

public class MouvementEntreePaiementPayload {
    private Long caisseId;
    private Double montant;

    public MouvementEntreePaiementPayload(Long caisseId, Double montant) {
        this.caisseId = caisseId;
        this.montant = montant;
    }

    public Long getCaisseId() {
        return caisseId;
    }

    public void setCaisseId(Long caisseId) {
        this.caisseId = caisseId;
    }

    public Double getMontant() {
        return montant;
    }

    public void setMontant(Double montant) {
        this.montant = montant;
    }
}
