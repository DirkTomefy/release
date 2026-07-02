package mg.bovit.release.dto;

import mg.bovit.release.model.Caisse;

public class MouvementCaisseSoldeDto {
    private Caisse caisse;
    private Double solde;

    public Caisse getCaisse() {
        return caisse;
    }

    public void setCaisse(Caisse caisse) {
        this.caisse = caisse;
    }

    public Double getSolde() {
        return solde;
    }

    public void setSolde(Double solde) {
        this.solde = solde;
    }

}
