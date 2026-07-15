package mg.bovit.release.controller.iep;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpServletResponse;
import mg.bovit.release.dto.MaterielStockDto;
import mg.bovit.release.model.Inventaire;
import mg.bovit.release.model.InventaireDetail;
import mg.bovit.release.service.InventaireService;
import mg.bovit.release.service.MaterielService;
import mg.bovit.release.service.iep.InventaireExcelExporter;
import mg.bovit.release.service.iep.InventairePdfExporter;

/**
 * Endpoints dédiés à l'export des inventaires en Excel et PDF.
 * - /inventaire/export/excel/{id} et /inventaire/export/pdf/{id} : export d'un seul inventaire
 * - /inventaire/export/excel/all et /inventaire/export/pdf/all   : export de toute la liste
 */
@Controller
@RequestMapping("/inventaire/export")
public class InventaireExportController {

    @Autowired
    private InventaireService inventaireService;
    @Autowired
    private MaterielService materielService;

    private Map<Long, Double> computeCurrentStocks(List<InventaireDetail> details) {
        Map<Long, Double> currentStocks = new HashMap<>();
        for (InventaireDetail detail : details) {
            if (detail.getMateriel() != null) {
                MaterielStockDto stock = materielService.findMaterielStockRestantById(detail.getMateriel().getId());
                currentStocks.put(detail.getMateriel().getId(),
                        stock != null && stock.getQuantiteRestant() != null ? stock.getQuantiteRestant() : 0.0);
            }
        }
        return currentStocks;
    }

    @GetMapping("/excel/{id}")
    public void exportOneExcel(@PathVariable Long id, HttpServletResponse response) throws IOException {
        List<InventaireDetail> details = inventaireService.listerInventairesDetailsParId(id);
        Map<Long, Double> currentStocks = computeCurrentStocks(details);
        byte[] data = InventaireExcelExporter.exportDetails(id, details, currentStocks);
        writeResponse(response, data,
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                "inventaire_" + id + ".xlsx");
    }

    @GetMapping("/pdf/{id}")
    public void exportOnePdf(@PathVariable Long id, HttpServletResponse response) throws Exception {
        List<InventaireDetail> details = inventaireService.listerInventairesDetailsParId(id);
        Map<Long, Double> currentStocks = computeCurrentStocks(details);
        byte[] data = InventairePdfExporter.exportDetails(id, details, currentStocks, "Inventaire n°" + id);
        writeResponse(response, data, "application/pdf", "inventaire_" + id + ".pdf");
    }

    @GetMapping("/excel/all")
    public void exportAllExcel(HttpServletResponse response) throws IOException {
        List<Inventaire> inventaires = inventaireService.listerInventaires();
        byte[] data = InventaireExcelExporter.export(inventaires);
        writeResponse(response, data,
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                "liste_inventaires.xlsx");
    }

    @GetMapping("/pdf/all")
    public void exportAllPdf(HttpServletResponse response) throws Exception {
        List<Inventaire> inventaires = inventaireService.listerInventaires();
        byte[] data = InventairePdfExporter.export(inventaires, "Liste des inventaires");
        writeResponse(response, data, "application/pdf", "liste_inventaires.pdf");
    }

    private void writeResponse(HttpServletResponse response, byte[] data, String contentType, String filename)
            throws IOException {
        response.setContentType(contentType);
        response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");
        response.setContentLength(data.length);
        response.getOutputStream().write(data);
        response.getOutputStream().flush();
    }
}
