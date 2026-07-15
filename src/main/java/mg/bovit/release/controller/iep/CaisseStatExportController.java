package mg.bovit.release.controller.iep;

import java.io.IOException;
import java.sql.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpServletResponse;
import mg.bovit.release.dto.CaisseStatDTO;
import mg.bovit.release.service.CaisseService;
import mg.bovit.release.service.iep.CaisseStatExcelExporter;
import mg.bovit.release.service.iep.CaisseStatPdfExporter;

/**
 * Endpoints dédiés à l'export des statistiques de caisse en Excel et PDF,
 * en miroir de l'export des inventaires. Les filtres (dateDebut, dateFin,
 * caisseId, causeId) reprennent exactement ceux de /caisse/stats/data.
 * - /caisse/stats/export/excel : export Excel des stats de la période filtrée
 * - /caisse/stats/export/pdf   : export PDF des stats de la période filtrée
 */
@Controller
@RequestMapping("/caisse/stats/export")
public class CaisseStatExportController {

    @Autowired
    private CaisseService caisseService;

    @GetMapping("/excel")
    public void exportExcel(@RequestParam("dateDebut") String dateDebutStr,
                            @RequestParam("dateFin") String dateFinStr,
                            @RequestParam(value = "caisseId", required = false) Long caisseId,
                            @RequestParam(value = "causeId", required = false) Long causeId,
                            HttpServletResponse response) throws IOException {
        try {
            CaisseStatDTO stats = caisseService.getStatistiques(
                    Date.valueOf(dateDebutStr), Date.valueOf(dateFinStr), caisseId, causeId);
            String periode = "Période : du " + dateDebutStr + " au " + dateFinStr;
            byte[] data = CaisseStatExcelExporter.export(stats, periode);
            writeResponse(response, data,
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                    "statistiques_caisse.xlsx");
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        }
    }

    @GetMapping("/pdf")
    public void exportPdf(@RequestParam("dateDebut") String dateDebutStr,
                          @RequestParam("dateFin") String dateFinStr,
                          @RequestParam(value = "caisseId", required = false) Long caisseId,
                          @RequestParam(value = "causeId", required = false) Long causeId,
                          HttpServletResponse response) throws IOException {
        try {
            CaisseStatDTO stats = caisseService.getStatistiques(
                    Date.valueOf(dateDebutStr), Date.valueOf(dateFinStr), caisseId, causeId);
            String periode = "Période : du " + dateDebutStr + " au " + dateFinStr;
            byte[] data = CaisseStatPdfExporter.export(stats, periode, "Statistiques de caisse");
            writeResponse(response, data, "application/pdf", "statistiques_caisse.pdf");
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        }
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
