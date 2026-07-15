package mg.bovit.release.controller;

import mg.bovit.release.service.PayementEmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AlerteController {

    @Autowired
    private PayementEmployeeService payementEmployeeService;

    @GetMapping("/employee/alertes")
    public String listNonPayes(Model model) {
        model.addAttribute("alertes", payementEmployeeService.getAlertesNonPayes());
        return "employee/non_payee"; // Renvoie vers ton non_payee.html
    }
}