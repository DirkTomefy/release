package mg.bovit.release.controller;

import mg.bovit.release.dto.MultiCriteriaFormBovinList;
import mg.bovit.release.model.Race;
import mg.bovit.release.service.BovinService;
import mg.bovit.release.service.RaceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import mg.bovit.release.model.Bovin;
import java.util.List;

@Controller
@RequestMapping("/bovins")
public class BovinDirkController {

    @Autowired
    private BovinService bovinService;

    @Autowired
    private RaceService raceService;

    @GetMapping
    public String listBovins(@ModelAttribute("criteria") MultiCriteriaFormBovinList criteria,
                             Model model) throws Exception {
        // Si le DTO est null, on en crée un (cas première arrivée)
        if (criteria == null) {
            criteria = new MultiCriteriaFormBovinList();
        }

        // Récupération paginée
        Page<Bovin> bovinPage = bovinService.searchBovins(criteria);

        // Liste des races pour le filtre déroulant
        List<Race> races = raceService.findAll();

        // Ajout au modèle
        model.addAttribute("bovinPage", bovinPage);
        model.addAttribute("races", races);
        model.addAttribute("criteria", criteria);

        return "bovin/list";   // nom de la vue Thymeleaf
    }
}
