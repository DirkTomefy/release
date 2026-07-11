package mg.bovit.release.dto;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

public class MultiCriteriaEtatStockMateriel {
    
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateDebut;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateFin;
    
    private Integer idTypeMateriel;
    private Integer idMateriel;


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
    public Integer getIdTypeMateriel() {
        return idTypeMateriel;
    }
    public void setIdTypeMateriel(Integer idTypeMateriel) {
        this.idTypeMateriel = idTypeMateriel;
    }
    public Integer getIdMateriel() {
        return idMateriel;
    }
    public void setIdMateriel(Integer idMateriel) {
        this.idMateriel = idMateriel;
    }
}
