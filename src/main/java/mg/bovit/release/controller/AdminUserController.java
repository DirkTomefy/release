package mg.bovit.release.controller;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import mg.bovit.release.repository.RoleRepository;
import mg.bovit.release.service.UserService;

@Controller
@RequestMapping("/admin/users")
@PreAuthorize("hasRole('ADMIN')")
public class AdminUserController {

    private final RoleRepository roleRepository;
    private final UserService userService;

    public AdminUserController(RoleRepository roleRepository, UserService userService) {
        this.roleRepository = roleRepository;
        this.userService = userService;
    }

    @GetMapping("/new")
    public String createUserForm(Model model) {
        if (!model.containsAttribute("roles")) {
            model.addAttribute("roles", roleRepository.findAll());
        }
        return "admin/user-create";
    }

    @PostMapping("/new")
    public String createUser(@RequestParam String login,
                              @RequestParam String password,
                              @RequestParam(required = false) List<Long> roleIds,
                              RedirectAttributes redirectAttributes,
                              Model model) {
        try {
            userService.createUser(login, password, roleIds);
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("roles", roleRepository.findAll());
            return "admin/user-create";
        }

        redirectAttributes.addFlashAttribute("success", "Utilisateur créé avec succès");
        return "redirect:/admin/users/new";
    }
}