package mg.bovit.release.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "facture")
public class Facture {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "id_vente", nullable = false, unique = true)
    private VenteBovin vente;

    @Column(name = "numero_facture", length = 50, nullable = false)
    private String numeroFacture;

    @Column(name = "code_facture", length = 50, nullable = false, unique = true)
    private String codeFacture;

    @Column(name = "date_facture", nullable = false)
    private LocalDate dateFacture;

    @Column(name = "montant_total", nullable = false)
    private Double montantTotal;

    @OneToMany(mappedBy = "facture", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FactureDetail> details = new ArrayList<>();

    // Constructeurs, getters et setters
    public Facture() {}

    public Facture(VenteBovin vente, String numeroFacture, String codeFacture, LocalDate dateFacture, Double montantTotal) {
        this.vente = vente;
        this.numeroFacture = numeroFacture;
        this.codeFacture = codeFacture;
        this.dateFacture = dateFacture;
        this.montantTotal = montantTotal;
    }

    // Getters et setters (à générer)
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public VenteBovin getVente() { return vente; }
    public void setVente(VenteBovin vente) { this.vente = vente; }

    public String getNumeroFacture() { return numeroFacture; }
    public void setNumeroFacture(String numeroFacture) { this.numeroFacture = numeroFacture; }

    public String getCodeFacture() { return codeFacture; }
    public void setCodeFacture(String codeFacture) { this.codeFacture = codeFacture; }

    public LocalDate getDateFacture() { return dateFacture; }
    public void setDateFacture(LocalDate dateFacture) { this.dateFacture = dateFacture; }

    public Double getMontantTotal() { return montantTotal; }
    public void setMontantTotal(Double montantTotal) { this.montantTotal = montantTotal; }

    public List<FactureDetail> getDetails() { return details; }
    public void setDetails(List<FactureDetail> details) { this.details = details; }

    // Méthode utilitaire pour ajouter un détail
    public void addDetail(FactureDetail detail) {
        details.add(detail);
        detail.setFacture(this);
    }
}