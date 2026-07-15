package mg.bovit.release.controller;

import mg.bovit.release.service.ImportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Controller
@RequestMapping("/import")
public class ImportController {

    @Autowired
    private ImportService importService;

    @GetMapping
    public String showImportPage() {
        return "import";
    }

    @PostMapping("/upload")
    public String handleImport(@RequestParam("type") String type,
                               @RequestParam("file") MultipartFile file,
                               Model model) {
        if (file.isEmpty()) {
            model.addAttribute("error", "Veuillez sélectionner un fichier.");
            return "import";
        }
        try {
            switch (type) {
                case "caisse":
                    importService.importCaisseEtMouvements(file);
                    break;
                case "pesee":
                    importService.importBovinsEtPesees(file);
                    break;
                case "inventaire":
                    importService.importInventaireEtDetails(file);
                    break;
                case "paiement":
                    importService.importPaiementsEmployesContrats(file);
                    break;
                default:
                    model.addAttribute("error", "Type d'import inconnu.");
                    return "import";
            }
            model.addAttribute("success", "Import réussi !");
        } catch (Exception e) {
            model.addAttribute("error", "Erreur lors de l'import : " + e.getMessage());
        }
        return "import";
    }

    @GetMapping("/model")
    public ResponseEntity<?> downloadModel(@RequestParam("type") String type) {
        try {
            byte[] data = importService.generateModel(type);
            String fileName;
            switch (type) {
                case "caisse": fileName = "modele_caisse.xlsx"; break;
                case "pesee": fileName = "modele_pesee.xlsx"; break;
                case "inventaire": fileName = "modele_inventaire.xlsx"; break;
                case "paiement": fileName = "modele_paiement.xlsx"; break;
                default: return ResponseEntity.badRequest().body("Type inconnu");
            }
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName);
            return ResponseEntity.ok()
                    .headers(headers)
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(data);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Type inconnu");
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("Erreur lors de la génération du modèle");
        }
    }
}