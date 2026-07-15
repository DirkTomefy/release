package mg.bovit.release.dto;

import java.util.List;

public class BuyBovinRequest {
    private String dateAchat;
    private Long raceId;
    private Integer quantite;
    private Double poidsAchat;
    private Double prixUnitaire; // Ajouté
    private List<CaissePaymentDTO> payments;

    // Getters et Setters
    public String getDateAchat() { return dateAchat; }
    public void setDateAchat(String dateAchat) { this.dateAchat = dateAchat; }
    
    public Long getRaceId() { return raceId; }
    public void setRaceId(Long raceId) { this.raceId = raceId; }
    
    public Integer getQuantite() { return quantite; }
    public void setQuantite(Integer quantite) { this.quantite = quantite; }
    
    public Double getPoidsAchat() { return poidsAchat; }
    public void setPoidsAchat(Double poidsAchat) { this.poidsAchat = poidsAchat; }
    
    public Double getPrixUnitaire() { return prixUnitaire; }
    public void setPrixUnitaire(Double prixUnitaire) { this.prixUnitaire = prixUnitaire; }
    
    public List<CaissePaymentDTO> getPayments() { return payments; }
    public void setPayments(List<CaissePaymentDTO> payments) { this.payments = payments; }

    public static class CaissePaymentDTO {
        private Long caisseId;
        private Double montant;

        public Long getCaisseId() { return caisseId; }
        public void setCaisseId(Long caisseId) { this.caisseId = caisseId; }
        
        public Double getMontant() { return montant; }
        public void setMontant(Double montant) { this.montant = montant; }
    }
}