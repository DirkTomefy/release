package mg.bovit.release.controller;

import mg.bovit.release.dto.BuyBovinRequest;
import mg.bovit.release.dto.MultiCriteriaFormBovinList;
import mg.bovit.release.dto.ControllerMessage;
import mg.bovit.release.dto.PeseBovinRequest;
import mg.bovit.release.model.*;
import mg.bovit.release.service.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller; // ← remplacer RestController par Controller
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller // ← important
@RequestMapping("/peseBovin")
public class PeseBovinController {
    @Autowired
    PeseBovinService peseBovinService;
    @Autowired
    BovinService bovinService;

    // function to shwo list of pese poids of bovin
    @GetMapping
    public String listPeseBovin(Model model) {
        List<PeseBovin> pesesBovin = peseBovinService.findAll();

        model.addAttribute("pesesBovin", pesesBovin);

        return "peseBovin/list";
    }
    
    // function to show forms to create pese poids
    @GetMapping("/form")
    public String formPeseBovin(Model model) {
        // find all bovin for select option
        List<Bovin> bovins = bovinService.findAll();

        model.addAttribute("bovins", bovins);

        return "peseBovin/form";
    }

    // function post to create new pese_bovin
    @PostMapping("/create")
    @ResponseBody
    public ControllerMessage createPeseBovin(@RequestBody PeseBovinRequest peseBovinRequest) {
        ControllerMessage response = new ControllerMessage();

        try {
            // verufy if bovin existe or not
            Bovin temp_bovin = bovinService.findById(peseBovinRequest.getBovinId());
    
            // get latest pese by bovin
            PeseBovin latestPese = peseBovinService.getLatestPeseByBovin(temp_bovin.getId());
    
            // verify if date comming is after date of latest pese
            if (latestPese != null && latestPese.getDate_pese().before(peseBovinRequest.getDatePese())) {
                throw new Exception("la date de pesée doit être après la date de la dernière pesée");
            }

            // verify poids_apres
            if (peseBovinRequest.getPoids() <= 0) {
                throw new Exception("Le nouveau poids du bovin ne doit pas être négatif ou null");
            }

            // insertion du nouveau pesé dans la base
            PeseBovin newPeseBovin = new PeseBovin();
            newPeseBovin.setBovin(temp_bovin);
            newPeseBovin.setDate_pese(peseBovinRequest.getDatePese());
            newPeseBovin.setPoids_apres(peseBovinRequest.getPoids());
            peseBovinService.save(newPeseBovin);

            response.setStatus("success");
            response.setMessage("pesé faites avec succès.");

        } catch (Exception e) {
            response.setStatus("error");
            response.setMessage(e.getMessage());
        }

        return response;
    }
}
