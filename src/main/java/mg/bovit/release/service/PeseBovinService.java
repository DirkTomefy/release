package mg.bovit.release.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import mg.bovit.release.dto.PeseBovinRequest;   // import ajouté
import mg.bovit.release.dto.MulticriteriaListPeseBovin;
import mg.bovit.release.model.Bovin;
import mg.bovit.release.model.PeseBovin;
import mg.bovit.release.model.sqlview.PeseBovinWithDateVente;
import mg.bovit.release.repository.BovinRepository;
import mg.bovit.release.repository.PeseBovinRepository;
import mg.bovit.release.repository.PeseBovinWIthDateVenteRepository;
import mg.bovit.release.specification.PeseSpecification;

@Service
public class PeseBovinService {

    @Autowired
    private PeseBovinRepository peseBovinRepository;

    @Autowired
    private BovinRepository bovinRepository;

    @Autowired
    private PeseBovinWIthDateVenteRepository peseBovinWIthDateVenteRepository;

    @Autowired
    private BovinService bovinService;   // injecté pour récupérer un bovin

    // ----- Méthodes existantes -----

    public PeseBovin findById(Long id_peseBovin) {
        return peseBovinRepository.findById(id_peseBovin).orElse(null);
    }

    public PeseBovin getLatestPeseByBovin(Long id_bovin) {
        return peseBovinRepository.getLatestPeseByBovin(id_bovin);
    }

    public PeseBovin getOrCreateLatestPeseByBovin(Long id_bovin) {
        PeseBovin latest = getLatestPeseByBovin(id_bovin);
        if (latest != null) {
            return latest;
        }
        Bovin bovin = bovinRepository.findById(id_bovin).orElse(null);
        if (bovin == null) {
            return null;
        }
        PeseBovin newPeseBovin = new PeseBovin();
        newPeseBovin.setBovin(bovin);
        newPeseBovin.setDate_pese(bovin.getDate_achat());
        newPeseBovin.setPoids_apres(bovin.getPoids_achat());
        return peseBovinRepository.save(newPeseBovin);
    }

    // Méthode de sauvegarde simple (conservée pour usage interne)
    public PeseBovin save(PeseBovin peseBovin) {
        return peseBovinRepository.save(peseBovin);
    }

    public List<PeseBovin> findAll() {
        return peseBovinRepository.findAll();
    }

    public Page<PeseBovinWithDateVente> searchPeseBovins(MulticriteriaListPeseBovin form) {
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
        return peseBovinWIthDateVenteRepository.findAll(PeseSpecification.fromForm(form), pageable);
    }

    public List<PeseBovin> findByBovinIdOrderByDatePeseAsc(Long bovinId) {
        return peseBovinRepository.findByBovinIdOrderByDatePeseAsc(bovinId);
    }

    public PeseBovinWithDateVente findViewById(Long id) {
        return peseBovinWIthDateVenteRepository.findById(id).orElse(null);
    }

    public List<PeseBovinWithDateVente> searchAllForExport(MulticriteriaListPeseBovin form) {
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
        return peseBovinWIthDateVenteRepository.findAll(
                PeseSpecification.fromForm(form),
                Sort.by(direction, sortField)
        );
    }

    // ----- Nouvelle méthode avec toute la logique métier -----
    @Transactional
    public PeseBovin createOrUpdatePeseBovin(PeseBovinRequest request) throws Exception {
        // 1. Vérifier que le bovin existe
        Bovin bovin = bovinService.findById(request.getBovinId());
        if (bovin == null) {
            throw new Exception("Bovin introuvable avec l'ID : " + request.getBovinId());
        }

        // 2. Récupérer la dernière pesée du bovin
        PeseBovin latestPese = getLatestPeseByBovin(bovin.getId());

        // 3. Vérifier que la date de la nouvelle pesée est postérieure à la dernière pesée
        if (latestPese != null && latestPese.getDate_pese().after(request.getDatePese())) {
            throw new Exception("La date de pesée doit être après la date de la dernière pesée");
        }

        // 4. Vérifier que le poids est strictement positif
        if (request.getPoids() <= 0) {
            throw new Exception("Le nouveau poids du bovin doit être strictement positif");
        }

        // 5. Construire l'entité à sauvegarder (création ou mise à jour)
        PeseBovin peseBovin;
        if (request.getIdPeseBovin() != null) {
            // Mise à jour : on charge l'existant
            peseBovin = findById(request.getIdPeseBovin());
            if (peseBovin == null) {
                throw new Exception("Pesée introuvable avec l'ID : " + request.getIdPeseBovin());
            }
            // On met à jour les champs
            peseBovin.setBovin(bovin);
            peseBovin.setDate_pese(request.getDatePese());
            peseBovin.setPoids_apres(request.getPoids());
        } else {
            // Création : nouvelle instance
            peseBovin = new PeseBovin();
            peseBovin.setBovin(bovin);
            peseBovin.setDate_pese(request.getDatePese());
            peseBovin.setPoids_apres(request.getPoids());
        }

        // 6. Sauvegarder et retourner l'entité
        return peseBovinRepository.save(peseBovin);
    }
}