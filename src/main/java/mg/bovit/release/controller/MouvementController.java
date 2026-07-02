package mg.bovit.release.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import mg.bovit.release.model.Caisse;
import mg.bovit.release.model.Materiel;
import mg.bovit.release.model.MaterielType;
import mg.bovit.release.service.CaisseService;
import mg.bovit.release.service.MaterielService;
import mg.bovit.release.service.MaterielTypeService;
import mg.bovit.release.service.MouvementStockService;

@Controller
@RequestMapping("/mouvements")
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
        return "mouvements/list";
    }

    @GetMapping("/form")
    public String showForm(Model model) {
        List<MaterielType> materielTypes = materielTypeService.findAll();
        List<Materiel> materiels = materielService.findAll();
        List<Caisse> caisses = caisseService.findAll();

        // List<String> typeMouvement = List.of("ENTREE", "SORTIE");
        model.addAttribute("materielTypes", materielTypes);
        model.addAttribute("materiels", materiels);
        model.addAttribute("caisses", caisses);
        // model.addAttribute("typeMouvements", typeMouvement);

        return "mouvement/entreeForm";
    }

    // @GetMapping("/list")
}
