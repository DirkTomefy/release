package mg.bovit.release.dto;

import java.sql.Date;

import mg.bovit.release.model.Materiel;

public class MaterielStockDateDto extends MaterielStockDto {
    
    private Date date;

    public MaterielStockDateDto(Materiel materiel, Double quantiteRestant,Date date) {
        super(materiel,quantiteRestant);
        this.date = date;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
