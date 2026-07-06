package mg.bovit.release.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;


import mg.bovit.release.dto.MaterielStockDto;
import mg.bovit.release.service.MaterielService;

@Controller
@RequestMapping("/inventaire")
public class InventaireController {
    @Autowired
    private MaterielService materielService;
    @GetMapping("/{id}")
    public String getInventaire(@PathVariable Long id, Model model) {
        MaterielStockDto materielStockDto = materielService.findMaterielStockRestantById(id);
        model.addAttribute("materielStock", materielStockDto);

        return "inventaire/form";
    }
    
}