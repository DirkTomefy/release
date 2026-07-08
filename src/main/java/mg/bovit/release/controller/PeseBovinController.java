package mg.bovit.release.controller;

import java.io.IOException;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.servlet.http.HttpServletResponse;
import mg.bovit.release.dto.ControllerMessage;
import mg.bovit.release.dto.MulticriteriaListPeseBovin;
import mg.bovit.release.dto.PeseBovinRequest;
import mg.bovit.release.model.Bovin;
import mg.bovit.release.model.PeseBovin;
import mg.bovit.release.model.Race;
import mg.bovit.release.service.BovinService;
import mg.bovit.release.service.PeseBovinService;
import mg.bovit.release.service.RaceService;

@Controller
@RequestMapping("/peseBovin")
public class PeseBovinController {
    @Autowired
    PeseBovinService peseBovinService;
    @Autowired
    private RaceService raceService;
    @Autowired
    BovinService bovinService;

    // Permet à Spring d'accepter les paramètres date vides ("") sans lever
    // une erreur de conversion, en les traitant comme null (allowEmpty = true)
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(java.sql.Date.class,
            new CustomDateEditor(new java.text.SimpleDateFormat("yyyy-MM-dd"), true));
    }

    @GetMapping("/list")
    public String listPeseBovin(@ModelAttribute("criteria") MulticriteriaListPeseBovin criteria, Model model) {
        // Sécurisation des paramètres par défaut
        if (criteria == null) {
            criteria = new MulticriteriaListPeseBovin();
        }
        if (criteria.getSize() <= 0) {
            criteria.setSize(10);
        }

        // Appel du service de recherche multicritère paginée
        Page<PeseBovin> pesePage = peseBovinService.searchPeseBovins(criteria);
        List<Race> races = raceService.findAll();

        // Ajout des attributs requis par la vue Thymeleaf
        model.addAttribute("pesePage", pesePage);
        model.addAttribute("pesesBovin", pesePage.getContent());
        model.addAttribute("races", races);
        model.addAttribute("criteria", criteria);

        return "peseBovin/list";
    }

    @GetMapping({"/form", "/form/{id}"})
    public String formPeseBovin(
        @PathVariable(name="id", required = false) Long id,
        Model model
    ) {
        // find all bovin for select option
        List<Bovin> bovins = bovinService.findAll();

        // find peseBovin if id is not null
        if (id != null) {
            PeseBovin peseBovin = peseBovinService.findById(id);
            if (peseBovin != null) {
                model.addAttribute("peseBovin", peseBovin);
            }
        }

        model.addAttribute("bovins", bovins);

        return "peseBovin/form";
    }

    @GetMapping("/detail/{id}")
    public String showPeseDetail(
        @PathVariable("id") Long bovinId,
        @RequestParam(required = false) String dateDebut,
        @RequestParam(required = false) String dateFin,
        Model model
    ) throws Exception {

        // Récupérer les informations du bovin
        Bovin bovin = bovinService.findById(bovinId);

        // Récupérer toutes les pesées du bovin
        List<PeseBovin> pesees = peseBovinService.findByBovinIdOrderByDatePeseAsc(bovinId);

        // Si des dates de filtre sont fournies, filtrer les pesées
        if (dateDebut != null && !dateDebut.isEmpty()) {
            java.sql.Date debut = java.sql.Date.valueOf(dateDebut);
            pesees = pesees.stream()
                .filter(p -> !p.getDate_pese().before(debut))
                .collect(java.util.stream.Collectors.toList());
        }
        if (dateFin != null && !dateFin.isEmpty()) {
            java.sql.Date fin = java.sql.Date.valueOf(dateFin);
            pesees = pesees.stream()
                .filter(p -> !p.getDate_pese().after(fin))
                .collect(java.util.stream.Collectors.toList());
        }

        // Calcul des statistiques
        double poidsActuel = 0;
        double gainTotal = 0;
        double gainMoyenJour = 0;
        int nbPesees = pesees.size();

        if (!pesees.isEmpty()) {
            poidsActuel = pesees.get(pesees.size() - 1).getPoids_apres();
            double poidsInitial = pesees.get(0).getPoids_apres();
            gainTotal = poidsActuel - poidsInitial;

            // Calcul du gain moyen par jour
            java.sql.Date firstDate = pesees.get(0).getDate_pese();
            java.sql.Date lastDate = pesees.get(pesees.size() - 1).getDate_pese();
            long diffInMillis = lastDate.getTime() - firstDate.getTime();
            long diffInDays = diffInMillis / (1000 * 60 * 60 * 24);
            if (diffInDays > 0) {
                gainMoyenJour = gainTotal / diffInDays;
            }
        }

        // Déterminer les dates min et max pour les filtres
        java.sql.Date dateDebutDefault = null;
        java.sql.Date dateFinDefault = null;
        if (!pesees.isEmpty()) {
            dateDebutDefault = pesees.get(0).getDate_pese();
            dateFinDefault = pesees.get(pesees.size() - 1).getDate_pese();
        } else {
            dateDebutDefault = new java.sql.Date(System.currentTimeMillis());
            dateFinDefault = new java.sql.Date(System.currentTimeMillis());
        }

        // Ajouter les attributs au modèle
        model.addAttribute("bovinId", bovinId);
        model.addAttribute("raceNom", bovin.getRace().getNom());
        model.addAttribute("dateAchat", bovin.getDate_achat());
        model.addAttribute("poidsInitial", bovin.getPoids_achat());
        model.addAttribute("poidsActuel", poidsActuel > 0 ? poidsActuel : null);
        model.addAttribute("gainTotal", gainTotal);
        model.addAttribute("gainMoyenJour", gainMoyenJour);
        model.addAttribute("nbPesees", nbPesees);
        model.addAttribute("pesees", pesees);
        model.addAttribute("dateDebut", dateDebut != null ? java.sql.Date.valueOf(dateDebut) : dateDebutDefault);
        model.addAttribute("dateFin", dateFin != null ? java.sql.Date.valueOf(dateFin) : dateFinDefault);

        return "peseBovin/detail";
    }

    // function post to create new pese_bovin
    @PostMapping("/create")
    @ResponseBody
    public ControllerMessage createPeseBovin(
        @RequestBody PeseBovinRequest peseBovinRequest
    ) {
        ControllerMessage response = new ControllerMessage();

        try {
            // verify if bovin existe or not
            Bovin temp_bovin = bovinService.findById(peseBovinRequest.getBovinId());

            // get latest pese by bovin
            PeseBovin latestPese = peseBovinService.getLatestPeseByBovin(temp_bovin.getId());

            // verify if date comming is after date of latest pese
            if (latestPese != null && latestPese.getDate_pese().after(peseBovinRequest.getDatePese())) {
                throw new Exception("la date de pesée doit être après la date de la dernière pesée");
            }

            // verify poids_apres
            if (peseBovinRequest.getPoids() <= 0) {
                throw new Exception("Le nouveau poids du bovin ne doit pas être négatif ou null");
            }

            // insertion du nouveau pesé dans la base
            PeseBovin newPeseBovin = new PeseBovin();

            // verify if update or create
            if (peseBovinRequest.getIdPeseBovin() != null) {
                PeseBovin temp_peseBovin = peseBovinService.findById(peseBovinRequest.getIdPeseBovin());
                if (temp_peseBovin == null) {
                    throw new Exception("Pese du bovin introuvable");
                }

                newPeseBovin.setId(peseBovinRequest.getIdPeseBovin());
                newPeseBovin.setBovin(temp_bovin);
                newPeseBovin.setDate_pese(peseBovinRequest.getDatePese());
                newPeseBovin.setPoids_apres(peseBovinRequest.getPoids());
            }
            else {
                newPeseBovin.setBovin(temp_bovin);
                newPeseBovin.setDate_pese(peseBovinRequest.getDatePese());
                newPeseBovin.setPoids_apres(peseBovinRequest.getPoids());
            }

            peseBovinService.save(newPeseBovin);

            response.setStatus("success");
            response.setMessage("pesé faites avec succès.");

        } catch (Exception e) {
            response.setStatus("error");
            response.setMessage(e.getMessage());
        }

        return response;
    }

    @GetMapping("/export")
    public void exportExcel(
        @ModelAttribute("criteria") MulticriteriaListPeseBovin criteria,
        HttpServletResponse response
    ) throws IOException {

        if (criteria == null) {
            criteria = new MulticriteriaListPeseBovin();
        }
        criteria.setSize(Integer.MAX_VALUE);

        Page<PeseBovin> pesePage = peseBovinService.searchPeseBovins(criteria);
        List<PeseBovin> pesees = pesePage.getContent();

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=pesees_bovins.xlsx");

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Pesées");

            // Style entête
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);

            Row headerRow = sheet.createRow(0);
            String[] headers = {"ID", "ID Bovin", "Race", "Date de pesée", "Poids (kg)"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // Style date
            CellStyle dateStyle = workbook.createCellStyle();
            CreationHelper createHelper = workbook.getCreationHelper();
            dateStyle.setDataFormat(createHelper.createDataFormat().getFormat("dd/mm/yyyy"));

            int rowIdx = 1;
            for (PeseBovin p : pesees) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(p.getId());
                row.createCell(1).setCellValue(p.getBovin().getId());
                row.createCell(2).setCellValue(p.getBovin().getRace().getNom());

                Cell dateCell = row.createCell(3);
                dateCell.setCellValue(p.getDate_pese());
                dateCell.setCellStyle(dateStyle);

                row.createCell(4).setCellValue(p.getPoids_apres());
            }

            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(response.getOutputStream());
        }
    }
}