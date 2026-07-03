package mg.bovit.release.controller;

import mg.bovit.release.model.Contrat;
import mg.bovit.release.model.Employee;
import mg.bovit.release.repository.EmployeeRepository;
import mg.bovit.release.service.ContratService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class ContratController {

    @Autowired
    private ContratService contratService;

    @Autowired
    private EmployeeRepository employeeRepository;

    @GetMapping("/contrat/new")
    public String showContratForm(Model model) {
        List<Employee> employees = employeeRepository.findAll();
        
        model.addAttribute("contrat", new Contrat());
        model.addAttribute("employees", employees);
        
        return "contrat/form"; 
    }
    // Traiter la soumission du formulaire
    @PostMapping("/contrat/save")
    public String saveContrat(@ModelAttribute("contrat") Contrat contrat, @RequestParam("employeeId") Long employeeId) {
        contratService.saveContratWithEmployee(contrat, employeeId);
        return "redirect:/contrat/new"; // Ou rediriger vers une liste de contrats si elle existe
    }
}