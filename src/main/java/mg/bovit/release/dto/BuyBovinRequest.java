package mg.bovit.release.dto;

import java.util.List;

public class BuyBovinRequest {
    private Long raceId;
    private String dateAchat;
    private Integer quantite;
    private Double poidsAchat;
    private List<CaissePaymentDTO> payments;

    // Getters et Setters
    public Long getRaceId() { return raceId; }
    public void setRaceId(Long raceId) { this.raceId = raceId; }

    public String getDateAchat() { return dateAchat; }
    public void setDateAchat(String dateAchat) { this.dateAchat = dateAchat; }

    public Integer getQuantite() { return quantite; }
    public void setQuantite(Integer quantite) { this.quantite = quantite; }

    public List<CaissePaymentDTO> getPayments() { return payments; }
    public void setPayments(List<CaissePaymentDTO> payments) { this.payments = payments; }

    public Double getPoidsAchat() { return poidsAchat; }
    public void setPoidsAchat(Double poidsAchat) { this.poidsAchat = poidsAchat; }
    
    public static class CaissePaymentDTO {
        private Long caisseId; // ou le nom/type converti en ID de caisse
        private Double montant;

        // Getters et Setters
        public Long getCaisseId() { return caisseId; }
        public void setCaisseId(Long caisseId) { this.caisseId = caisseId; }

        public Double getMontant() { return montant; }
        public void setMontant(Double montant) { this.montant = montant; }
    }
}
