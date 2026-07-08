package mg.bovit.release.controller;

import mg.bovit.release.dto.BuyBovinRequest;
import mg.bovit.release.dto.MultiCriteriaFormBovinList;
import mg.bovit.release.model.Bovin;
import mg.bovit.release.model.Caisse;
import mg.bovit.release.model.Race;
import mg.bovit.release.model.sqlview.BovinWithPoids;
import mg.bovit.release.service.BovinService;
import mg.bovit.release.service.CaisseService;
import mg.bovit.release.service.RaceService;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/bovins")
public class BovinController {

    @Autowired
    private BovinService bovinService;
    @Autowired
    private RaceService raceService;
    @Autowired
    private CaisseService caisseService;

    // Méthode pour la liste – retourne une vue
    @GetMapping
    public String listBovins(@ModelAttribute("criteria") MultiCriteriaFormBovinList criteria,
            Model model) throws Exception {
        if (criteria == null) {
            criteria = new MultiCriteriaFormBovinList();
        }
        if (criteria.getSize() <= 0) {
            criteria.setSize(10);
        }
        if (criteria.getSize() > 1000) {
            criteria.setSize(1000);
        }

        Page<BovinWithPoids> bovinPage = bovinService.searchBovinsWithPoids(criteria);
        List<Race> races = raceService.findAll();

        model.addAttribute("bovinPage", bovinPage);
        model.addAttribute("races", races);
        model.addAttribute("criteria", criteria);

        return "bovin/list";
    }

    // Méthode pour afficher le formulaire d'achat – retourne une vue
    @GetMapping("/achat/form")
    public String showBuyForm(Model model) throws Exception {
        List<Race> races = raceService.findAll();
        List<Caisse> caisses = caisseService.findAll();
        model.addAttribute("races", races);
        model.addAttribute("caisses", caisses);
        model.addAttribute("buyRequest", new BuyBovinRequest());
        return "bovin/form";
    }

    // Export Excel – méthode indépendante, bien fermée
    @GetMapping("/export/excel")
    public ResponseEntity<byte[]> exportBovinsExcel(
            @ModelAttribute("criteria") MultiCriteriaFormBovinList criteria) throws Exception {

        if (criteria == null) {
            criteria = new MultiCriteriaFormBovinList();
        }
        criteria.setSize(100000);
        criteria.setPage(0);

        Page<BovinWithPoids> bovinPage = bovinService.searchBovinsWithPoids(criteria);
        List<BovinWithPoids> bovins = bovinPage.getContent();

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Bovins");

            String[] headers = {"ID", "Race", "Date achat", "Poids achat (kg)", "Poids actuel (kg)"};
            Row headerRow = sheet.createRow(0);
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);

            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            int rowIdx = 1;
            for (BovinWithPoids b : bovins) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(b.getId());
                row.createCell(1).setCellValue(b.getRaceNom() != null ? b.getRaceNom() : "");
                row.createCell(2).setCellValue(b.getDateAchat() != null ? b.getDateAchat().toString() : "");
                row.createCell(3).setCellValue(b.getPoidsAchat() != null ? b.getPoidsAchat() : 0);
                row.createCell(4).setCellValue(b.getPoidsActuel() != null ? b.getPoidsActuel() : 0);
            }

            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);

            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=bovins.xlsx");

            return ResponseEntity.ok()
                    .headers(responseHeaders)
                    .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .body(out.toByteArray());
        }
    }

    // Méthode d'achat – restée indépendante, plus imbriquée dans export
    @PostMapping("/achat")
    @ResponseBody
    public ResponseEntity<?> buyBovin(@RequestBody BuyBovinRequest request) {
        try {
            Bovin bovin = new Bovin();
            bovin.setDate_achat(Date.valueOf(request.getDateAchat()));
            bovin.setPoids_achat(request.getPoidsAchat());

            Race race = new Race();
            race.setId(request.getRaceId());
            bovin.setRace(race);

            Double prixUnitaire = request.getPrixUnitaire();
            if (prixUnitaire == null || prixUnitaire <= 0) {
                return ResponseEntity.badRequest().body(
                    Map.of("status", "error", "message", "Le prix unitaire est obligatoire et doit être > 0")
                );
            }

            List<Caisse> caisses = new ArrayList<>();
            if (request.getPayments() != null) {
                for (BuyBovinRequest.CaissePaymentDTO pDto : request.getPayments()) {
                    Caisse caisse = new Caisse();
                    caisse.setId(pDto.getCaisseId());
                    caisse.setMontant_actuelle(pDto.getMontant());
                    caisses.add(caisse);
                }
            }

            bovinService.buyBovin(bovin, caisses, request.getQuantite(), prixUnitaire);

            return ResponseEntity.ok(Map.of("status", "success", "message", "Achat enregistré avec succès !"));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("status", "error", "message", e.getMessage()));
        }
    }
}