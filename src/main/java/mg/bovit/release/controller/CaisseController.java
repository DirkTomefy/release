package mg.bovit.release.controller;

import java.io.ByteArrayOutputStream;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import mg.bovit.release.dto.CaisseStatDTO;
import mg.bovit.release.model.Caisse;
import mg.bovit.release.service.CaisseService;

@Controller
@RequestMapping("/caisse")
public class CaisseController {

    @Autowired
    private CaisseService caisseService;

    // Page des statistiques d'entrées / sorties de caisse (histogramme + filtres)
    @GetMapping("/stats")
    public String showStats(Model model) {
        List<Caisse> caisses = caisseService.findAll();

        LocalDate finDefaut = LocalDate.now();
        LocalDate debutDefaut = finDefaut.minusDays(29); // 30 derniers jours par défaut

        model.addAttribute("caisses", caisses);
        model.addAttribute("dateDebutDefaut", debutDefaut);
        model.addAttribute("dateFinDefaut", finDefaut);

        return "caisse/stats";
    }

    // Données JSON pour le graphique, rafraîchies en AJAX à chaque changement de filtre.
    // caisseId absent ou null => toutes les caisses confondues.
    @GetMapping("/stats/data")
    @ResponseBody
    public CaisseStatDTO getStatsData(
            @RequestParam("dateDebut") String dateDebutStr,
            @RequestParam("dateFin") String dateFinStr,
            @RequestParam(value = "caisseId", required = false) Long caisseId
    ) {
        try {
            Date dateDebut = Date.valueOf(dateDebutStr);
            Date dateFin = Date.valueOf(dateFinStr);
            return caisseService.getStatistiques(dateDebut, dateFin, caisseId);
        } catch (IllegalArgumentException e) {
            CaisseStatDTO erreur = new CaisseStatDTO();
            erreur.setErreur("Format de date invalide.");
            return erreur;
        } catch (Exception e) {
            CaisseStatDTO erreur = new CaisseStatDTO();
            erreur.setErreur(e.getMessage() != null ? e.getMessage() : "Erreur inattendue.");
            return erreur;
        }
    }

    // Export Excel des mêmes statistiques (mêmes filtres que /stats/data)
    @GetMapping("/stats/export/excel")
    public ResponseEntity<byte[]> exportStatsExcel(
            @RequestParam("dateDebut") String dateDebutStr,
            @RequestParam("dateFin") String dateFinStr,
            @RequestParam(value = "caisseId", required = false) Long caisseId
    ) throws Exception {

        Date dateDebut;
        Date dateFin;
        try {
            dateDebut = Date.valueOf(dateDebutStr);
            dateFin = Date.valueOf(dateFinStr);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }

        CaisseStatDTO stats = caisseService.getStatistiques(dateDebut, dateFin, caisseId);

        if (stats.getErreur() != null) {
            return ResponseEntity.badRequest().build();
        }

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Statistiques Caisse");

            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);

            // Ligne d'en-tête
            String[] headers = {"Période", "Entrées", "Sorties", "Solde intervalle"};
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // Lignes de données, une par intervalle (label / entrée / sortie)
            List<String> labels = stats.getLabels();
            List<Double> entrees = stats.getEntrees();
            List<Double> sorties = stats.getSorties();

            int rowIdx = 1;
            if (labels != null) {
                for (int i = 0; i < labels.size(); i++) {
                    Row row = sheet.createRow(rowIdx++);
                    double entree = (entrees != null && i < entrees.size() && entrees.get(i) != null) ? entrees.get(i) : 0.0;
                    double sortie = (sorties != null && i < sorties.size() && sorties.get(i) != null) ? sorties.get(i) : 0.0;

                    row.createCell(0).setCellValue(labels.get(i));
                    row.createCell(1).setCellValue(entree);
                    row.createCell(2).setCellValue(sortie);
                    row.createCell(3).setCellValue(entree - sortie);
                }
            }

            // Ligne vide puis totaux cumulés sur la période
            rowIdx++;
            Row totalLabelRow = sheet.createRow(rowIdx++);
            Cell totalLabelCell = totalLabelRow.createCell(0);
            totalLabelCell.setCellValue("Total période");
            totalLabelCell.setCellStyle(headerStyle);

            Row totalRow = sheet.createRow(rowIdx++);
            totalRow.createCell(0).setCellValue("Entrées");
            totalRow.createCell(1).setCellValue(stats.getTotalEntree() != null ? stats.getTotalEntree() : 0.0);

            Row totalRow2 = sheet.createRow(rowIdx++);
            totalRow2.createCell(0).setCellValue("Sorties");
            totalRow2.createCell(1).setCellValue(stats.getTotalSortie() != null ? stats.getTotalSortie() : 0.0);

            Row totalRow3 = sheet.createRow(rowIdx++);
            totalRow3.createCell(0).setCellValue("Solde");
            totalRow3.createCell(1).setCellValue(stats.getSolde() != null ? stats.getSolde() : 0.0);

            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);

            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=stats_caisse.xlsx");

            return ResponseEntity.ok()
                    .headers(responseHeaders)
                    .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .body(out.toByteArray());
        }
    }
}