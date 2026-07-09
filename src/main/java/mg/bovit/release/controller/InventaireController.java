package mg.bovit.release.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import mg.bovit.release.dto.InventairePayload;
import mg.bovit.release.dto.MaterielStockDto;
import mg.bovit.release.model.Caisse;
import mg.bovit.release.model.Inventaire;
import mg.bovit.release.model.InventaireDetail;
import mg.bovit.release.model.Materiel;
import mg.bovit.release.model.MaterielType;
import mg.bovit.release.service.InventaireService;
import mg.bovit.release.service.MaterielService;
import mg.bovit.release.service.MaterielTypeService;

@Controller
@RequestMapping("/inventaire")
public class InventaireController {
    @Autowired
    private MaterielService materielService;
    @Autowired
    private InventaireService inventaireService;

    @Autowired
    private MaterielTypeService materielTypeService; // Ajoute l'injection en haut du controleur

    @GetMapping("/form")
    public String getInventaireForm(Model model) {
        // Recupere tous les materiels avec leur stock actuel pour initialiser le
        // tableau
        List<MaterielStockDto> stocks = materielService.findAllMaterielStockRestant();
        List<MaterielType> materielTypes = materielTypeService.findAll();
        List<Materiel> materiels = materielService.findAll();

        model.addAttribute("materielTypes", materielTypes);
        model.addAttribute("materiels", materiels);
        model.addAttribute("stocks", stocks);

        return "inventaire/form";
    }

    @PostMapping("/form/valider")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> validerMultipleInventaire(@RequestBody InventairePayload payload) {
        Map<String, Object> response = new HashMap<>();
        try {
            if (payload.getDetails() == null || payload.getDetails().isEmpty()) {
                response.put("status", "error");
                response.put("message", "Aucun materiel n'a ete ajoute a l'inventaire.");
                return ResponseEntity.badRequest().body(response);
            }

            // Traite l'inventaire complet en base et ajuste les mouvements de stock
            inventaireService.faireInventaireMultiple(payload);

            response.put("status", "success");
            response.put("message",
                    "L'inventaire global a ete enregistre et le stock de chaque materiel a ete ajuste.");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Erreur lors de la validation globale : " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    @GetMapping("/liste")
    public String listInventaires(Model model) {
        // Implementation for listing inventaires
        List<Inventaire> inventaires = inventaireService.listerInventaires();
        model.addAttribute("inventaires", inventaires);

        return "inventaire/list";
    }

    @GetMapping("/{id}/details")
    public String listInventaireDetails(@PathVariable Long id, Model model) {
        List<InventaireDetail> inventaireDetails = inventaireService.listerInventairesDetails();
        model.addAttribute("inventaireDetails", inventaireDetails);

        return "inventaire/details";
    }
}