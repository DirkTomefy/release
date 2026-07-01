package mg.bovit.release.dto;

import java.sql.Date;

public class MultiCriteriaFormBovinList {

    // Filtres
    private Long raceId;
    private Date dateAchatMin;
    private Date dateAchatMax;
    private Double prixAchatMin;
    private Double prixAchatMax;
    private Double poidsMin;
    private Double poidsMax;
    private String statut;            // "tous", "vendu", "non_vendu"

    // Pagination
    private int page = 0;
    private int size = 10;
    private String sort = "id,asc";   // ex: "dateAchat,desc"

    // Getters et setters
    public Long getRaceId() { return raceId; }
    public void setRaceId(Long raceId) { this.raceId = raceId; }
    
    public Date getDateAchatMin() { return dateAchatMin; }
    public void setDateAchatMin(Date dateAchatMin) { this.dateAchatMin = dateAchatMin; }
    
    public Date getDateAchatMax() { return dateAchatMax; }
    public void setDateAchatMax(Date dateAchatMax) { this.dateAchatMax = dateAchatMax; }
    
    public Double getPrixAchatMin() { return prixAchatMin; }
    public void setPrixAchatMin(Double prixAchatMin) { this.prixAchatMin = prixAchatMin; }
    
    public Double getPrixAchatMax() { return prixAchatMax; }
    public void setPrixAchatMax(Double prixAchatMax) { this.prixAchatMax = prixAchatMax; }
    
    public Double getPoidsMin() { return poidsMin; }
    public void setPoidsMin(Double poidsMin) { this.poidsMin = poidsMin; }
    
    public Double getPoidsMax() { return poidsMax; }
    public void setPoidsMax(Double poidsMax) { this.poidsMax = poidsMax; }
    
    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }
    
    public int getPage() { return page; }
    public void setPage(int page) { this.page = page; }
    
    public int getSize() { return size; }
    public void setSize(int size) { this.size = size; }
    
    public String getSort() { return sort; }
    public void setSort(String sort) { this.sort = sort; }
}