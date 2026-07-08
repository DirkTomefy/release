package mg.bovit.release.controller;

import mg.bovit.release.dto.ControllerMessage;
import mg.bovit.release.dto.MultiCriteriaFormBovinList;
import mg.bovit.release.dto.VenteInsertDto;
import mg.bovit.release.dto.VenteStatsDTO;
import mg.bovit.release.model.Client;
import mg.bovit.release.model.Race;
import mg.bovit.release.model.sqlview.BovinWithPoids;
import mg.bovit.release.service.BovinService;
import mg.bovit.release.service.CaisseService;
import mg.bovit.release.service.ClientService;
import mg.bovit.release.service.RaceService;
import mg.bovit.release.service.VenteService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/vente")
public class VenteController {

    @Autowired
    private VenteService venteService;
    @Autowired
    private ClientService clientService;
    @Autowired
    private BovinService bovinService;
    @Autowired
    private RaceService raceService;

    @Autowired
    private CaisseService caisseService;

    // Page d'insertion d'une vente : liste des clients (dropdown + recherche)
    // et liste des bovins disponibles, filtrable via le multicritère déjà existant
    // (on réutilise MultiCriteriaFormBovinList / BovinService.searchBovinsWithPoids
    // sans toucher au code Bovin existant).
    @GetMapping("/new")
    public String showInsertForm(@ModelAttribute("criteria") MultiCriteriaFormBovinList criteria, Model model) {
        if (criteria == null) {
            criteria = new MultiCriteriaFormBovinList();
        }
        // Par défaut, on ne propose que les bovins pas encore vendus
        if (criteria.getStatut() == null || criteria.getStatut().isBlank()) {
            criteria.setStatut("non_vendu");
        }
        if (criteria.getSize() <= 0) {
            criteria.setSize(10);
        }
        if (criteria.getSize() > 1000) {
            criteria.setSize(1000);
        }

        Page<BovinWithPoids> bovinPage = bovinService.searchBovinsWithPoids(criteria);
        List<Client> clients = clientService.findAll();
        List<Race> races = raceService.findAll();

        model.addAttribute("bovinPage", bovinPage);
        model.addAttribute("clients", clients);
        model.addAttribute("races", races);
        model.addAttribute("criteria", criteria);
        model.addAttribute("venteForm", new VenteInsertDto());
        model.addAttribute("caisses", caisseService.findAll());

        return "vente/form";
    }

    @PostMapping("/save")
    @ResponseBody
    public ControllerMessage saveVente(@RequestBody VenteInsertDto venteInsertDto) {
        ControllerMessage response = new ControllerMessage();
        try {
            venteService.insertVente(venteInsertDto);
            response.setStatus("success");
            response.setMessage("Vente enregistrée avec succès.");
        } catch (Exception e) {
            response.setStatus("error");
            response.setMessage(e.getMessage());
        }
        return response;
    }

    @GetMapping("/stats/data")
    @ResponseBody
    public VenteStatsDTO getStatsData(@RequestParam(required = false) String dateDebut,
            @RequestParam(required = false) String dateFin,
            @RequestParam(required = false) Long raceId) {
        LocalDate debut = dateDebut != null && !dateDebut.isEmpty() ? LocalDate.parse(dateDebut) : null;
        LocalDate fin = dateFin != null && !dateFin.isEmpty() ? LocalDate.parse(dateFin) : null;
        return venteService.getVenteStats(debut, fin, raceId);
    }

    @GetMapping("/stats")
    public String statsPage(Model model) {
        model.addAttribute("races", raceService.findAll());
        return "vente/stats";
    }

}
