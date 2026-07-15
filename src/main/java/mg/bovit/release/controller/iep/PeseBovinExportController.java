package mg.bovit.release.controller.iep;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpServletResponse;
import mg.bovit.release.dto.MulticriteriaListPeseBovin;
import mg.bovit.release.model.sqlview.PeseBovinWithDateVente;
import mg.bovit.release.service.PeseBovinService;
import mg.bovit.release.service.iep.PeseBovinExcelExporter;
import mg.bovit.release.service.iep.PeseBovinPdfExporter;

/**
 * Endpoints dédiés à l'export des pesées de bovins en Excel et PDF.
 * - /peseBovin/export/excel/{id} et /peseBovin/export/pdf/{id} : export d'une seule pesée
 * - /peseBovin/export/excel/all et /peseBovin/export/pdf/all   : export de toute la liste filtrée
 *   (les mêmes critères que la liste - criteria - sont réutilisés, pagination ignorée)
 */
@Controller
@RequestMapping("/peseBovin/export")
public class PeseBovinExportController {

    @Autowired
    PeseBovinService peseBovinService;

    @GetMapping("/excel/{id}")
    public void exportOneExcel(@PathVariable Long id, HttpServletResponse response) throws IOException {
        PeseBovinWithDateVente pese = peseBovinService.findViewById(id);
        byte[] data = PeseBovinExcelExporter.export(List.of(pese));
        writeResponse(response, data,
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                "pesee_" + id + ".xlsx");
    }

    @GetMapping("/pdf/{id}")
    public void exportOnePdf(@PathVariable Long id, HttpServletResponse response) throws Exception {
        PeseBovinWithDateVente pese = peseBovinService.findViewById(id);
        byte[] data = PeseBovinPdfExporter.export(List.of(pese), "Fiche de pesée n°" + id);
        writeResponse(response, data, "application/pdf", "pesee_" + id + ".pdf");
    }

    @GetMapping("/excel/all")
    public void exportAllExcel(@ModelAttribute("criteria") MulticriteriaListPeseBovin criteria,
                                HttpServletResponse response) throws IOException {
        List<PeseBovinWithDateVente> pesees = peseBovinService.searchAllForExport(criteria);
        byte[] data = PeseBovinExcelExporter.export(pesees);
        writeResponse(response, data,
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                "liste_pesees.xlsx");
    }

    @GetMapping("/pdf/all")
    public void exportAllPdf(@ModelAttribute("criteria") MulticriteriaListPeseBovin criteria,
                              HttpServletResponse response) throws Exception {
        List<PeseBovinWithDateVente> pesees = peseBovinService.searchAllForExport(criteria);
        byte[] data = PeseBovinPdfExporter.export(pesees, "Liste des pesées de bovins");
        writeResponse(response, data, "application/pdf", "liste_pesees.pdf");
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
