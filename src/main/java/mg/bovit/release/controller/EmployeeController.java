package mg.bovit.release.controller;

import mg.bovit.release.dto.EmployeeContratDTO;
import mg.bovit.release.service.EmployeeService;
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

    // Route pour afficher la vue contenant le formulaire
    @GetMapping("/employee/new")
    public String showCreationForm(Model model) {
        model.addAttribute("employeeForm", new EmployeeContratDTO());
        return "form"; // Correspond au nom de votre fichier d'affichage form.html sans l'extension
    }

    // Route de capture de soumission du formulaire POST
    @PostMapping("/employee/save")
    public String saveEmployee(@ModelAttribute("employeeForm") EmployeeContratDTO formDto) {
        employeeService.saveEmployeeWithContrat(formDto);
        // Redirection vers la liste après enregistrement réussi
        return "redirect:/employee/list#listEmployeePage";
    }
}