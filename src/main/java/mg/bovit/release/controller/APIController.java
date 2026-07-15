package mg.bovit.release.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import mg.bovit.release.model.Materiel;
import mg.bovit.release.service.MaterielService;

@RestController
@RequestMapping("/api")
public class APIController {

    private final MaterielService materielService;

    public APIController(MaterielService materielService) {
        this.materielService = materielService;
    }


    @GetMapping("/materiels/type/{typeId}")
    public ResponseEntity<List<Materiel>> getMaterielsByType(@PathVariable Long typeId) {
        List<Materiel> materiels = materielService.findMaterielByTypeId(typeId);
        
        if (materiels.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        
        return ResponseEntity.ok(materiels); // Retourne un JSON propre avec un statut 200 OK
    }
}