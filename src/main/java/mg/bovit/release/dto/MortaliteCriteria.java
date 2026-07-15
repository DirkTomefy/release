package mg.bovit.release.dto;

public class MortaliteCriteria {

    private Long raceId;

    // Dates au format ISO "yyyy-MM-dd" (celui produit par <input type="date">)
    private String dateMin;
    private String dateMax;

    private Integer page = 0;
    private Integer size = 10;

    public Long getRaceId() { return raceId; }
    public void setRaceId(Long raceId) { this.raceId = raceId; }

    public String getDateMin() { return dateMin; }
    public void setDateMin(String dateMin) { this.dateMin = dateMin; }

    public String getDateMax() { return dateMax; }
    public void setDateMax(String dateMax) { this.dateMax = dateMax; }

    public Integer getPage() { return page; }
    public void setPage(Integer page) { this.page = page; }

    public Integer getSize() { return size; }
    public void setSize(Integer size) { this.size = size; }
}
