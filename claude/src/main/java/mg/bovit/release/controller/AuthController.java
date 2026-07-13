package mg.bovit.release.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {

    @GetMapping({"/login", "/auth/login"})
    public String loginPage(@RequestParam(required = false) String error,
                            @RequestParam(required = false) String logout,
                            Model model) {
        if (error != null) {
            model.addAttribute("error", "Identifiants invalides");
        }
        if (logout != null) {
            model.addAttribute("message", "Vous avez été déconnecté");
        }
        return "auth/login";
    }
}