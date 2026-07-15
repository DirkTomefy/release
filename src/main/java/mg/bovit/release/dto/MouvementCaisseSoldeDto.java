package mg.bovit.release.dto;

import mg.bovit.release.model.Caisse;

// a utiliser si on veut solde depuis calcul dans mvt_caisse
public class MouvementCaisseSoldeDto {
    private Caisse caisse;
    private Double solde;

    public MouvementCaisseSoldeDto() {}

    public MouvementCaisseSoldeDto(Caisse caisse, Double solde) {
        this.caisse = caisse;
        this.solde = solde;
    }

    public Caisse getCaisse() { return caisse; }
    public void setCaisse(Caisse caisse) { this.caisse = caisse; }
    public Double getSolde() { return solde; }
    public void setSolde(Double solde) { this.solde = solde; }
}