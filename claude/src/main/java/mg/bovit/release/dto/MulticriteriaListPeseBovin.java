package mg.bovit.release.dto;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

public class MulticriteriaListPeseBovin {

  
    private Long raceId;
    
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate datePeseMin;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate datePeseMax;

    private Double poidsApresMin;
    private Double poidsApresMax;

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

    public LocalDate getDatePeseMin() {
        return datePeseMin;
    }

    public void setDatePeseMin(LocalDate datePeseMin) {
        this.datePeseMin = datePeseMin;
    }

    public LocalDate getDatePeseMax() {
        return datePeseMax;
    }

    public void setDatePeseMax(LocalDate datePeseMax) {
        this.datePeseMax = datePeseMax;
    }

    public Double getPoidsApresMin() {
        return poidsApresMin;
    }

    public void setPoidsApresMin(Double poidsApresMin) {
        this.poidsApresMin = poidsApresMin;
    }

    public Double getPoidsApresMax() {
        return poidsApresMax;
    }

    public void setPoidsApresMax(Double poidsApresMax) {
        this.poidsApresMax = poidsApresMax;
    }

}