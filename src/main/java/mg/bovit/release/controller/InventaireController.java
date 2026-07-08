package mg.bovit.release.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import mg.bovit.release.dto.MaterielStockDto;
import mg.bovit.release.service.InventaireService;
import mg.bovit.release.service.MaterielService;

@Controller
@RequestMapping("/inventaire")
public class InventaireController {
    @Autowired
    private MaterielService materielService;
    @Autowired
    private InventaireService inventaireService;

    @GetMapping("/{id}")
    public String getInventaire(@PathVariable Long id, Model model) {
        MaterielStockDto materielStockDto = materielService.findMaterielStockRestantById(id);
        model.addAttribute("materielStock", materielStockDto);

        return "inventaire/form";
    }

    @PostMapping("/{id}")
    public String updateInventaire(@PathVariable Long id, @RequestParam("qte_reelle") Double qteReelle, @RequestParam("date_inventaire") String dateInventaire) {
        MaterielStockDto materielStockDto = materielService.findMaterielStockRestantById(id);
        if (materielStockDto != null) {
            // elle gere si c'est entree ou sortie selon la quantite reelle et la quantite restante
            inventaireService.faireInventaire(materielStockDto, qteReelle, dateInventaire);
        }

        return "redirect:/inventaire/" + id;
    }
    
}