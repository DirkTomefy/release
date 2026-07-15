package mg.bovit.release.controller;

import mg.bovit.release.service.FactureService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/factures")
public class FactureController {

    private final FactureService factureService;

    public FactureController(FactureService factureService) {
        this.factureService = factureService;
    }

    @PostMapping("/generer/{idVente}")
    public ResponseEntity<byte[]> genererFacture(@PathVariable Long idVente) throws Exception {
        byte[] pdf = factureService.genererFacture(idVente);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=facture_" + idVente + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

    @GetMapping("/telecharger/{codeFacture}")
    public ResponseEntity<byte[]> telechargerFacture(@PathVariable String codeFacture) throws Exception {
        byte[] pdf = factureService.telechargerPdf(codeFacture);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + codeFacture + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }
}