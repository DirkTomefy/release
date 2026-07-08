package mg.bovit.release.controller;

import java.io.ByteArrayOutputStream;
import java.util.List;

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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import mg.bovit.release.dto.MultiCriteriaFormClientList;
import mg.bovit.release.model.Client;
import mg.bovit.release.service.ClientService;

@Controller
@RequestMapping("/clients")
public class ClientController {

    @Autowired
    private ClientService clientService;

    // Liste + filtre + recherche multicritère globale
    @GetMapping
    public String listClients(@ModelAttribute("criteria") MultiCriteriaFormClientList criteria, Model model) {
        if (criteria == null) {
            criteria = new MultiCriteriaFormClientList();
        }
        if (criteria.getSize() <= 0) {
            criteria.setSize(10);
        }
        if (criteria.getSize() > 1000) {
            criteria.setSize(1000);
        }

        Page<Client> clientPage = clientService.searchClients(criteria);

        model.addAttribute("clientPage", clientPage);
        model.addAttribute("criteria", criteria);

        return "client/list";
    }

    // Formulaire de création (id absent) ou de modification (id présent)
    @GetMapping({"/form", "/form/{id}"})
    public String showForm(@PathVariable(name = "id", required = false) Long id, Model model) {
        Client client = new Client();
        if (id != null) {
            try {
                client = clientService.findById(id);
            } catch (Exception e) {
                // client introuvable : on repart sur un formulaire vide
            }
        }
        model.addAttribute("client", client);
        return "client/form";
    }

    @PostMapping("/save")
    public String saveClient(@ModelAttribute("client") Client client, RedirectAttributes redirectAttributes) {
        try {
            clientService.save(client);
            redirectAttributes.addFlashAttribute("successMessage", "Client enregistré avec succès.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            String redirectUrl = "redirect:/clients/form";
            if (client.getId() != null) {
                redirectUrl += "/" + client.getId();
            }
            return redirectUrl;
        }
        return "redirect:/clients";
    }

    @PostMapping("/delete/{id}")
    public String deleteClient(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            clientService.delete(id);
            redirectAttributes.addFlashAttribute("successMessage", "Client supprimé avec succès.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/clients";
    }

    @GetMapping("/export/excel")
    public ResponseEntity<byte[]> exportClientsExcel(
            @ModelAttribute("criteria") MultiCriteriaFormClientList criteria) throws Exception {

        if (criteria == null) {
            criteria = new MultiCriteriaFormClientList();
        }
        criteria.setSize(100000);
        criteria.setPage(0);

        Page<Client> clientPage = clientService.searchClients(criteria);
        List<Client> clients = clientPage.getContent();

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Clients");

            String[] headers = {"ID", "Nom", "Prénom", "Contact"};
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
            for (Client c : clients) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(c.getId() != null ? c.getId() : 0);
                row.createCell(1).setCellValue(c.getNom() != null ? c.getNom() : "");
                row.createCell(2).setCellValue(c.getPrenom() != null ? c.getPrenom() : "");
                row.createCell(3).setCellValue(c.getContact() != null ? c.getContact() : "");
            }

            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);

            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=clients.xlsx");

            return ResponseEntity.ok()
                    .headers(responseHeaders)
                    .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .body(out.toByteArray());
        }
    }
}