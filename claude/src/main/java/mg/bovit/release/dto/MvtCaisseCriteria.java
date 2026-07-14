package mg.bovit.release.dto;

public class MvtCaisseCriteria {

    private Long caisseId;
    private Long causeCaisseId;

    // Dates au format ISO "yyyy-MM-dd" (celui produit par <input type="date">)
    private String dateMin;
    private String dateMax;

    private Integer page = 0;
    private Integer size = 20;

    public Long getCaisseId() { return caisseId; }
    public void setCaisseId(Long caisseId) { this.caisseId = caisseId; }

    public Long getCauseCaisseId() { return causeCaisseId; }
    public void setCauseCaisseId(Long causeCaisseId) { this.causeCaisseId = causeCaisseId; }

    public String getDateMin() { return dateMin; }
    public void setDateMin(String dateMin) { this.dateMin = dateMin; }

    public String getDateMax() { return dateMax; }
    public void setDateMax(String dateMax) { this.dateMax = dateMax; }

    public Integer getPage() { return page; }
    public void setPage(Integer page) { this.page = page; }

    public Integer getSize() { return size; }
    public void setSize(Integer size) { this.size = size; }
}
