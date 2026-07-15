package mg.bovit.release.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mg.bovit.release.dto.MaterielStockDto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import mg.bovit.release.dto.InventairePayload;
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
    private MaterielTypeService materielTypeService;

    @GetMapping("/form")
    public String getInventaireForm(Model model) {
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
        List<Inventaire> inventaires = inventaireService.listerInventaires();

        // Stock actuel de chaque matériel (materielId -> quantité restante)
        Map<Long, Double> stockByMateriel = new HashMap<>();
        for (MaterielStockDto stock : materielService.findAllMaterielStockRestant()) {
            stockByMateriel.put(stock.getMateriel().getId(),
                    stock.getQuantiteRestant() != null ? stock.getQuantiteRestant() : 0.0);
        }

        // Quantité actuelle totale par inventaire (somme des stocks actuels de ses matériels)
        Map<Long, Double> currentQuantityByInventaire = new HashMap<>();
        for (Inventaire inventaire : inventaires) {
            double total = 0.0;
            for (InventaireDetail detail : inventaireService.listerInventairesDetailsParId(inventaire.getId())) {
                if (detail.getMateriel() != null) {
                    total += stockByMateriel.getOrDefault(detail.getMateriel().getId(), 0.0);
                }
            }
            currentQuantityByInventaire.put(inventaire.getId(), total);
        }

        model.addAttribute("inventaires", inventaires);
        model.addAttribute("currentQuantityByInventaire", currentQuantityByInventaire);

        return "inventaire/list";
    }

    @GetMapping("/{id}/details")
    public String listInventaireDetails(@PathVariable Long id, Model model) {
        // Filtrer ou charger les détails specifiques a cet inventaire id
        List<InventaireDetail> inventaireDetails = inventaireService.listerInventairesDetailsParId(id);

        // Calcul de la quantité actuelle (stock restant) de chaque matériel
        Map<Long, Double> currentStocks = new HashMap<>();
        for (InventaireDetail detail : inventaireDetails) {
            if (detail.getMateriel() != null) {
                MaterielStockDto stock = materielService.findMaterielStockRestantById(detail.getMateriel().getId());
                currentStocks.put(detail.getMateriel().getId(), stock != null && stock.getQuantiteRestant() != null ? stock.getQuantiteRestant() : 0.0);
            }
        }

        model.addAttribute("inventaireDetails", inventaireDetails);
        model.addAttribute("currentStocks", currentStocks);

        return "inventaire/listDetails";
    }
}