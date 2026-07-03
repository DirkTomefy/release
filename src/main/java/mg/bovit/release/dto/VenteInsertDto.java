package mg.bovit.release.dto;

import java.sql.Date;
import java.util.List;

import mg.bovit.release.dto.BuyBovinRequest.CaissePaymentDTO;

public class VenteInsertDto {

    private Long clientId;
    private String description;
    private Date dateVente;
    private List<LigneVenteDto> lignes;
    private List<CaissePaymentDTO> payments;

    public Long getClientId() { return clientId; }
    public void setClientId(Long clientId) { this.clientId = clientId; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Date getDateVente() { return dateVente; }
    public void setDateVente(Date dateVente) { this.dateVente = dateVente; }

    public List<LigneVenteDto> getLignes() { return lignes; }
    public void setLignes(List<LigneVenteDto> lignes) { this.lignes = lignes; }
    public List<CaissePaymentDTO> getPayments() { return payments; }
    public void setPayments(List<CaissePaymentDTO> payments) { this.payments = payments; }
    // Une ligne = un bovin vendu dans cette vente, avec son prix de vente
    public static class LigneVenteDto {
        private Long bovinId;
        private Double prixVente;

        public Long getBovinId() { return bovinId; }
        public void setBovinId(Long bovinId) { this.bovinId = bovinId; }

        public Double getPrixVente() { return prixVente; }
        public void setPrixVente(Double prixVente) { this.prixVente = prixVente; }
    }
}
