package mg.bovit.release.controller;

import mg.bovit.release.dto.BuyBovinRequest;
import mg.bovit.release.dto.MultiCriteriaFormBovinList;
import mg.bovit.release.model.Bovin;
import mg.bovit.release.model.Caisse;
import mg.bovit.release.model.Race;
import mg.bovit.release.model.sqlview.BovinWithPoids;
import mg.bovit.release.service.BovinService;
import mg.bovit.release.service.CaisseService;
import mg.bovit.release.service.RaceService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller; // ← remplacer RestController par Controller
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller // ← important
@RequestMapping("/bovins")
public class BovinController {

    @Autowired
    private BovinService bovinService;
    @Autowired
    private RaceService raceService;
    @Autowired
    private CaisseService caisseService;

    // Méthode pour la liste – retourne une vue
     @GetMapping
    public String listBovins(@ModelAttribute("criteria") MultiCriteriaFormBovinList criteria,
            Model model) throws Exception {
        if (criteria == null) {
            criteria = new MultiCriteriaFormBovinList();
        }
        if (criteria.getSize() <= 0) {
            criteria.setSize(10);
        }
        if (criteria.getSize() > 1000) {
            criteria.setSize(1000);
        }

        // Utilisation de la nouvelle méthode qui utilise la vue
        Page<BovinWithPoids> bovinPage = bovinService.searchBovinsWithPoids(criteria);
        List<Race> races = raceService.findAll();

        model.addAttribute("bovinPage", bovinPage);
        model.addAttribute("races", races);
        model.addAttribute("criteria", criteria);

        return "bovin/list";
    }

    // Méthode pour afficher le formulaire d'achat – retourne une vue
    @GetMapping("/achat/form")
    public String showBuyForm(Model model) throws Exception {
        List<Race> races = raceService.findAll();
        List<Caisse> caisses = caisseService.findAll();
        model.addAttribute("races", races);
        model.addAttribute("caisses", caisses);
        model.addAttribute("buyRequest", new BuyBovinRequest());
        return "bovin/form"; // Vue Thymeleaf
    }

    // Méthode pour l'achat – réponse JSON
    @PostMapping("/achat")
    @ResponseBody
    public ResponseEntity<?> buyBovin(@RequestBody BuyBovinRequest request) {
        try {
            Bovin bovin = new Bovin();
            bovin.setDate_achat(Date.valueOf(request.getDateAchat()));
            bovin.setPoids_achat(request.getPoidsAchat()); // <-- Ajout de la liaison ici

            Race race = new Race();
            race.setId(request.getRaceId());
            bovin.setRace(race);

            List<Caisse> caisses = new ArrayList<>();
            if (request.getPayments() != null) {
                for (BuyBovinRequest.CaissePaymentDTO pDto : request.getPayments()) {
                    Caisse caisse = new Caisse();
                    caisse.setId(pDto.getCaisseId());
                    caisse.setMontant_actuelle(pDto.getMontant());
                    caisses.add(caisse);
                }
            }

            bovinService.buyBovin(bovin, caisses, request.getQuantite());

            return ResponseEntity.ok(Map.of("status", "success", "message", "Achat enregistré avec succès !"));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("status", "error", "message", e.getMessage()));
        }
    }
}