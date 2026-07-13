package mg.bovit.release.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import mg.bovit.release.dto.MaterielStockDto;
import mg.bovit.release.dto.MultiCriteriaEtatStockMateriel;
import mg.bovit.release.model.MouvementStock;
import mg.bovit.release.service.MaterielService;
import mg.bovit.release.service.MaterielTypeService;
import mg.bovit.release.service.MouvementStockService;

@Controller
@RequestMapping("/materiel")
public class MaterielController {

    @Autowired
    private MaterielTypeService materielTypeService;

    @Autowired
    private MaterielService materielService;

    @Autowired
    private MouvementStockService mouvementStockService;

    @GetMapping("/etatstock")
    public String showEtatStockView(MultiCriteriaEtatStockMateriel criteria, Model model) {
        model.addAttribute("typeMateriels", materielTypeService.findAll());
        model.addAttribute("criteria", criteria);

        if (criteria.getIdTypeMateriel() != null) {
            model.addAttribute("materiels",
                    materielService.findMaterielByTypeId(criteria.getIdTypeMateriel().longValue()));
        } else {
            model.addAttribute("materiels", materielService.findAll());
        }
        Map<LocalDate, MaterielStockDto> stocks = mouvementStockService.searchEtatStock(criteria);

        ArrayList<String> labelles = new ArrayList<>();
        ArrayList<Double> values = new ArrayList<>();

        for (Map.Entry<LocalDate, MaterielStockDto> stock : stocks.entrySet()) {
            labelles.add(stock.getKey().toString());
            values.add(stock.getValue().getQuantiteRestant());
        }
        model.addAttribute("labels", labelles);
        model.addAttribute("values", values);

        return "materiel/etatstock";
    }

    @GetMapping("/liste")
    public String getMaterielListe(Model model) {
        List<MaterielStockDto> materielStockDtos = materielService.findAllMaterielStockRestant();
        model.addAttribute("materielStockDtos", materielStockDtos);

        return "materiel/list";
    }

    @GetMapping("/{id}")
    public String getMaterielDetails(@PathVariable Long id, Model model) {
        MaterielStockDto materielStockDto = materielService.findMaterielStockRestantById(id);
        model.addAttribute("materielStock", materielStockDto);
        List<MouvementStock> mouvementStocks = materielService.findDetailsMaterielById(id);
        model.addAttribute("mouvementStocks", mouvementStocks);

        return "materiel/listDetails";
    }
}
