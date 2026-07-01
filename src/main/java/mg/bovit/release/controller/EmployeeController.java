package mg.bovit.release.controller;

import mg.bovit.release.dto.EmployeeContratDTO;
import mg.bovit.release.service.EmployeeService;
import mg.bovit.release.repository.CaisseRepository; // Import indispensable
import mg.bovit.release.repository.TypePayementEmployeeRepository; // Import indispensable

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private CaisseRepository caisseRepository; // Injection de la caisse pour le formulaire

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

    @Autowired
    private TypePayementEmployeeRepository typePayementRepository; // Injecte le repo

    @GetMapping("/employee/paiement")
    public String afficherFormulairePaiement(Model model) {
        // Envoie de la liste des employés (s'assurer de l'attribut 'employees')
        model.addAttribute("employees", employeeService.findAllEmployees()); 
        
        // Envoie de la liste des caisses (s'assurer de l'attribut 'caisses')
        model.addAttribute("caisses", caisseRepository.findAll());
        model.addAttribute("typesPayement", typePayementRepository.findAll());
        // Retourne le nom de ton fichier sans l'extension (.html)
        return "employee/paiement"; 
    }
}