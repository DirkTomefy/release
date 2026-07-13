package mg.bovit.release.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import mg.bovit.release.dto.ControllerMessage;
import mg.bovit.release.dto.MultiCriteriaFormBovinList;
import mg.bovit.release.dto.VenteInsertDto;
import mg.bovit.release.dto.*;
import mg.bovit.release.model.Client;
import mg.bovit.release.model.Facture;
import mg.bovit.release.model.Race;
import mg.bovit.release.model.VenteBovin;
import mg.bovit.release.model.sqlview.BovinWithPoids;
import mg.bovit.release.service.BovinService;
import mg.bovit.release.service.CaisseService;
import mg.bovit.release.service.ClientService;
import mg.bovit.release.service.RaceService;
import mg.bovit.release.service.VenteService;
import mg.bovit.release.model.VenteDetail;
import mg.bovit.release.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.nio.charset.StandardCharsets;

@Controller
@RequestMapping("/vente")
public class VenteController {

    @Autowired
    private VenteService venteService;
    @Autowired
    private ClientService clientService;
    @Autowired
    private BovinService bovinService;
    @Autowired
    private RaceService raceService;
    @Autowired
    private CaisseService caisseService;
    @Autowired
    private FactureService factureService;
    @Autowired
    private VenteDetailService venteDetailService; // Nouveau service à créer
  
   public VenteController(
            VenteService venteService,
            ClientService clientService,
            BovinService bovinService,
            RaceService raceService,
            CaisseService caisseService) {
        this.venteService = venteService;
        this.clientService = clientService;
        this.bovinService = bovinService;
        this.raceService = raceService;
        this.caisseService = caisseService;
    }

    // Page d'insertion d'une vente
    @GetMapping("/new")
    public String showInsertForm(@ModelAttribute("criteria") MultiCriteriaFormBovinList criteria, Model model) {
        if (criteria == null) {
            criteria = new MultiCriteriaFormBovinList();
        }
        if (criteria.getStatut() == null || criteria.getStatut().isBlank()) {
            criteria.setStatut("non_vendu");
        }
        if (criteria.getSize() <= 0) {
            criteria.setSize(10);
        }
        if (criteria.getSize() > 1000) {
            criteria.setSize(1000);
        }

        Page<BovinWithPoids> bovinPage = bovinService.searchBovinsWithPoids(criteria);
        List<Client> clients = clientService.findAll();
        List<Race> races = raceService.findAll();

        model.addAttribute("bovinPage", bovinPage);
        model.addAttribute("clients", clients);
        model.addAttribute("races", races);
        model.addAttribute("criteria", criteria);
        model.addAttribute("venteForm", new VenteInsertDto());
        model.addAttribute("caisses", caisseService.findAll());

        return "vente/form";
    }

    @PostMapping("/save")
    @ResponseBody
    public ControllerMessage saveVente(@RequestBody VenteInsertDto venteInsertDto) {
        ControllerMessage response = new ControllerMessage();
        try {
            venteService.insertVente(venteInsertDto);
            response.setStatus("success");
            response.setMessage("Vente enregistrée avec succès.");
        } catch (Exception e) {
            response.setStatus("error");
            response.setMessage(e.getMessage());
        }
        return response;
    }

    // ==================== Liste des ventes ====================
    @GetMapping("/list")
    public String listVentes(Model model) {
        model.addAttribute("clients", clientService.findAll());
        model.addAttribute("races", raceService.findAll());
        model.addAttribute("searchCriteria", new VenteSearchCriteria());
        return "vente/list";
    }

    @GetMapping("/list/data")
    @ResponseBody
    public Page<VenteListItem> getVentesList(VenteSearchCriteria criteria,
                                             @RequestParam(defaultValue = "0") int page,
                                             @RequestParam(defaultValue = "10") int size) {
        criteria.setPage(page);
        criteria.setSize(size);
        Pageable pageable = PageRequest.of(page, size, Sort.by("date_vente").descending());
        return venteService.searchVentes(criteria, pageable);
    }

    // ==================== Détails d'une vente ====================
    @GetMapping("/{id}/details")
    public String venteDetails(@PathVariable Long id, Model model) {
        VenteBovin vente = venteService.findById(id).orElseThrow();
        List<VenteDetail> details = venteDetailService.findByVenteId(id);
        Facture facture = factureService.findByVenteId(id).orElse(null);
        double totalVente = details.stream()
                .mapToDouble(d -> d.getBovin().getPrix_vente() != null ? d.getBovin().getPrix_vente() : 0.0)
                .sum();
        model.addAttribute("vente", vente);
        model.addAttribute("details", details);
        model.addAttribute("facture", facture);
        model.addAttribute("venteTotal", totalVente);
        return "vente/details";
    }

    // ==================== Génération / téléchargement facture ====================
    @Transactional
    @PostMapping("/{id}/facture")
    public ResponseEntity<byte[]> gererFacture(@PathVariable Long id) {
        try {
            // Vérifier si la facture existe déjà
            if (factureService.existsByVenteId(id)) {
                Facture facture = factureService.findByVenteId(id).get();
                byte[] pdf = factureService.generatePdf(facture);
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + facture.getCodeFacture() + ".pdf")
                        .contentType(MediaType.APPLICATION_PDF)
                        .body(pdf);
            } else {
                // Générer la facture et la sauvegarder, retourner le PDF
                byte[] pdf = factureService.genererFacture(id);
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=facture_" + id + ".pdf")
                        .contentType(MediaType.APPLICATION_PDF)
                        .body(pdf);
            }
        } catch (Exception e) {
            String message = e.getMessage() != null ? e.getMessage() : "Erreur lors de la génération de la facture";
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .contentType(MediaType.TEXT_PLAIN)
                    .body(message.getBytes(StandardCharsets.UTF_8));
        }
    }

    // ==================== Statistiques ====================
    @GetMapping("/stats")
    public String statsPage(Model model) {
        model.addAttribute("races", raceService.findAll());
        return "vente/stats";
    }

    @GetMapping("/stats/data")
    @ResponseBody
    public VenteStatsDTO getStatsData(@RequestParam(required = false) String dateDebut,
                                      @RequestParam(required = false) String dateFin,
                                      @RequestParam(required = false) Long raceId) {
        LocalDate debut = dateDebut != null && !dateDebut.isEmpty() ? LocalDate.parse(dateDebut) : null;
        LocalDate fin = dateFin != null && !dateFin.isEmpty() ? LocalDate.parse(dateFin) : null;
        return venteService.getVenteStats(debut, fin, raceId);
    }
}
