package mg.bovit.release.model;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name="vente_detail", uniqueConstraints = @UniqueConstraint(columnNames = "id_bovin"))
public class VenteDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="id_vente")
    private VenteBovin venteBovin;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="id_bovin")
    private Bovin bovin;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public VenteBovin getVenteBovin() {
        return venteBovin;
    }

    public void setVenteBovin(VenteBovin venteBovin) {
        this.venteBovin = venteBovin;
    }

    public Bovin getBovin() {
        return bovin;
    }

    public void setBovin(Bovin bovin) {
        this.bovin = bovin;
    }
}
