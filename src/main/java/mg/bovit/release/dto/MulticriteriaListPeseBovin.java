package mg.bovit.release.dto;

import java.sql.Date;

public class MulticriteriaListPeseBovin {

  
    private Long raceId;
    private Date dateRecherePese;
    private Double prixAchatMin;
    private Double prixAchatMax;

    private String statut; // "tous", "vendu", "non_vendu"

    // Pagination
    private int page = 0;
    private int size = 10;
    private String sort = "id,asc";

    
    public Long getRaceId() {
        return raceId;
    }

    public void setRaceId(Long raceId) {
        this.raceId = raceId;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public Date getDateRecherePese() {
        return dateRecherePese;
    }

    public void setDateRecherePese(Date dateRecherePese) {
        this.dateRecherePese = dateRecherePese;
    }

    public Double getPrixAchatMin() {
        return prixAchatMin;
    }

    public void setPrixAchatMin(Double prixAchatMin) {
        this.prixAchatMin = prixAchatMin;
    }

    public Double getPrixAchatMax() {
        return prixAchatMax;
    }

    public void setPrixAchatMax(Double prixAchatMax) {
        this.prixAchatMax = prixAchatMax;
    }

}