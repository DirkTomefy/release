package mg.bovit.release.dto;

import java.util.List;

public class MortaliteStatsDTO {

    private Long totalMortalites;
    private List<String> labels;  // mois formatés "YYYY-MM"
    private List<Long> counts;    // nombre de mortalités par mois

    public Long getTotalMortalites() { return totalMortalites; }
    public void setTotalMortalites(Long totalMortalites) { this.totalMortalites = totalMortalites; }

    public List<String> getLabels() { return labels; }
    public void setLabels(List<String> labels) { this.labels = labels; }

    public List<Long> getCounts() { return counts; }
    public void setCounts(List<Long> counts) { this.counts = counts; }
}
