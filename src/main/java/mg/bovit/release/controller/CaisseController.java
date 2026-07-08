package mg.bovit.release.controller;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import mg.bovit.release.dto.CaisseStatDTO;
import mg.bovit.release.model.Caisse;
import mg.bovit.release.service.CaisseService;

@Controller
@RequestMapping("/caisse")
public class CaisseController {

    @Autowired
    private CaisseService caisseService;

    // Page des statistiques d'entrées / sorties de caisse (histogramme + filtres)
    @GetMapping("/stats")
    public String showStats(Model model) {
        List<Caisse> caisses = caisseService.findAll();

        LocalDate finDefaut = LocalDate.now();
        LocalDate debutDefaut = finDefaut.minusDays(29); // 30 derniers jours par défaut

        model.addAttribute("caisses", caisses);
        model.addAttribute("dateDebutDefaut", debutDefaut);
        model.addAttribute("dateFinDefaut", finDefaut);

        return "caisse/stats";
    }

    // Données JSON pour le graphique, rafraîchies en AJAX à chaque changement de filtre.
    // caisseId absent ou null => toutes les caisses confondues.
    @GetMapping("/stats/data")
    @ResponseBody
    public CaisseStatDTO getStatsData(
            @RequestParam("dateDebut") String dateDebutStr,
            @RequestParam("dateFin") String dateFinStr,
            @RequestParam(value = "caisseId", required = false) Long caisseId
    ) {
        try {
            Date dateDebut = Date.valueOf(dateDebutStr);
            Date dateFin = Date.valueOf(dateFinStr);
            return caisseService.getStatistiques(dateDebut, dateFin, caisseId);
        } catch (IllegalArgumentException e) {
            CaisseStatDTO erreur = new CaisseStatDTO();
            erreur.setErreur("Format de date invalide.");
            return erreur;
        } catch (Exception e) {
            CaisseStatDTO erreur = new CaisseStatDTO();
            erreur.setErreur(e.getMessage() != null ? e.getMessage() : "Erreur inattendue.");
            return erreur;
        }
    }
}
