package mg.bovit.release.dto;

import java.sql.Date;
import java.time.LocalDate;

public class VenteListItem {
    private Long id;
    private LocalDate dateVente;
    private String clientNom;
    private String clientPrenom;
    private Double montantTotal;
    private Integer nombreBovins;
    private String codeFacture;
    private Long factureId;
    private boolean factureExistante;
    
    public VenteListItem() {
    }
    public VenteListItem(Long id, LocalDate dateVente, String clientNom, String clientPrenom, Double montantTotal,
            Integer nombreBovins, String codeFacture, Long factureId, boolean factureExistante) {
        this.id = id;
        this.dateVente = dateVente;
        this.clientNom = clientNom;
        this.clientPrenom = clientPrenom;
        this.montantTotal = montantTotal;
        this.nombreBovins = nombreBovins;
        this.codeFacture = codeFacture;
        this.factureId = factureId;
        this.factureExistante = factureExistante;
    }
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public LocalDate getDateVente() {
        return dateVente;
    }
    public void setDateVente(LocalDate dateVente) {
        this.dateVente = dateVente;
    }
    public String getClientNom() {
        return clientNom;
    }
    public void setClientNom(String clientNom) {
        this.clientNom = clientNom;
    }
    public String getClientPrenom() {
        return clientPrenom;
    }
    public void setClientPrenom(String clientPrenom) {
        this.clientPrenom = clientPrenom;
    }
    public Double getMontantTotal() {
        return montantTotal;
    }
    public void setMontantTotal(Double montantTotal) {
        this.montantTotal = montantTotal;
    }
    public Integer getNombreBovins() {
        return nombreBovins;
    }
    public void setNombreBovins(Integer nombreBovins) {
        this.nombreBovins = nombreBovins;
    }
    public String getCodeFacture() {
        return codeFacture;
    }
    public void setCodeFacture(String codeFacture) {
        this.codeFacture = codeFacture;
    }
    public Long getFactureId() {
        return factureId;
    }
    public void setFactureId(Long factureId) {
        this.factureId = factureId;
    }
    public boolean isFactureExistante() {
        return factureExistante;
    }
    public void setFactureExistante(boolean factureExistante) {
        this.factureExistante = factureExistante;
    }

    // constructeur, getters, setters
}