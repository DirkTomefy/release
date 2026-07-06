package mg.bovit.release.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import mg.bovit.release.dto.MaterielStockDto;
import mg.bovit.release.service.MaterielService;

@Controller
@RequestMapping("/materiel")
public class MaterielController {
    @Autowired
    private MaterielService materielService;

    @GetMapping("/liste")
    public String getMaterielListe(Model model) {
        List<MaterielStockDto> materielStockDtos = materielService.findAllMaterielStockRestant();
        model.addAttribute("materielStockDtos", materielStockDtos);

        return "materiel/list";
    }
}
