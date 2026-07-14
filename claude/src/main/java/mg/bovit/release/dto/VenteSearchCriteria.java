package mg.bovit.release.dto;

import java.time.LocalDate;

public class VenteSearchCriteria {
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private Long clientId;
    private Long raceId;
    private Integer page = 0;
    private Integer size = 10;
    public LocalDate getDateDebut() {
        return dateDebut;
    }
    public void setDateDebut(LocalDate dateDebut) {
        this.dateDebut = dateDebut;
    }
    public LocalDate getDateFin() {
        return dateFin;
    }
    public void setDateFin(LocalDate dateFin) {
        this.dateFin = dateFin;
    }
    public Long getClientId() {
        return clientId;
    }
    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }
    public Long getRaceId() {
        return raceId;
    }
    public void setRaceId(Long raceId) {
        this.raceId = raceId;
    }
    public Integer getPage() {
        return page;
    }
    public void setPage(Integer page) {
        this.page = page;
    }
    public Integer getSize() {
        return size;
    }
    public void setSize(Integer size) {
        this.size = size;
    }

    // getters et setters
}