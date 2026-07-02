package mg.bovit.release.controller;

import mg.bovit.release.dto.EmployeeContratDTO;
import mg.bovit.release.service.EmployeeService;
import mg.bovit.release.repository.CaisseRepository;
import mg.bovit.release.repository.TypePayementEmployeeRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private CaisseRepository caisseRepository; // Injection de la caisse pour le formulaire

    @Autowired
    private TypePayementEmployeeRepository typePayementRepository;

    // Route pour afficher le formulaire de création d'un employé
    @GetMapping("/employee/new")
    public String showCreationForm(Model model) {
        model.addAttribute("employeeForm", new EmployeeContratDTO());
        return "employee/form";
    }

    @PostMapping("/employee/save")
    public String saveEmployee(@ModelAttribute("employeeForm") EmployeeContratDTO formDto) {
        employeeService.saveEmployeeWithContrat(formDto);
        return "redirect:/employee/new";
    }

    @GetMapping("/employee/paiement")
    public String afficherFormulairePaiement(
            @RequestParam(value = "employeeId", required = false) Long employeeId,
            @RequestParam(value = "mois", required = false) String mois,
            Model model) {
        // Envoie de la liste des employés
        model.addAttribute("employees", employeeService.findAllEmployees());

        // Envoie de la liste des caisses
        model.addAttribute("caisses", caisseRepository.findAll());
        model.addAttribute("typesPayement", typePayementRepository.findAll());

        // Pré-sélection (venant par ex. de la page des alertes "employé non payé")
        model.addAttribute("preselectEmployeeId", employeeId);
        model.addAttribute("preselectMois", mois);

        return "employee/paiement";
    }
}
