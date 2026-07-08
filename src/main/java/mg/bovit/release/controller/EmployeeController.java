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
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
    public String saveEmployee(@ModelAttribute EmployeeContratDTO dto, RedirectAttributes redirectAttributes, Model model) {
        try {
            employeeService.saveEmployeeWithContrat(dto);
            // Ajout du message de succès
            redirectAttributes.addFlashAttribute("successMessage", "L'employé " + dto.getNom() + " " + dto.getPrenom() + " a été enregistré avec succès !");
            return "redirect:/employee/list"; // Redirection vers la liste après succès
        } catch (ResponseStatusException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getReason());
            return "redirect:/employee/new"; // Redirige vers le formulaire en cas d'erreur
        }
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
    @GetMapping({"/employee", "/employee/list"})
    public String listEmployee(Model model) {
        model.addAttribute("employees", employeeService.findAllEmployees());
        return "employee/list";
    }
}
