package mg.bovit.release.controller;

import mg.bovit.release.dto.BuyBovinRequest;
import mg.bovit.release.dto.MultiCriteriaFormBovinList;
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
@RequestMapping("/pesePoids")
public class PesePoidsController {
    @Autowired
    PesePoidsService pesePoidsService;


    @GetMapping
    public String listPesePoids(Model model) {
        List<PesePoids> pesesPoids = pesePoidsService.findAll();

        model.addAttribute("pesesPoids", pesesPoids);

        return "pesePoids/list";
    } 
}
