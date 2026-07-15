package mg.bovit.release.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import mg.bovit.release.model.User;
import mg.bovit.release.repository.UserRepository;
import mg.bovit.release.security.CustomUserDetails;

@Service
public class UtilisateurDetailsService implements UserDetailsService {

    private final UserRepository utilisateurRepository;

    public UtilisateurDetailsService(UserRepository utilisateurRepository) {
        this.utilisateurRepository = utilisateurRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        User user = utilisateurRepository.findByLogin(login)
            .orElseThrow(() -> new UsernameNotFoundException("Utilisateur introuvable : " + login));
        return new CustomUserDetails(user);
    }
}
