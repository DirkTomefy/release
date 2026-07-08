package mg.bovit.release.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {

    @GetMapping("/login")
    public String loginPage(
            @RequestParam(value = "error", required = false) String error,
            @RequestParam(value = "logout", required = false) String logout,
            Model model) {
        
        if (error != null) {
            model.addAttribute("error", "Nom d'utilisateur ou mot de passe incorrect");
        }
        
        if (logout != null) {
            model.addAttribute("message", "Vous avez été déconnecté avec succès");
        }
        
        return "login";
    }

    @GetMapping("/")
    public String home() {
        return "redirect:/bovins";  
    }
}