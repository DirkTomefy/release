package mg.bovit.release.dto;

import mg.bovit.release.model.Caisse;

public class MouvementCaisseSoldeDto {
    private Caisse caisse;
    private Double solde;

    // Constructeur vide nécessaire
    public MouvementCaisseSoldeDto() {}

    // LE CONSTRUCTEUR POUR LE JPQL
    public MouvementCaisseSoldeDto(Caisse caisse, Double solde) {
        this.caisse = caisse;
        this.solde = solde;
    }

    // Tes getters et setters restent inchangés...
    public Caisse getCaisse() { return caisse; }
    public void setCaisse(Caisse caisse) { this.caisse = caisse; }
    public Double getSolde() { return solde; }
    public void setSolde(Double solde) { this.solde = solde; }
}