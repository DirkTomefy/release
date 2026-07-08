package mg.bovit.release.dto;

import java.util.List;

public class VenteStatsDTO {
    private Long totalVentes;
    private Long totalBovinsVendus;
    private Double montantTotal;
    private List<String> labels;      // mois formatés "YYYY-MM"
    private List<Double> montants;    // montant total par mois
    private List<Long> counts;        // nombre de bovins vendus par mois

    // Getters et setters
    public Long getTotalVentes() { return totalVentes; }
    public void setTotalVentes(Long totalVentes) { this.totalVentes = totalVentes; }

    public Long getTotalBovinsVendus() { return totalBovinsVendus; }
    public void setTotalBovinsVendus(Long totalBovinsVendus) { this.totalBovinsVendus = totalBovinsVendus; }

    public Double getMontantTotal() { return montantTotal; }
    public void setMontantTotal(Double montantTotal) { this.montantTotal = montantTotal; }

    public List<String> getLabels() { return labels; }
    public void setLabels(List<String> labels) { this.labels = labels; }

    public List<Double> getMontants() { return montants; }
    public void setMontants(List<Double> montants) { this.montants = montants; }

    public List<Long> getCounts() { return counts; }
    public void setCounts(List<Long> counts) { this.counts = counts; }
}