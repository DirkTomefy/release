package mg.bovit.release.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import mg.bovit.release.dto.ControllerMessage;
import mg.bovit.release.dto.MortaliteCriteria;
import mg.bovit.release.dto.MortaliteInsertDto;
import mg.bovit.release.dto.MortaliteStatsDTO;
import mg.bovit.release.dto.MultiCriteriaFormBovinList;
import mg.bovit.release.model.Mortalite;
import mg.bovit.release.model.Race;
import mg.bovit.release.model.sqlview.BovinWithPoids;
import mg.bovit.release.service.BovinService;
import mg.bovit.release.service.MortaliteService;
import mg.bovit.release.service.RaceService;

@Controller
@RequestMapping("/mortalite")
public class MortaliteController {

    @Autowired
    private MortaliteService mortaliteService;

    @Autowired
    private BovinService bovinService;

    @Autowired
    private RaceService raceService;

    // ==================== Liste des mortalités ====================
    @GetMapping
    public String listMortalites(@ModelAttribute("criteria") MortaliteCriteria criteria, Model model) {
        if (criteria == null) {
            criteria = new MortaliteCriteria();
        }
        if (criteria.getPage() == null) {
            criteria.setPage(0);
        }
        if (criteria.getSize() == null || criteria.getSize() <= 0) {
            criteria.setSize(10);
        }

        Page<Mortalite> mortalitePage = mortaliteService.findPaginated(criteria);
        List<Race> races = raceService.findAll();

        model.addAttribute("mortalitePage", mortalitePage);
        model.addAttribute("races", races);
        model.addAttribute("criteria", criteria);
        return "mortalite/list";
    }

    // ==================== Page d'insertion multiple ====================
    // L'utilisateur saisit une date et une liste d'identifiants de bovins
    // (input id_bovin). Une liste des bovins encore vivants est affichée
    // à titre indicatif pour l'aider à retrouver les bons identifiants.
    @GetMapping("/new")
    public String showInsertForm(Model model) {
        MultiCriteriaFormBovinList criteria = new MultiCriteriaFormBovinList();
        criteria.setStatut("non_vendu");
        criteria.setSize(1000);

        Page<BovinWithPoids> bovinPage = bovinService.searchBovinsWithPoids(criteria);

        model.addAttribute("bovinPage", bovinPage);
        return "mortalite/form";
    }

    @PostMapping("/save")
    @ResponseBody
    public ControllerMessage saveMortalite(@RequestBody MortaliteInsertDto dto) {
        ControllerMessage response = new ControllerMessage();
        try {
            LocalDate date = LocalDate.now();
            mortaliteService.declareMortaliteMultiple(dto.getBovinIds(), date);
            response.setStatus("success");
            response.setMessage("Mortalité enregistrée avec succès.");
        } catch (Exception e) {
            response.setStatus("error");
            response.setMessage(e.getMessage());
        }
        return response;
    }

    // ==================== Statistiques ====================
    @GetMapping("/stats")
    public String statsPage(Model model) {
        model.addAttribute("races", raceService.findAll());
        return "mortalite/stats";
    }

    @GetMapping("/stats/data")
    @ResponseBody
    public MortaliteStatsDTO getStatsData(@RequestParam(required = false) String dateDebut,
                                          @RequestParam(required = false) String dateFin,
                                          @RequestParam(required = false) Long raceId) {
        LocalDate debut = dateDebut != null && !dateDebut.isEmpty() ? LocalDate.parse(dateDebut) : null;
        LocalDate fin = dateFin != null && !dateFin.isEmpty() ? LocalDate.parse(dateFin) : null;
        return mortaliteService.getStats(debut, fin, raceId);
    }
}
