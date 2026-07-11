package mg.bovit.release.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import mg.bovit.release.model.Role;
import mg.bovit.release.model.User;
import mg.bovit.release.repository.RoleRepository;
import mg.bovit.release.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

@Controller
public class AuthController {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping({"/login", "/auth/login"})
    public String loginPage(@RequestParam(required = false) String error,
                            @RequestParam(required = false) String registered,
                            @RequestParam(required = false) String logout,
                            Model model) {
        if (error != null) {
            model.addAttribute("error", "Identifiants invalides");
        }
        if (registered != null) {
            model.addAttribute("message", "Compte créé avec succès, vous pouvez vous connecter");
        }
        if (logout != null) {
            model.addAttribute("message", "Vous avez été déconnecté");
        }
        return "auth/login";
    }

    @GetMapping({"/register", "/auth/register"})
    public String registerPage() {
        return "auth/register";
    }

    @PostMapping({"/register", "/auth/register"})
    public String register(@RequestParam String login,
                            @RequestParam String password,
                            Model model) {

        if (userRepository.findByLogin(login).isPresent()) {
            model.addAttribute("error", "Ce login existe déjà");
            return "auth/register";
        }

        Role roleEmploye = roleRepository.findByLibelle("EMPLOYE")
            .orElseThrow(() -> new RuntimeException("Rôle EMPLOYE introuvable, as-tu bien seedé la table role ?"));

        User user = new User();
        user.setLogin(login);
        user.setPassword(passwordEncoder.encode(password));
        user.setRoles(Set.of(roleEmploye)); // rôle par défaut à l'inscription
        user.setActif(true);

        userRepository.save(user);

        return "redirect:/auth/login?registered=true";
    }
}