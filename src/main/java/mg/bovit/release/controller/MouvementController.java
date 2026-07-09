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

import mg.bovit.release.dto.MouvementStockPayload;
import mg.bovit.release.model.Caisse;
import mg.bovit.release.model.Materiel;
import mg.bovit.release.model.MaterielType;
import mg.bovit.release.service.CaisseService;
import mg.bovit.release.service.MaterielService;
import mg.bovit.release.service.MaterielTypeService;
import mg.bovit.release.service.MouvementStockService;

@Controller
@RequestMapping("/mouvement")
public class MouvementController {

    @Autowired
    private MaterielService materielService;
    @Autowired
    private MaterielTypeService materielTypeService;
    @Autowired
    private MouvementStockService mouvementStockService;
    @Autowired
    private CaisseService caisseService;

    @GetMapping("/")
    public String listMouvements() {
        return "mouvement/list";
    }

    // Affiche le formulaire unique fusionne
    @GetMapping("/form")
    public String showFormUnique(Model model) {
        List<MaterielType> materielTypes = materielTypeService.findAll();
        List<Materiel> materiels = materielService.findAll();
        List<Caisse> caisses = caisseService.findAll();

        model.addAttribute("materielTypes", materielTypes);
        model.addAttribute("materiels", materiels);
        model.addAttribute("caisses", caisses);

        return "mouvement/mouvement-form"; // Nom de ton nouveau fichier HTML unique
    }

    // Endpoint de validation unique pour ENTREE et SORTIE
    @PostMapping("/form/valider")
    public ResponseEntity<Map<String, String>> validerMouvement(@RequestBody MouvementStockPayload payload) {
        Map<String, String> response = new HashMap<>();
        try {
            mouvementStockService.traiterMouvementStock(payload);

            response.put("status", "success");
            response.put("message", "Mouvement traite avec succes");
            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            response.put("status", "error");
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
}