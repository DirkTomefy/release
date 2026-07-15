package mg.bovit.release.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;

@Entity
@Table(name = "payement_employee")
public class PayementEmployee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_employee", nullable = false)
    private Employee employee;

    @ManyToOne
    @JoinColumn(name = "id_type_payement_employee", nullable = false)
    private TypePayementEmployee typePayementEmployee;

    @Column(
        name = "date_payement",
        nullable = false
    )
    private Timestamp datePayement;

    /**
     * Mois concerne par le paiement (toujours stocke au 1er du mois),
     * PAS la date reelle a laquelle le paiement a ete effectue.
     * Permet de payer un mois en retard sans fausser la verification
     * "mois deja paye ?" et les alertes de non-paiement.
     */
    @Column(
        name = "mois",
        nullable = false
    )
    private Date mois;

    /**
     * Montant reellement verse lors de cette transaction
     * (somme repartie sur les differentes caisses utilisees).
     */
    @Column(
        name = "montant",
        nullable = false,
        precision = 12,
        scale = 2
    )
    private BigDecimal montant;

    @Column(
        name = "reste_paye",
        nullable = false,
        precision = 12,
        scale = 2
    )
    private BigDecimal restePaye;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public TypePayementEmployee getTypePayementEmployee() {
        return typePayementEmployee;
    }

    public void setTypePayementEmployee(TypePayementEmployee typePayementEmployee) {
        this.typePayementEmployee = typePayementEmployee;
    }

    public Timestamp getDatePayement() {
        return datePayement;
    }

    public void setDatePayement(Timestamp datePayement) {
        this.datePayement = datePayement;
    }

    public Date getMois() {
        return mois;
    }

    public void setMois(Date mois) {
        this.mois = mois;
    }

    public BigDecimal getMontant() {
        return montant;
    }

    public void setMontant(BigDecimal montant) {
        this.montant = montant;
    }

    public BigDecimal getRestePaye() {
        return restePaye;
    }

    public void setRestePaye(BigDecimal restePaye) {
        this.restePaye = restePaye;
    }
}