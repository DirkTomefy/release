package mg.bovit.release.dto;

import java.sql.Date;

public class PeseBovinRequest {
    private Long bovinId;
    private Date datePese;
    private Double poids;
    private Long idPeseBovin;

    public Long getIdPeseBovin() {
        return idPeseBovin;
    }
    public void setIdPeseBovin(Long idPeseBovin) {
        this.idPeseBovin = idPeseBovin;
    }
    public Long getBovinId() {
        return bovinId;
    }
    public void setBovinId(Long bovinId) {
        this.bovinId = bovinId;
    }
    public Date getDatePese() {
        return datePese;
    }
    public void setDatePese(Date datePese) {
        this.datePese = datePese;
    }
    public Double getPoids() {
        return poids;
    }
    public void setPoids(Double poids) {
        this.poids = poids;
    }
}
