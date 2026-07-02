package mg.bovit.release.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import mg.bovit.release.dto.MouvementEntreePayload;
import mg.bovit.release.model.Caisse;
import mg.bovit.release.model.Materiel;
import mg.bovit.release.model.MaterielType;
import mg.bovit.release.model.MouvementStockEntree;
import mg.bovit.release.service.CaisseService;
import mg.bovit.release.service.MaterielService;
import mg.bovit.release.service.MaterielTypeService;
import mg.bovit.release.service.MouvementStockEntreePaiementService;
import mg.bovit.release.service.MouvementStockEntreeService;

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
    private MouvementStockEntreePaiementService mouvementStockEntreePaiementService;
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
        MouvementStockEntree mouvementStockEntreeSaved = mouvementStockEntreeService.saveFromPayloadAndReturn(payload);
        mouvementStockEntreePaiementService.saveListPaiementFromPayload(payload, mouvementStockEntreeSaved);

        // On crée un vrai objet JSON { "status": "success", "message": "..." }
        Map<String, String> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Mouvement enregistré avec succès");

        return ResponseEntity.ok(response);
    }

    // @GetMapping("/list")
}
