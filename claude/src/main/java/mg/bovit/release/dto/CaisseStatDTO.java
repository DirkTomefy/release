package mg.bovit.release.dto;

import java.util.List;

/**
 * Données agrégées d'entrées/sorties de caisse, prêtes à être consommées
 * par un histogramme (Chart.js) côté vue. Une "barre" correspond à un
 * intervalle de temps (jour, semaine ou mois selon l'étendue de la période
 * demandée) et porte le total des entrées et le total des sorties de cet
 * intervalle.
 */
public class CaisseStatDTO {

    // Libellés des barres de l'histogramme (ex : "04/07", "Sem. 01/07", "juil. 2026")
    private List<String> labels;

    // Total des entrées (montants positifs) pour chaque intervalle
    private List<Double> entrees;

    // Total des sorties (valeur absolue des montants négatifs) pour chaque intervalle
    private List<Double> sorties;

    // Cumuls sur toute la période filtrée (entrées/sorties)
    private Double totalEntree = 0.0;
    private Double totalSortie = 0.0;

    // Solde RÉEL de la caisse à la date de fin sélectionnée : cumul de tout
    // l'historique des mouvements jusqu'à cette date (pas seulement
    // totalEntree - totalSortie de la période affichée).
    private Double solde = 0.0;

    // Renseigné uniquement en cas d'erreur (ex : dates invalides), sinon null
    private String erreur;

    public List<String> getLabels() {
        return labels;
    }

    public void setLabels(List<String> labels) {
        this.labels = labels;
    }

    public List<Double> getEntrees() {
        return entrees;
    }

    public void setEntrees(List<Double> entrees) {
        this.entrees = entrees;
    }

    public List<Double> getSorties() {
        return sorties;
    }

    public void setSorties(List<Double> sorties) {
        this.sorties = sorties;
    }

    public Double getTotalEntree() {
        return totalEntree;
    }

    public void setTotalEntree(Double totalEntree) {
        this.totalEntree = totalEntree;
    }

    public Double getTotalSortie() {
        return totalSortie;
    }

    public void setTotalSortie(Double totalSortie) {
        this.totalSortie = totalSortie;
    }

    public Double getSolde() {
        return solde;
    }

    public void setSolde(Double solde) {
        this.solde = solde;
    }

    public String getErreur() {
        return erreur;
    }

    public void setErreur(String erreur) {
        this.erreur = erreur;
    }
}
