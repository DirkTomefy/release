package mg.bovit.release.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import mg.bovit.release.dto.ControllerMessage;
import mg.bovit.release.dto.MulticriteriaListPeseBovin;
import mg.bovit.release.dto.PeseBovinRequest;
import mg.bovit.release.model.Bovin;
import mg.bovit.release.model.PeseBovin;
import mg.bovit.release.model.Race;
import mg.bovit.release.model.sqlview.PeseBovinWithDateVente;
import mg.bovit.release.service.BovinService;
import mg.bovit.release.service.PeseBovinService;
import mg.bovit.release.service.RaceService;

@Controller
@RequestMapping("/peseBovin")
public class PeseBovinController {

    @Autowired
    private PeseBovinService peseBovinService;

    @Autowired
    private RaceService raceService;

    @Autowired
    private BovinService bovinService;

    // ----- Méthodes GET inchangées -----

    @GetMapping("/list")
    public String listPeseBovin(@ModelAttribute("criteria") MulticriteriaListPeseBovin criteria, Model model) {
        if (criteria == null) {
            criteria = new MulticriteriaListPeseBovin();
        }
        if (criteria.getSize() <= 0) {
            criteria.setSize(10);
        }

        Page<PeseBovinWithDateVente> pesePage = peseBovinService.searchPeseBovins(criteria);
        List<Race> races = raceService.findAll();

        model.addAttribute("pesePage", pesePage);
        model.addAttribute("pesesBovin", pesePage.getContent());
        model.addAttribute("races", races);
        model.addAttribute("criteria", criteria);

        return "peseBovin/list";
    }

    @GetMapping({"/form", "/form/{id}"})
    public String formPeseBovin(
            @PathVariable(name = "id", required = false) Long id,
            Model model) {
        List<Bovin> bovins = bovinService.findAll();
        if (id != null) {
            PeseBovin peseBovin = peseBovinService.findById(id);
            if (peseBovin != null) {
                model.addAttribute("peseBovin", peseBovin);
            }
        }
        model.addAttribute("bovins", bovins);
        return "peseBovin/form";
    }

    @GetMapping("/detail/{id}")
    public String showPeseDetail(
            @PathVariable("id") Long bovinId,
            @RequestParam(required = false) String dateDebut,
            @RequestParam(required = false) String dateFin,
            Model model) throws Exception {

        Bovin bovin = bovinService.findById(bovinId);
        List<PeseBovin> pesees = peseBovinService.findByBovinIdOrderByDatePeseAsc(bovinId);

        if (dateDebut != null && !dateDebut.isEmpty()) {
            java.sql.Date debut = java.sql.Date.valueOf(dateDebut);
            pesees = pesees.stream()
                    .filter(p -> !p.getDate_pese().before(debut))
                    .collect(java.util.stream.Collectors.toList());
        }
        if (dateFin != null && !dateFin.isEmpty()) {
            java.sql.Date fin = java.sql.Date.valueOf(dateFin);
            pesees = pesees.stream()
                    .filter(p -> !p.getDate_pese().after(fin))
                    .collect(java.util.stream.Collectors.toList());
        }

        double poidsActuel = 0;
        double gainTotal = 0;
        double gainMoyenJour = 0;
        int nbPesees = pesees.size();

        if (!pesees.isEmpty()) {
            poidsActuel = pesees.get(pesees.size() - 1).getPoids_apres();
            double poidsInitial = pesees.get(0).getPoids_apres();
            gainTotal = poidsActuel - poidsInitial;

            java.sql.Date firstDate = pesees.get(0).getDate_pese();
            java.sql.Date lastDate = pesees.get(pesees.size() - 1).getDate_pese();
            long diffInMillis = lastDate.getTime() - firstDate.getTime();
            long diffInDays = diffInMillis / (1000 * 60 * 60 * 24);
            if (diffInDays > 0) {
                gainMoyenJour = gainTotal / diffInDays;
            }
        }

        java.sql.Date dateDebutDefault = null;
        java.sql.Date dateFinDefault = null;
        if (!pesees.isEmpty()) {
            dateDebutDefault = pesees.get(0).getDate_pese();
            dateFinDefault = pesees.get(pesees.size() - 1).getDate_pese();
        } else {
            dateDebutDefault = new java.sql.Date(System.currentTimeMillis());
            dateFinDefault = new java.sql.Date(System.currentTimeMillis());
        }

        model.addAttribute("bovinId", bovinId);
        model.addAttribute("raceNom", bovin.getRace().getNom());
        model.addAttribute("dateAchat", bovin.getDate_achat());
        model.addAttribute("poidsInitial", bovin.getPoids_achat());
        model.addAttribute("poidsActuel", poidsActuel > 0 ? poidsActuel : null);
        model.addAttribute("gainTotal", gainTotal);
        model.addAttribute("gainMoyenJour", gainMoyenJour);
        model.addAttribute("nbPesees", nbPesees);
        model.addAttribute("pesees", pesees);
        model.addAttribute("dateDebut", dateDebut != null ? java.sql.Date.valueOf(dateDebut) : dateDebutDefault);
        model.addAttribute("dateFin", dateFin != null ? java.sql.Date.valueOf(dateFin) : dateFinDefault);

        return "peseBovin/detail";
    }

    // ----- Méthode POST simplifiée -----
    @PostMapping("/create")
    @ResponseBody
    public ControllerMessage createPeseBovin(@RequestBody PeseBovinRequest peseBovinRequest) {
        ControllerMessage response = new ControllerMessage();
        try {
            // Délégation de toute la logique métier au service
            peseBovinService.createOrUpdatePeseBovin(peseBovinRequest);
            response.setStatus("success");
            response.setMessage("Pesée effectuée avec succès.");
        } catch (Exception e) {
            response.setStatus("error");
            response.setMessage(e.getMessage());
        }
        return response;
    }
}