package mg.bovit.release.controller;

import mg.bovit.release.dto.MultiCriteriaFormClientList;
import mg.bovit.release.model.Client;
import mg.bovit.release.service.ClientService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
}
