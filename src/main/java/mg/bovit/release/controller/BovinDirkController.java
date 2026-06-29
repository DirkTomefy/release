package mg.bovit.release.controller;

import mg.bovit.release.dto.MultiCriteriaFormBovinList;
import mg.bovit.release.model.Race;
import mg.bovit.release.service.BovinService;
import mg.bovit.release.service.RaceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import mg.bovit.release.model.Bovin;
import java.util.List;

@Controller
@RequestMapping("/bovins")
public class BovinDirkController {

    @Autowired
    private BovinService bovinService;

   

}
