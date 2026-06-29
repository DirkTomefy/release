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
        if (criteria == null) {
            criteria = new MultiCriteriaFormBovinList();
        }
        // Validation de la taille
        if (criteria.getSize() <= 0) {
            criteria.setSize(10);
        }
        // Limite optionnelle pour éviter des charges excessives
        if (criteria.getSize() > 1000) {
            criteria.setSize(1000);
        }

        Page<Bovin> bovinPage = bovinService.searchBovins(criteria);
        List<Race> races = raceService.findAll();

        model.addAttribute("bovinPage", bovinPage);
        model.addAttribute("races", races);
        model.addAttribute("criteria", criteria);

        return "bovin/list";
    }
    
}
