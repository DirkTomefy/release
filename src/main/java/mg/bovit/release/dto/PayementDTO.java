package mg.bovit.release.dto;

import java.math.BigDecimal;

public class PayementDTO {
    private Long employeeId;
    private Long typePayementId; // Avance, Salaire, Sanction, etc.
    private String mois;         // Format "YYYY-MM" venant de <input type="month">
    private Long caisseId;
    private BigDecimal montant;

    // Getters et Setters
    public Long getEmployeeId() { 
        return employeeId; 
    }
    public void setEmployeeId(Long employeeId) { 
        this.employeeId = employeeId; 
    }

    public Long getTypePayementId() { 
        return typePayementId; 
    }
    public void setTypePayementId(Long typePayementId) { 
        this.typePayementId = typePayementId; 
    }

    public String getMois() { 
        return mois; 
    }
    public void setMois(String mois) { 
        this.mois = mois; 
    }

    public Long getCaisseId() { 
        return caisseId; 
    }
    public void setCaisseId(Long caisseId) { 
        this.caisseId = caisseId; 
    }

    public BigDecimal getMontant() { 
        return montant; 
    }
    public void setMontant(BigDecimal montant) { 
        this.montant = montant; 
    }
}