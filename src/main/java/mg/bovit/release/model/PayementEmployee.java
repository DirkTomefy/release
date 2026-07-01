package mg.bovit.release.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
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

    public BigDecimal getRestePaye() {
        return restePaye;
    }

    public void setRestePaye(BigDecimal restePaye) {
        this.restePaye = restePaye;
    }
}