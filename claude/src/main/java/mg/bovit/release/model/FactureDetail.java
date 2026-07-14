package mg.bovit.release.model;

import jakarta.persistence.*;

@Entity
@Table(name = "facture_detail")
public class FactureDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_facture", nullable = false)
    private Facture facture;

    @OneToOne
    @JoinColumn(name = "id_vente_detail", nullable = false, unique = true)
    private VenteDetail venteDetail;

    @Column(name = "prix_unitaire", nullable = false)
    private Double prixUnitaire;

    @Column(name = "quantite", nullable = false)
    private Integer quantite = 1;

    // Constructeurs, getters et setters
    public FactureDetail() {}

    public FactureDetail(Facture facture, VenteDetail venteDetail, Double prixUnitaire, Integer quantite) {
        this.facture = facture;
        this.venteDetail = venteDetail;
        this.prixUnitaire = prixUnitaire;
        this.quantite = quantite;
    }

    // Getters et setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Facture getFacture() { return facture; }
    public void setFacture(Facture facture) { this.facture = facture; }

    public VenteDetail getVenteDetail() { return venteDetail; }
    public void setVenteDetail(VenteDetail venteDetail) { this.venteDetail = venteDetail; }

    public Double getPrixUnitaire() { return prixUnitaire; }
    public void setPrixUnitaire(Double prixUnitaire) { this.prixUnitaire = prixUnitaire; }

    public Integer getQuantite() { return quantite; }
    public void setQuantite(Integer quantite) { this.quantite = quantite; }
}