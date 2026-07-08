package mg.bovit.release.service;

import mg.bovit.release.model.Role;
import mg.bovit.release.model.Utilisateur;
import mg.bovit.release.repository.RoleRepository;
import mg.bovit.release.repository.UtilisateurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Instant;

@Service
public class UtilisateurService {

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public Utilisateur createUtilisateur(String username, String password, String email, 
                                         String nom, String prenom, String roleNom) {
        Role role = roleRepository.findByNom(roleNom)
                .orElseThrow(() -> new RuntimeException("Rôle non trouvé: " + roleNom));

        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setUsername(username);
        utilisateur.setPassword(passwordEncoder.encode(password));
        utilisateur.setEmail(email);
        utilisateur.setNom(nom);
        utilisateur.setPrenom(prenom);
        utilisateur.setRole(role);
        utilisateur.setActif(true);
        utilisateur.setDateCreation(Timestamp.from(Instant.now()));

        return utilisateurRepository.save(utilisateur);
    }

    public boolean existsByUsername(String username) {
        return utilisateurRepository.existsByUsername(username);
    }
}