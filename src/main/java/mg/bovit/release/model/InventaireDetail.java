package mg.bovit.release.model;

import jakarta.persistence.*;

@Entity
@Table(name = "inventaire_detail")
public class InventaireDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_inventaire", nullable = false)
    private Inventaire inventaire;

    @ManyToOne
    @JoinColumn(name = "id_bovin", nullable = false)
    private Bovin bovin;

    @Column(nullable = false)
    private Integer quantite = 1;

    private String observations;

    // getters et setters
}