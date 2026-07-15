package mg.bovit.release.dto;

import java.util.List;

public class FinancialStatsDTO {
    private List<String> labels;
    private List<Double> entrees;
    private List<Double> sorties;
    private List<Double> benefices;
    private Double totalEntrees;
    private Double totalSorties;
    private Double totalBenefice;

    // Getters et setters
    public List<String> getLabels() { return labels; }
    public void setLabels(List<String> labels) { this.labels = labels; }
    public List<Double> getEntrees() { return entrees; }
    public void setEntrees(List<Double> entrees) { this.entrees = entrees; }
    public List<Double> getSorties() { return sorties; }
    public void setSorties(List<Double> sorties) { this.sorties = sorties; }
    public List<Double> getBenefices() { return benefices; }
    public void setBenefices(List<Double> benefices) { this.benefices = benefices; }
    public Double getTotalEntrees() { return totalEntrees; }
    public void setTotalEntrees(Double totalEntrees) { this.totalEntrees = totalEntrees; }
    public Double getTotalSorties() { return totalSorties; }
    public void setTotalSorties(Double totalSorties) { this.totalSorties = totalSorties; }
    public Double getTotalBenefice() { return totalBenefice; }
    public void setTotalBenefice(Double totalBenefice) { this.totalBenefice = totalBenefice; }
}