package mg.bovit.release.model;

import jakarta.persistence.*;

@Entity
@Table(name="vente_detail")
public class VenteDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name="id_vente")
    private VenteBovin venteBovin;

    @ManyToOne
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
