package mg.bovit.release.service;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.data.domain.Sort;
import mg.bovit.release.specification.BovinSpecification;
import mg.bovit.release.repository.*;
import mg.bovit.release.model.*;
import mg.bovit.release.model.sqlview.BovinWithPoids;
import mg.bovit.release.dto.MultiCriteriaFormBovinList;

@Service
public class BovinService {
    @Autowired
    private BovinRepository bovinRepository;
    @Autowired
    private CaisseService caisseService;

    @Autowired
    private BovinWithPoidsRepository bovinWithPoidsRepository;

    @Autowired
    private PeseBovinRepository peseRepository;

    // function to find bovin with status by id bovin
    public BovinWithPoids findBovinPoidsById(Long id_bovin) throws Exception {
        return bovinWithPoidsRepository.findById(id_bovin).orElseThrow();
    }

    // function to find bovin by id
    public Bovin findById(Long id_bovin) throws Exception {
        return bovinRepository.findById(id_bovin).orElseThrow();
    }

    // function to buy bovin
    @Transactional(rollbackFor = Exception.class)
    public void buyBovin(Bovin bovin, List<Caisse> caisses, int quantite) throws Exception {

        if (quantite <= 0) {
            throw new Exception("la quantite ne doit pas être inférieure ou égal à 0");
        }

        // prix total
        Double prix_total = 0.0;

        // rectify caisse and verify if enough
        for (int i = 0; i < caisses.size(); i++) {
            if (caisses.get(i).getMontant_actuelle() <= 0) {
                throw new Exception("le prix total ne doit pas être inférieure ou égal à 0");
            }
            else {
                // find caisse 
                Caisse temp_caisse = caisseService.findById(caisses.get(i).getId());
                // rectify temp_caisse
                temp_caisse.setMontant_actuelle(temp_caisse.getMontant_actuelle() - caisses.get(i).getMontant_actuelle());
                // save in base
                temp_caisse = caisseService.save(temp_caisse);

                if (temp_caisse.getMontant_actuelle() < 0) {
                    throw new Exception("le montant de la caisse est insuffisant");
                }
                
                prix_total = prix_total + caisses.get(i).getMontant_actuelle();
            }
        }

        // insert bovin in base
        Double prix_unitaire = prix_total / quantite;
        for (int i = 0; i < quantite; i++) {
            Bovin newBovin = new Bovin();
            PeseBovin newPeseBovin = new PeseBovin();

            
            newBovin.setRace(bovin.getRace());
            newBovin.setPoids_achat(bovin.getPoids_achat());
            newBovin.setPoids_vente(bovin.getPoids_vente());
            newBovin.setDate_achat(bovin.getDate_achat());
            newBovin.setDate_vente(bovin.getDate_vente());
            newBovin.setPrix_achat(prix_unitaire);
            
            bovinRepository.save(newBovin);

            newPeseBovin.setBovin(newBovin);
            newPeseBovin.setDate_pese(bovin.getDate_achat());
            newPeseBovin.setPoids_apres(bovin.getPoids_achat());

            peseRepository.save(newPeseBovin);
        }
    }

    // Nouvelle méthode de recherche paginée et filtrée (utilisant la vue)
    public Page<BovinWithPoids> searchBovinsWithPoids(MultiCriteriaFormBovinList form) {
        // Construction du tri
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

        return bovinWithPoidsRepository.findAll(BovinSpecification.fromForm(form), pageable);
    }            

    public List<Bovin> findAll() {
        return bovinRepository.findAll();
    }
}
