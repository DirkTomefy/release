package mg.bovit.release.controller;
import mg.bovit.release.model.sqlview.BovinWithPoids;
import mg.bovit.release.service.BovinService;


import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Controller; // ← remplacer RestController par Controller
import org.springframework.web.bind.annotation.*;


@Controller
public class BovinApiController {
    @Autowired
    BovinService bovinService;

    // function to get bovin by id
    @GetMapping("bovin/api/{id}")
    @ResponseBody
    public BovinWithPoids findBovinById(
        @PathVariable(name="id") Long id
    ) {
        BovinWithPoids bovin;

        try {
            // find bovin by id
            bovin = bovinService.findBovinPoidsById(id);
        } catch (Exception e) {
            bovin = null;
        }

        return bovin;
    }
}
