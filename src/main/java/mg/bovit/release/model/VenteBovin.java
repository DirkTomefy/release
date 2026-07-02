package mg.bovit.release.model;

import jakarta.persistence.*;

import java.sql.Date;

@Entity
@Table(name="vente_bovin")
public class VenteBovin {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name="id_client")
    private Client client;

    @Column(
        name = "description"
    )
    private String description;

    @Column(
        name = "date_vente",
        nullable = false
    )
    private Date date_vente;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getDate_vente() {
        return date_vente;
    }

    public void setDate_vente(Date date_vente) {
        this.date_vente = date_vente;
    }
}
