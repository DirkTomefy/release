package mg.bovit.release.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import mg.bovit.release.dto.MultiCriteriaFormClientList;
import mg.bovit.release.model.Client;
import mg.bovit.release.model.VenteBovin;
import mg.bovit.release.service.ClientService;

@Controller
@RequestMapping("/clients")
public class ClientController {

    private ClientService clientService;

    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

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

    @GetMapping("/{id}/ventes")
    @ResponseBody
    public List<VenteBovin> ventesByClient(@PathVariable Long id) {
        return clientService.findVentesByClientId(id);
    }
}
