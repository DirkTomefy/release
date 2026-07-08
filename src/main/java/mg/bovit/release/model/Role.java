package mg.bovit.release.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "role")
public class Role {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "nom", nullable = false, unique = true, length = 50)
    private String nom;
    
    @Column(name = "description")
    private String description;
    
    @OneToMany(mappedBy = "role")
    private List<Utilisateur> utilisateurs;
    
    // Constructeurs
    public Role() {}
    
    public Role(String nom) {
        this.nom = nom;
    }
    
    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public List<Utilisateur> getUtilisateurs() { return utilisateurs; }
    public void setUtilisateurs(List<Utilisateur> utilisateurs) { this.utilisateurs = utilisateurs; }
}