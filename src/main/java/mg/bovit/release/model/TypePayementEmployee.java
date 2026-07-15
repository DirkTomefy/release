package mg.bovit.release.model;

import jakarta.persistence.*;

@Entity
@Table(name = "type_payement_employee")
public class TypePayementEmployee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(
        name = "libelle",
        nullable = false
    )
    private String libelle;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLibelle() {
        return libelle;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }
}