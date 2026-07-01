package mg.bovit.release.controller;

import mg.bovit.release.dto.MulticriteriaListPeseBovin;
import mg.bovit.release.dto.ControllerMessage;
import mg.bovit.release.dto.PeseBovinRequest;
import mg.bovit.release.model.*;
import mg.bovit.release.service.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller; 
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller 
@RequestMapping("/peseBovin")
public class PeseBovinController {
    @Autowired
    PeseBovinService peseBovinService;
    @Autowired
    BovinService bovinService;

    @GetMapping("/list")
    public String listPeseBovin(@ModelAttribute("criteria") MulticriteriaListPeseBovin criteria,Model model) {
        List<PeseBovin> pesesBovin = peseBovinService.findAll();

        model.addAttribute("pesesBovin", pesesBovin);

        return "peseBovin/list";
    }
    
    @GetMapping({"/form", "/form/{id}"})
    public String formPeseBovin(
        @PathVariable(name="id", required = false) Long id,
        Model model
    ) {
        // find all bovin for select option
        List<Bovin> bovins = bovinService.findAll();

        // find peseBovin if id is not null
        if (id != null) {
            PeseBovin peseBovin = peseBovinService.findById(id);
            if (peseBovin != null) {
                model.addAttribute("peseBovin", peseBovin);
            }
        }

        model.addAttribute("bovins", bovins);

        return "peseBovin/form";
    }

    // function post to create new pese_bovin
    @PostMapping("/create")
    @ResponseBody
    public ControllerMessage createPeseBovin(
        @RequestBody PeseBovinRequest peseBovinRequest
    ) {
        ControllerMessage response = new ControllerMessage();

        try {
            // verufy if bovin existe or not
            Bovin temp_bovin = bovinService.findById(peseBovinRequest.getBovinId());
    
            // get latest pese by bovin
            PeseBovin latestPese = peseBovinService.getLatestPeseByBovin(temp_bovin.getId());
    
            // verify if date comming is after date of latest pese
            if (latestPese != null && latestPese.getDate_pese().after(peseBovinRequest.getDatePese())) {
                throw new Exception("la date de pesée doit être après la date de la dernière pesée");
            }

            // verify poids_apres
            if (peseBovinRequest.getPoids() <= 0) {
                throw new Exception("Le nouveau poids du bovin ne doit pas être négatif ou null");
            }

            // insertion du nouveau pesé dans la base
            PeseBovin newPeseBovin = new PeseBovin();

            // verify if update or create
            if (peseBovinRequest.getIdPeseBovin() != null) {
                PeseBovin temp_peseBovin = peseBovinService.findById(peseBovinRequest.getIdPeseBovin());
                if (temp_peseBovin == null) {
                    throw new Exception("Pese du bovin introuvable");
                }

                newPeseBovin.setId(peseBovinRequest.getIdPeseBovin());
                newPeseBovin.setBovin(temp_bovin);
                newPeseBovin.setDate_pese(peseBovinRequest.getDatePese());
                newPeseBovin.setPoids_apres(peseBovinRequest.getPoids());
            }
            else {
                newPeseBovin.setBovin(temp_bovin);
                newPeseBovin.setDate_pese(peseBovinRequest.getDatePese());
                newPeseBovin.setPoids_apres(peseBovinRequest.getPoids());
            }

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
