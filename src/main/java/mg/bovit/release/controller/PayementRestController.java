package mg.bovit.release.controller;

import mg.bovit.release.dto.PayementDTO;
import mg.bovit.release.service.PayementEmployeeService;
import java.util.Map;
import java.util.HashMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/employees")
public class PayementRestController {

    @Autowired
    private PayementEmployeeService payementEmployeeService;

    @PostMapping("/payement")
    public ResponseEntity<Map<String, Object>> archiverPaiement(@RequestBody PayementDTO dto) {
        Map<String, Object> response = new HashMap<>();
        try {
            String result = payementEmployeeService.preciterPaiement(dto);
            response.put("status", result);
            if (result.equals("skipped")) {
                response.put("message", "Ce mois a déjà été régularisé pour cet employé.");
            } else {
                response.put("message", "Le paiement a été enregistré avec succès et la caisse a été mise à jour.");
            }
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}