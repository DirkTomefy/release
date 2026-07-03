package mg.bovit.release.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import mg.bovit.release.dto.MouvementEntreePayload;
import mg.bovit.release.dto.MouvementStockSortiePayload;
import mg.bovit.release.model.Caisse;
import mg.bovit.release.model.Materiel;
import mg.bovit.release.model.MaterielType;
import mg.bovit.release.service.CaisseService;
import mg.bovit.release.service.MaterielService;
import mg.bovit.release.service.MaterielTypeService;
import mg.bovit.release.service.MouvementStockEntreeService;
import mg.bovit.release.service.MouvementStockSortieService;

@Controller
@RequestMapping("/mouvement")
public class MouvementController {
    @Autowired
    private MaterielService materielService;
    @Autowired
    private MaterielTypeService materielTypeService;
    @Autowired
    private MouvementStockEntreeService mouvementStockEntreeService;
    @Autowired
    private MouvementStockSortieService mouvementStockSortieService;
    @Autowired
    private CaisseService caisseService;

    @GetMapping("/")
    public String listMouvements() {
        return "mouvement/list";
    }

    @GetMapping("/form/entree")
    public String showFormEntree(Model model) {
        List<MaterielType> materielTypes = materielTypeService.findAll();
        List<Materiel> materiels = materielService.findAll();
        List<Caisse> caisses = caisseService.findAll();

        // List<String> typeMouvement = List.of("ENTREE", "SORTIE");
        model.addAttribute("materielTypes", materielTypes);
        model.addAttribute("materiels", materiels);
        model.addAttribute("caisses", caisses);
        // model.addAttribute("typeMouvements", typeMouvement);

        return "mouvement/formEntree";
    }

    @PostMapping("/form/entree")
    public ResponseEntity<Map<String, String>> saveMouvementEntree(@RequestBody MouvementEntreePayload payload) {
        Map<String, String> response = new HashMap<>();
        try {
            mouvementStockEntreeService.transactionerEnregistrerMouvementEntreeEtPaiements(payload);

            response.put("status", "success");
            response.put("message", "Mouvement enregistré avec succès");

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            response.put("status", "error");
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @GetMapping("/form/sortie")
    public String showFormSortie(Model model) {
        List<MaterielType> materielTypes = materielTypeService.findAll();
        List<Materiel> materiels = materielService.findAll();
        model.addAttribute("materielTypes", materielTypes);
        model.addAttribute("materiels", materiels);
        return "mouvement/formSortie";
    }

    @PostMapping("/form/sortie")
    public ResponseEntity<Map<String, String>> saveMouvementSortie(@RequestBody MouvementStockSortiePayload payload) {
        Map<String, String> response = new HashMap<>();
        try {
            // ? ici on gere les 2 action qui sont ajout dans mvt_stock_sortie et update de
            // mvt_stock_entree
            // si l'un d'eux echoue, on annule TOUTTTTT
            mouvementStockSortieService.transactionerEnregistrerMouvementSortieEtUpdateMouvementEntree(payload);

            response.put("status", "success");
            response.put("message", "Mouvement de sortie enregistré avec succès");

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            response.put("status", "error");
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
}