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
import mg.bovit.release.dto.MvtCaisseCriteria;
import mg.bovit.release.model.MvtCaisse;
import mg.bovit.release.service.CaisseService;
import mg.bovit.release.service.iep.MvtCaisseExcelExporter;
import mg.bovit.release.service.iep.MvtCaissePdfExporter;

/**
 * Endpoints dédiés à l'export des mouvements de caisse en Excel et PDF.
 * - /caisse/export/excel/{id} et /caisse/export/pdf/{id} : export d'un seul mouvement
 * - /caisse/export/excel/all et /caisse/export/pdf/all   : export de toute la liste filtrée
 */
@Controller
@RequestMapping("/caisse/export")
public class MvtCaisseExportController {

    @Autowired
    private CaisseService caisseService;

    @GetMapping("/excel/{id}")
    public void exportOneExcel(@PathVariable Long id, HttpServletResponse response) throws IOException {
        MvtCaisse mvt = caisseService.findMouvementById(id);
        byte[] data = MvtCaisseExcelExporter.export(List.of(mvt));
        writeResponse(response, data,
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                "mouvement_caisse_" + id + ".xlsx");
    }

    @GetMapping("/pdf/{id}")
    public void exportOnePdf(@PathVariable Long id, HttpServletResponse response) throws Exception {
        MvtCaisse mvt = caisseService.findMouvementById(id);
        byte[] data = MvtCaissePdfExporter.export(List.of(mvt), "Mouvement de caisse n°" + id);
        writeResponse(response, data, "application/pdf", "mouvement_caisse_" + id + ".pdf");
    }

    @GetMapping("/excel/all")
    public void exportAllExcel(@ModelAttribute("criteria") MvtCaisseCriteria criteria,
                                HttpServletResponse response) throws IOException {
        List<MvtCaisse> mouvements = caisseService.searchAllForExport(criteria);
        byte[] data = MvtCaisseExcelExporter.export(mouvements);
        writeResponse(response, data,
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                "liste_mouvements_caisse.xlsx");
    }

    @GetMapping("/pdf/all")
    public void exportAllPdf(@ModelAttribute("criteria") MvtCaisseCriteria criteria,
                              HttpServletResponse response) throws Exception {
        List<MvtCaisse> mouvements = caisseService.searchAllForExport(criteria);
        byte[] data = MvtCaissePdfExporter.export(mouvements, "Liste des mouvements de caisse");
        writeResponse(response, data, "application/pdf", "liste_mouvements_caisse.pdf");
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
