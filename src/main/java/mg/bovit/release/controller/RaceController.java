package mg.bovit.release.controller;

import mg.bovit.release.dto.RaceCriteria;
import mg.bovit.release.model.Race;
import mg.bovit.release.service.RaceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/races")
public class RaceController {

    @Autowired
    private RaceService raceService;

    @GetMapping
    public String listRaces(@ModelAttribute("criteria") RaceCriteria criteria, Model model) {
        if (criteria == null) {
            criteria = new RaceCriteria();
        }
        if (criteria.getPage() == null) criteria.setPage(0);
        if (criteria.getSize() == null) criteria.setSize(10);

        Page<Race> racePage = raceService.findPaginated(criteria);
        model.addAttribute("racePage", racePage);
        model.addAttribute("criteria", criteria);
        return "race/list";
    }

    @GetMapping("/form")
    public String showFormForAdd(Model model) {
        model.addAttribute("race", new Race());
        return "race/form";
    }

    @GetMapping("/form/{id}")
    public String showFormForUpdate(@PathVariable Long id, Model model) {
        try {
            Race race = raceService.findById(id);
            model.addAttribute("race", race);
            return "race/form";
        } catch (Exception e) {
            return "redirect:/races?error=Race non trouvée";
        }
    }

    @PostMapping("/save")
    public String saveRace(@ModelAttribute("race") Race race, RedirectAttributes redirectAttributes) {
        try {
            raceService.save(race);
            redirectAttributes.addFlashAttribute("successMessage", "Race enregistrée avec succès.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erreur lors de l'enregistrement : " + e.getMessage());
        }
        return "redirect:/races";
    }

    @PostMapping("/delete/{id}")
    public String deleteRace(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            raceService.deleteById(id);
            redirectAttributes.addFlashAttribute("successMessage", "Race supprimée avec succès.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erreur lors de la suppression : " + e.getMessage());
        }
        return "redirect:/races";
    }
}