package mg.bovit.release.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import mg.bovit.release.dto.MultiCriteriaFormClientList;
import mg.bovit.release.model.Client;
import mg.bovit.release.model.VenteBovin;
import mg.bovit.release.repository.ClientRepository;
import mg.bovit.release.repository.VenteBovinRepository;
import mg.bovit.release.specification.ClientSpecification;

@Service
public class ClientService {
    private final ClientRepository clientRepository;
    private final VenteBovinRepository venteBovinRepository;

    public ClientService(ClientRepository clientRepository, VenteBovinRepository venteBovinRepository) {
        this.clientRepository = clientRepository;
        this.venteBovinRepository = venteBovinRepository;
    }

    // function to find client by id
    public Client findById(Long id_client) throws Exception {
        return clientRepository.findById(id_client).orElseThrow(() -> new Exception("Client introuvable"));
    }

    public List<Client> findAll() {
        return clientRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<VenteBovin> findVentesByClientId(Long clientId) {
        return venteBovinRepository.findByClient_Id(clientId);
    }

    public Client save(Client client) throws Exception {
        if (client.getNom() == null || client.getNom().isBlank()) {
            throw new Exception("Le nom du client est obligatoire");
        }
        if (client.getPrenom() == null || client.getPrenom().isBlank()) {
            throw new Exception("Le prénom du client est obligatoire");
        }
        if (client.getContact() == null || client.getContact().isBlank()) {
            throw new Exception("Le contact du client est obligatoire");
        }

        String contact = client.getContact().trim();
        client.setContact(contact);

        boolean duplicateContact = client.getId() == null
                ? clientRepository.existsByContactIgnoreCase(contact)
                : clientRepository.existsByContactIgnoreCaseAndIdNot(contact, client.getId());

        if (duplicateContact) {
            throw new Exception("Un client avec ce contact existe déjà");
        }

        return clientRepository.save(client);
    }

    public void delete(Long id_client) throws Exception {
        if (!clientRepository.existsById(id_client)) {
            throw new Exception("Client introuvable");
        }

        if (!venteBovinRepository.findByClient_Id(id_client).isEmpty()) {
            throw new Exception("Suppression impossible : ce client a des ventes associées");
        }

        clientRepository.deleteById(id_client);
    }

    // Recherche paginée avec filtre + recherche multicritère globale
    public Page<Client> searchClients(MultiCriteriaFormClientList form) {
        String sortField = "id";
        Sort.Direction direction = Sort.Direction.ASC;
        if (form.getSort() != null && !form.getSort().isEmpty()) {
            String[] parts = form.getSort().split(",");
            if (parts.length >= 1) {
                sortField = parts[0];
            }
            if (parts.length >= 2) {
                direction = Sort.Direction.fromString(parts[1]);
            }
        }
        Pageable pageable = PageRequest.of(
                form.getPage(),
                form.getSize(),
                Sort.by(direction, sortField)
        );

        return clientRepository.findAll(ClientSpecification.fromForm(form), pageable);
    }
}
