package mg.bovit.release.model;

import jakarta.persistence.*;
import java.sql.Date;

@Entity
@Table(name = "mvt_caisse")
public class MvtCaisse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(
        name = "date",
        nullable = false
    )
    private Date date;

    @Column(
        name = "montant",
        nullable = false
    )
    private Double montant;

    @ManyToOne
    @JoinColumn(
        name = "id_caisse", 
        nullable = false
    )
    private Caisse caisse;

    @ManyToOne
    @JoinColumn(
        name = "id_cause_caisse",
        nullable = false
    )
    private CauseCaisse causeCaisse;

    // Getters et Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Double getMontant() {
        return montant;
    }

    public void setMontant(Double montant) {
        this.montant = montant;
    }

    public Caisse getCaisse() {
        return caisse;
    }

    public void setCaisse(Caisse caisse) {
        this.caisse = caisse;
    }

    public CauseCaisse getCauseCaisse() {
        return causeCaisse;
    }

    public void setCauseCaisse(CauseCaisse causeCaisse) {
        this.causeCaisse = causeCaisse;
    }
}