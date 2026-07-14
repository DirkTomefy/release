package mg.bovit.release.dto;

import java.math.BigDecimal;
import java.util.List;

public class PayementDTO {
    private Long employeeId;
    private Long typePayementId; // Avance, Salaire, Sanction, etc.
    private String mois;         // Format "YYYY-MM" venant de <input type="month">
    private BigDecimal montant;  // Montant total à verser (doit être égal à la somme des lignes "payments")
    private List<CaissePaymentDTO> payments; // Répartition du montant sur une ou plusieurs caisses

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

    public BigDecimal getMontant() {
        return montant;
    }
    public void setMontant(BigDecimal montant) {
        this.montant = montant;
    }

    public List<CaissePaymentDTO> getPayments() {
        return payments;
    }
    public void setPayments(List<CaissePaymentDTO> payments) {
        this.payments = payments;
    }

    public static class CaissePaymentDTO {
        private Long caisseId;
        private BigDecimal montant;

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
}
