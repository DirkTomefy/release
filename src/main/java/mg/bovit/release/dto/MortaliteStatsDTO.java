package mg.bovit.release.dto;

import java.util.List;

public class MortaliteStatsDTO {

    private Long totalMortalites;
    private Double totalPrixMortalites;
    private List<String> labels;  // mois formatés "YYYY-MM"
    private List<Long> counts;    // nombre de mortalités par mois
    private List<Double> prixTotals; // prix total des mortalités par mois

    public Long getTotalMortalites() { return totalMortalites; }
    public void setTotalMortalites(Long totalMortalites) { this.totalMortalites = totalMortalites; }

    public Double getTotalPrixMortalites() { return totalPrixMortalites; }
    public void setTotalPrixMortalites(Double totalPrixMortalites) { this.totalPrixMortalites = totalPrixMortalites; }

    public List<String> getLabels() { return labels; }
    public void setLabels(List<String> labels) { this.labels = labels; }

    public List<Long> getCounts() { return counts; }
    public void setCounts(List<Long> counts) { this.counts = counts; }

    public List<Double> getPrixTotals() { return prixTotals; }
    public void setPrixTotals(List<Double> prixTotals) { this.prixTotals = prixTotals; }
}
