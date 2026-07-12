package mg.bovit.release.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpServletResponse;
import mg.bovit.release.model.MouvementStock;
import mg.bovit.release.service.MouvementStockService;
import mg.bovit.release.util.MouvementStockExcelExporter;
import mg.bovit.release.util.MouvementStockPdfExporter;

/**
 * Endpoints dédiés à l'export des mouvements de stock en Excel et PDF.
 * - /mouvement/export/excel/{id} et /mouvement/export/pdf/{id} : export d'un seul mouvement
 * - /mouvement/export/excel/all et /mouvement/export/pdf/all   : export de toute la liste
 */
@Controller
@RequestMapping("/mouvement/export")
public class MouvementStockExportController {

    @Autowired
    private MouvementStockService mouvementStockService;

    @GetMapping("/excel/{id}")
    public void exportOneExcel(@PathVariable Long id, HttpServletResponse response) throws IOException {
        MouvementStock mouvement = mouvementStockService.findById(id);
        byte[] data = MouvementStockExcelExporter.export(List.of(mouvement));
        writeResponse(response, data,
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                "mouvement_" + id + ".xlsx");
    }

    @GetMapping("/pdf/{id}")
    public void exportOnePdf(@PathVariable Long id, HttpServletResponse response) throws Exception {
        MouvementStock mouvement = mouvementStockService.findById(id);
        byte[] data = MouvementStockPdfExporter.export(List.of(mouvement), "Mouvement de stock n°" + id);
        writeResponse(response, data, "application/pdf", "mouvement_" + id + ".pdf");
    }

    @GetMapping("/excel/all")
    public void exportAllExcel(HttpServletResponse response) throws IOException {
        List<MouvementStock> mouvements = mouvementStockService.findAll();
        byte[] data = MouvementStockExcelExporter.export(mouvements);
        writeResponse(response, data,
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                "liste_mouvements.xlsx");
    }

    @GetMapping("/pdf/all")
    public void exportAllPdf(HttpServletResponse response) throws Exception {
        List<MouvementStock> mouvements = mouvementStockService.findAll();
        byte[] data = MouvementStockPdfExporter.export(mouvements, "Liste des mouvements de stock");
        writeResponse(response, data, "application/pdf", "liste_mouvements.pdf");
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
