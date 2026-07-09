package mg.bovit.release.controller;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import mg.bovit.release.dto.BuyBovinRequest;
import mg.bovit.release.dto.MultiCriteriaFormBovinList;
import mg.bovit.release.model.Bovin;
import mg.bovit.release.model.Caisse;
import mg.bovit.release.model.Race;
import mg.bovit.release.model.sqlview.BovinWithPoids;
import mg.bovit.release.service.BovinService;
import mg.bovit.release.service.CaisseService;
import mg.bovit.release.service.MortaliteService;
import mg.bovit.release.service.RaceService;

@Controller // ← important
@RequestMapping("/bovins")
public class BovinController {

    @Autowired
    private BovinService bovinService;
    @Autowired
    private RaceService raceService;
    @Autowired
    private CaisseService caisseService;
    @Autowired
    private MortaliteService mortaliteService;

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

    @GetMapping("/achat/form")
    public String showBuyForm(Model model) throws Exception {
        List<Race> races = raceService.findAll();
        List<Caisse> caisses = caisseService.findAll();
        model.addAttribute("races", races);
        model.addAttribute("caisses", caisses);
        model.addAttribute("buyRequest", new BuyBovinRequest());
        return "bovin/form";
    }

    @PostMapping("/achat")
    @ResponseBody
    public ResponseEntity<?> buyBovin(@RequestBody BuyBovinRequest request) {
        try {
            Bovin bovin = new Bovin();
            bovin.setDate_achat(Date.valueOf(request.getDateAchat()));

        Race race = new Race();
        race.setId(request.getRaceId());
        bovin.setRace(race);

        // Récupérer le prix unitaire du formulaire
        Double prixUnitaire = request.getPrixUnitaire();
        if (prixUnitaire == null || prixUnitaire <= 0) {
            return ResponseEntity.badRequest().body(
                Map.of("status", "error", "message", "Le prix unitaire est obligatoire et doit être > 0")
            );
        }

        List<Caisse> caisses = new ArrayList<>();
        if (request.getPayments() != null) {
            for (BuyBovinRequest.CaissePaymentDTO pDto : request.getPayments()) {
                Caisse caisse = new Caisse();
                caisse.setId(pDto.getCaisseId());
                caisse.setMontant_actuelle(pDto.getMontant());
                caisses.add(caisse);
            }
        }

        // Passer le prix unitaire au service
        bovinService.buyBovin(bovin, caisses, request.getQuantite(), prixUnitaire);

        return ResponseEntity.ok(Map.of("status", "success", "message", "Achat enregistré avec succès !"));

    } catch (Exception e) {
        return ResponseEntity.badRequest().body(Map.of("status", "error", "message", e.getMessage()));
    }
}

    // Suppression d'un bovin : déclare automatiquement sa mortalité
    // (insertion dans la table mortalite) avant de le supprimer de bovin.
    @PostMapping("/delete/{id}")
    public String deleteBovin(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            mortaliteService.declareMortalite(id, LocalDate.now());
            redirectAttributes.addFlashAttribute("successMessage",
                    "Bovin #" + id + " supprimé et enregistré dans la mortalité.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Erreur lors de la suppression : " + e.getMessage());
        }
        return "redirect:/bovins";
    }
}