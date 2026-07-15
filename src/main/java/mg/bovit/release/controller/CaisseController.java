package mg.bovit.release.controller;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import mg.bovit.release.dto.CaisseStatDTO;
import mg.bovit.release.dto.MvtCaisseCriteria;
import mg.bovit.release.model.Caisse;
import mg.bovit.release.model.CauseCaisse;
import mg.bovit.release.model.MvtCaisse;
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
        List<CauseCaisse> causes = caisseService.findAllCauses();

        LocalDate finDefaut = LocalDate.now();
        LocalDate debutDefaut = finDefaut.minusDays(29); // 30 derniers jours par défaut

        model.addAttribute("caisses", caisses);
        model.addAttribute("causes", causes);
        model.addAttribute("dateDebutDefaut", debutDefaut);
        model.addAttribute("dateFinDefaut", finDefaut);

        return "caisse/stats";
    }

    // Données JSON pour le graphique, rafraîchies en AJAX à chaque changement de filtre.
    // caisseId/causeId absents ou null => toutes les caisses / toutes les causes confondues.
    @GetMapping("/stats/data")
    @ResponseBody
    public CaisseStatDTO getStatsData(
            @RequestParam("dateDebut") String dateDebutStr,
            @RequestParam("dateFin") String dateFinStr,
            @RequestParam(value = "caisseId", required = false) Long caisseId,
            @RequestParam(value = "causeId", required = false) Long causeId
    ) {
        try {
            Date dateDebut = Date.valueOf(dateDebutStr);
            Date dateFin = Date.valueOf(dateFinStr);
            return caisseService.getStatistiques(dateDebut, dateFin, caisseId, causeId);
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

    // Historique / traçabilité : liste paginée et filtrable de tous les
    // mouvements de caisse (avec leur cause), du plus récent au plus ancien.
    @GetMapping("/historique")
    public String showHistorique(@ModelAttribute("criteria") MvtCaisseCriteria criteria, Model model) {
        if (criteria == null) {
            criteria = new MvtCaisseCriteria();
        }
        if (criteria.getPage() == null) {
            criteria.setPage(0);
        }
        if (criteria.getSize() == null || criteria.getSize() <= 0) {
            criteria.setSize(20);
        }

        Page<MvtCaisse> mouvementPage = caisseService.findMouvementsPagines(criteria);

        model.addAttribute("mouvementPage", mouvementPage);
        model.addAttribute("caisses", caisseService.findAll());
        model.addAttribute("causes", caisseService.findAllCauses());
        model.addAttribute("criteria", criteria);

        return "caisse/historique";
    }
}
