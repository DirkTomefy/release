package mg.bovit.release.controller;

import mg.bovit.release.dto.BuyBovinRequest;
import mg.bovit.release.dto.MultiCriteriaFormBovinList;
import mg.bovit.release.model.Bovin;
import mg.bovit.release.model.Caisse;
import mg.bovit.release.model.Race;
import mg.bovit.release.service.BovinService;
import mg.bovit.release.service.CaisseService;
import mg.bovit.release.service.RaceService;

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

@Controller
public class BovinApiController {
    @Autowired
    BovinService bovinService;

    // function to get bovin by id
    @GetMapping("bovin/api/{id}")
    @ResponseBody
    public Bovin findBovinById(
        @PathVariable(name="id") Long id
    ) {
        Bovin bovin;

        try {
            // find bovin by id
            bovin = bovinService.findById(id);
        } catch (Exception e) {
            bovin = null;
        }

        return bovin;
    }
}
