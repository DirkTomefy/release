package mg.bovit.release.service;

import java.util.List;
import java.util.Set;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import mg.bovit.release.model.Role;
import mg.bovit.release.model.User;
import mg.bovit.release.repository.RoleRepository;
import mg.bovit.release.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
                        RoleRepository roleRepository,
                        PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public User createUser(String login, String rawPassword, List<Long> roleIds) {
        if (userRepository.findByLogin(login).isPresent()) {
            throw new IllegalArgumentException("Ce login existe déjà");
        }

        if (roleIds == null || roleIds.isEmpty()) {
            throw new IllegalArgumentException("Sélectionnez au moins un rôle");
        }

        Set<Role> roles = Set.copyOf(roleRepository.findAllById(roleIds));
        if (roles.isEmpty()) {
            throw new IllegalArgumentException("Rôle(s) invalide(s)");
        }

        User user = new User();
        user.setLogin(login);
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setRoles(roles);
        user.setActif(true);

        return userRepository.save(user);
    }
}