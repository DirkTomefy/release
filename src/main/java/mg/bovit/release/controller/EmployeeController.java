package mg.bovit.release.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import mg.bovit.release.dto.EmployeeContratDTO;
import mg.bovit.release.repository.CaisseRepository;
import mg.bovit.release.repository.TypePayementEmployeeRepository;
import mg.bovit.release.service.EmployeeService;
import mg.bovit.release.service.PayementEmployeeService;

@Controller
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private CaisseRepository caisseRepository;

    @Autowired
    private TypePayementEmployeeRepository typePayementRepository;

    @Autowired
    private PayementEmployeeService payementEmployeeService;

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
            redirectAttributes.addFlashAttribute("successMessage", "L'employé " + dto.getNom() + " " + dto.getPrenom() + " a été enregistré avec succès !");
            return "redirect:/employee/list";
        } catch (ResponseStatusException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getReason());
            return "redirect:/employee/new";
        }
    }

    // Route pour afficher le formulaire de paiement
    @GetMapping("/employee/paiement")
    public String afficherFormulairePaiement(
            @RequestParam(value = "employeeId", required = false) Long employeeId,
            @RequestParam(value = "mois", required = false) String mois,
            Model model) {
        model.addAttribute("employees", employeeService.findAllEmployees());
        model.addAttribute("caisses", caisseRepository.findAll());
        model.addAttribute("typesPayement", typePayementRepository.findAll());
        model.addAttribute("preselectEmployeeId", employeeId);
        model.addAttribute("preselectMois", mois);

        return "employee/paiement";
    }

    // 🌟 NOUVELLE ROUTE : Affiche la page des employés non payés
    @GetMapping("/employee/alerte")
    public String afficherAlertesPaiement(Model model) {
        model.addAttribute("alertes", payementEmployeeService.getAlertesNonPayes());
        return "employee/non_payee";
    }

    @GetMapping({"/employee", "/employee/list"})
    public String listEmployee(Model model) {
        model.addAttribute("employees", employeeService.findAllEmployees());
        return "employee/list";
    }

    @GetMapping("/employee/contrat/{id}")
    public String listContratsByEmployee(@PathVariable("id") Long employeeId, Model model) {
        try {
            model.addAttribute("contrats", employeeService.getContratsByEmployeeId(employeeId));
            model.addAttribute("id", employeeId);
            return "employee/contrat";
        } catch (ResponseStatusException e) {
            model.addAttribute("errorMessage", e.getReason());
            return "error";
        }
    }
}