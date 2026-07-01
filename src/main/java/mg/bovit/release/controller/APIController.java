package mg.bovit.release.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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

    // 1. Utiliser l'injection par constructeur (recommandé plutôt que @Autowired)
    private final MaterielService materielService;

    public APIController(MaterielService materielService) {
        this.materielService = materielService;
    }

    // 2. Utiliser @GetMapping au lieu de @RequestMapping
    // 3. Spécifier le type de retour (ex: List<MaterielDTO> ou List<Materiel>)
    @GetMapping("/materiels/type/{typeId}")
    public ResponseEntity<List<Materiel>> getMaterielsByType(@PathVariable Long typeId) {
        List<Materiel> materiels = materielService.findMaterielByTypeId(typeId);
        
        if (materiels.isEmpty()) {
            return ResponseEntity.noContent().build(); // Devrait idéalement renvoyer un 204 ou 404 si le type n'existe pas
        }
        
        return ResponseEntity.ok(materiels); // Retourne un JSON propre avec un statut 200 OK
    }
}